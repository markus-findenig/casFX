package controller;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

import javafx.application.Platform;
import model.ConfigModel;
import model.DecryptionECM;
import model.SimulatorModel;
import view.SimulatorView;

/**
 * 
 * Decryption Controller. Steuert die Entschlüsselung.
 *
 */
public class DecryptionController {

	/**
	 * Simulator Model
	 */
	private static SimulatorModel model;

	/**
	 * Simulator View
	 */
	private static SimulatorView view;

	/**
	 * Config Model
	 */
	private static ConfigModel configModel;

	/**
	 * Encryption ECM Model
	 */
	private static DecryptionECM decryptionECM;

	/**
	 * Static ECM length
	 */
	private static int ECM_LENGTH = 94;

	/**
	 * ecmHeader = ECM Section Header + Protocol number + Broadcast group id +
	 * AK id
	 */
	private static String ecmHeader;

	/**
	 * ecmPayload = CW (odd) + CW (even) + Program type + Date/Time + Recording
	 * control + Variable part
	 */
	private static String ecmPayload;

	/**
	 * ecmPayloadDecrypted = ecmPayload + Payload MAC
	 */
	private static String ecmPayloadDecrypted;

	/**
	 * ecm = ecmHeader + ecmPayload + MAC + Section CRC
	 */
	private static String ecm;

	/**
	 * ecmDecrypted = ecmHeader + ecmPayloadDecrypted + Section CRC
	 */
	private static String ecmDecrypted;

	/**
	 * Aktueller Authorization Key
	 */
	private static String ecmWorkKey;

	// input ECM Message
	private static String msgEcmHeader;
	private static String msgEcmProtocol;
	private static String msgEcmBroadcastId;
	private static String msgEcmWorkKeyId;
	private static String msgEcmCwOdd;
	private static String msgEcmCwEven;
	private static String msgEcmProgramType;
	private static String msgEcmDateTime;
	private static String msgEcmRecordControl;
	private static String msgEcmVariablePart;
	private static String msgEcmMAC;
	private static String msgEcmCRC;

	/**
	 * Startet die Entschlüsselung.
	 */
	public static void runDecryption() {
		model = SimulatorViewController.getModel();
		view = SimulatorViewController.getView();
		configModel = ConfigViewController.getConfigModel();
		
		view.getDecryption().setText("ON");
		model.setDecryptionState(true);

		decryptionECM = new DecryptionECM();

		OutputPlayerController.initOutputPlayer();

		model.setAuthorizationOutputKey0(view.getAk0OutTF().getText());
		model.setAuthorizationOutputKey1(view.getAk1OutTF().getText());

		// init Date/Time
		decryptionECM.setEcmDateTime("0");

		// GUI update
		view.getCwOutTF().setText("-- WAIT FOR ECM --");

		Runnable runReceiveMessage = new Runnable() {
			@Override
			public void run() {
				try {
					receiveMessage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		// start the task
		Thread thReceiveMessage = new Thread(runReceiveMessage);
		thReceiveMessage.setDaemon(true);
		thReceiveMessage.start();

	}

	/**
	 * Stoppt die Entschlüsselung.
	 */
	public static void stopDecryption() {
		view.getDecryption().setText("OFF");
		model.setDecryptionState(false);
		view.getVideoOutputButton().setDisable(true);
		
		view.getCwOutTF().setText("-- WAIT FOR ECM --");
		OutputPlayerController.stopOutputPlayer();

	}

	/**
	 * Empfängt die Broadcast Nachrichten. Leitet die Nachrichten anhand ihres Typs (ECM/EMM) weiter.
	 * @throws Exception - Fehler beim erstellen des Sockets.
	 */
	public static void receiveMessage() throws Exception {
		// default server = rtp://239.0.0.1:5004
		String client = configModel.getClient();
		String[] rtpSplit = client.split("://");
		// rtp = rtpSplit[0]
		String ipPort = rtpSplit[1];
		String[] ip = ipPort.split(":");

		// port = 5005
		MulticastSocket socket = new MulticastSocket(Integer.parseInt(ip[1].trim()) + 1);
		// group = 239.0.0.1
		InetAddress group = InetAddress.getByName(ip[0].trim());
		socket.joinGroup(group);

		byte[] buffer = new byte[2048];

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		// System.err.println("while :" +
		// PlayerViewController.embeddedMediaPlayer.isPlaying());

		while (model.getDecryptionState()) {
			// Wait to receive a datagram
			socket.receive(packet);

			// Convert the contents to a string, and display them
			String msg = new String(buffer, 0, packet.getLength());
			// System.out.println(msg);
			// System.out.println(packet.getAddress().getHostName() + ": " +
			// msg);

			if (msg.length() == ECM_LENGTH) {
				receivedECM(msg);
			}

			// TODO EMM Length

			// Reset the length of the packet before reusing it.
			packet.setLength(buffer.length);
		}

		socket.leaveGroup(group);
		socket.close();
	}

	/**
	 * Empfange ECM Nachricht.
	 * 
	 * @param msg
	 *            - Aktuelle ECM Nachricht
	 * @throws Exception
	 *             - Fehlerhafte ECM Nachricht
	 */
	private static void receivedECM(String msg) throws Exception {
		// spit msg into substrings
		msgEcmHeader = msg.substring(0, 16);
		msgEcmProtocol = msg.substring(16, 18);
		msgEcmBroadcastId = msg.substring(18, 20);
		msgEcmWorkKeyId = msg.substring(20, 22);
		msgEcmCwOdd = msg.substring(22, 38);
		msgEcmCwEven = msg.substring(38, 54);
		msgEcmProgramType = msg.substring(54, 56);
		msgEcmDateTime = msg.substring(56, 66);
		msgEcmRecordControl = msg.substring(66, 68);
		msgEcmVariablePart = msg.substring(68, 78);
		msgEcmMAC = msg.substring(78, 86);
		msgEcmCRC = msg.substring(86, 94);

		// System.out.println("msgHeader : " + msgEcmHeader);
		// System.out.println("msgProtocol : " + msgEcmProtocol);
		// System.out.println("msgBroadcastId : " + msgEcmBroadcastId);
		// System.out.println("msgWorkKeyId : " + msgEcmWorkKeyId);
		// System.out.println("msgCwOdd : " + msgEcmCwOdd);
		// System.out.println("msgCwEven : " + msgEcmCwEven);
		// System.out.println("msgProgramType : " + msgEcmProgramType);
		// System.out.println("msgDateTime : " + msgEcmDateTime);
		// System.out.println("msgRecordControl : " + msgEcmRecordControl);
		// System.out.println("msgVariablePart : " + msgEcmVariablePart);
		// System.out.println("msgMAC : " + msgEcmMAC);
		// System.out.println("msgCRC : " + msgEcmCRC);

		// get the current Authorization Key
		if (msgEcmWorkKeyId.equals("00")) {
			ecmWorkKey = model.getAuthorizationOutputKey0();
			// ecmWorkKey = "465284AA69A329782CA898EB3701F546";
		} else {
			ecmWorkKey = model.getAuthorizationOutputKey1();
			// ecmWorkKey = "F7577079D48B3D5ECAF3E53FDCCDFDFE";
		}

		// set ECM Header
		ecmHeader = msgEcmHeader + msgEcmProtocol + msgEcmBroadcastId + msgEcmWorkKeyId;

		// set ECM Playload
		ecmPayload = msgEcmCwOdd + msgEcmCwEven + msgEcmProgramType + msgEcmDateTime + msgEcmRecordControl
				+ msgEcmVariablePart;

		// String to Validation
		String validCRC = ecmHeader + ecmPayload + msgEcmMAC;

		// check CRC
		if (!validateCRC(msgEcmCRC, validCRC)) {
			System.err.println("CRC Mismatch");
			return;
		}

		// Decrypted ECM and separate Payload and MAC
		String decrypted = decryptedECM(ecmPayload + msgEcmMAC);
		ecmPayloadDecrypted = decrypted.substring(0, 56);
		String validMAC = decrypted.substring(56, 64);

		// check MAC
		if (!validateMAC(validMAC, ecmHeader + ecmPayloadDecrypted)) {
			System.err.println("MAC Mismatch");
			return;
		}

		ecmDecrypted = ecmHeader + decrypted + msgEcmCRC;

		// check Date/Time save only new ecm
		String ecmDateTime = ecmPayloadDecrypted.substring(34, 44);
		if (Integer.parseInt(ecmDateTime.trim()) > Integer.parseInt(decryptionECM.getEcmDateTime().trim())) {

			System.out.println("SAVE ECM : ");
			// save valid ecm
			decryptionECM.setEcmHeader(msgEcmHeader);
			decryptionECM.setEcmProtocol(msgEcmProtocol);
			decryptionECM.setEcmBroadcastId(msgEcmBroadcastId);
			decryptionECM.setEcmWorkKeyId(msgEcmWorkKeyId);
			decryptionECM.setEcmCwOdd(ecmPayloadDecrypted.substring(0, 16));
			decryptionECM.setEcmCwEven(ecmPayloadDecrypted.substring(16, 32));
			decryptionECM.setEcmProgramType(ecmPayloadDecrypted.substring(32, 34));
			decryptionECM.setEcmDateTime(ecmPayloadDecrypted.substring(34, 44));
			decryptionECM.setEcmRecordControl(ecmPayloadDecrypted.substring(44, 46));
			decryptionECM.setEcmVariablePart(ecmPayloadDecrypted.substring(46, 56));
			decryptionECM.setEcmMAC(validMAC);
			decryptionECM.setEcmCRC(msgEcmCRC);

			// TODO del
			System.out.println("msgHeader : " + msgEcmHeader);
			System.out.println("msgProtocol : " + msgEcmProtocol);
			System.out.println("msgBroadcastId : " + msgEcmBroadcastId);
			System.out.println("msgWorkKeyId : " + msgEcmWorkKeyId);
			System.out.println("msgCwOdd : " + ecmPayloadDecrypted.substring(0, 16));
			System.out.println("msgCwEven : " + ecmPayloadDecrypted.substring(16, 32));
			System.out.println("msgProgramType : " + ecmPayloadDecrypted.substring(32, 34));
			System.out.println("msgDateTime : " + ecmPayloadDecrypted.substring(34, 44));
			System.out.println("msgRecordControl : " + ecmPayloadDecrypted.substring(44, 46));
			System.out.println("msgVariablePart : " + ecmPayloadDecrypted.substring(46, 56));
			System.out.println("msgMAC : " + validMAC);
			System.out.println("msgCRC : " + msgEcmCRC);

			// TODO
			// update GUI decrypted ecm
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// Ouput Player View Button freigeben
					view.getVideoOutputButton().setDisable(false);

					// TODO
//					// if cw is odd = 8000000000000000
//					if (decryptionECM.getEcmHeader().equals("8100000000000000")) {
//						view.getCwOutTF().setText(decryptionECM.getEcmCwOdd());
//					}
//					// if cw is even = 8100000000000000
//					else {
//						view.getCwOutTF().setText(decryptionECM.getEcmCwEven());
//					}
					view.getEcmDecryptedTA().setText(ecmDecrypted);
				}
			});

		}

	}

	/**
	 * Überprüft den Cyclic Redundancy Check (CRC) anhand des Eingabe
	 * Strings {@link validCRC} mit {@link msgEcmCrc}.
	 * 
	 * @param msgEcmCrc
	 *            - Aktueller CRC.
	 * @param validCRC
	 *            - String zum Validieren.
	 * @return true if CRC Match or false if CRC Fail
	 */
	private static boolean validateCRC(String msgEcmCrc, String validCRC) {
		java.util.zip.CRC32 x = new java.util.zip.CRC32();
		byte[] bytes = validCRC.getBytes(Charset.forName("UTF-8"));
		x.update(bytes);
		String check = String.format("%02X", x.getValue());

		// check crc with msgEcmCrc
		if (check.equals(msgEcmCrc)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Entschlüsselt die ECM Nachricht (ecmPayload + Payload MAC).
	 * 
	 * @param ecm
	 *            - Nachricht zum Entschlüsseln.
	 * @return Entschlüsselte ECM Nachricht.
	 */
	private static String decryptedECM(String ecm) {
		// generate the decrypted key with the current Authorization Key
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(ecmWorkKey), "AES");
		Cipher cipher;
		byte[] result = null;

		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			result = cipher.doFinal(DatatypeConverter.parseHexBinary(ecm));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return DatatypeConverter.printHexBinary(result);
	}

	/**
	 * Überprüft die ECM (Header + Payload) ob der Message Authentication Code
	 * (MAC) gültig ist.
	 * 
	 * @param validMAC
	 *            - Aktueller MAC.
	 * @param getMAC
	 *            - String zum Überprüfen.
	 * @return true if MAC Match or false if MAC Fail.
	 */
	private static boolean validateMAC(String validMAC, String getMAC) {
		// generate a key
		SecretKeySpec macKey = new SecretKeySpec(ecmWorkKey.getBytes(), "HmacSHA1");
		String macString = null;
		// ALG_DES_MAC4_ISO9797_M1 for SmartCards
		try {
			Mac mac = Mac.getInstance(macKey.getAlgorithm());
			mac.init(macKey);
			// get the string as UTF-8 bytes
			byte[] b = getMAC.toString().getBytes(Charset.forName("UTF-8"));
			// create a digest from the byte array
			byte[] digest = mac.doFinal(b);
			// cut first 4 bytes in hex
			macString = String.valueOf(Hex.encodeHex(digest)).substring(0, 8).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No Such Algorithm:" + e.getMessage());
		} catch (InvalidKeyException e) {
			System.out.println("Invalid Key:" + e.getMessage());
		}
		// check MAC
		if (validMAC.equals(macString)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Liefert die Decryption ECM {@link decryptionECM}.
	 * 
	 * @return - Gibt die Decryption ECM zurück.
	 */
	public static DecryptionECM getDecryptionECM() {
		return decryptionECM;
	}

}
