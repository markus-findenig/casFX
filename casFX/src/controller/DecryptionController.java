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

import model.ConfigModel;
import model.DecryptionECM;
import model.DecryptionEMM;
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
	 * Encryption ECM Model
	 */
	private static DecryptionEMM decryptionEMM;

	/**
	 * Static ECM length
	 */
	private static int ECM_LENGTH = 94;
	
	/**
	 * Static EMM length
	 */
	private static int EMM_LENGTH = 144;

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
	
	/**
	 * ECM Section Header
	 */
	private static String msgEcmHeader;

	/**
	 * ECM Protocol number. Code that serves to identify processing functions on
	 * the IC card, encryption algorithms, etc.
	 */
	private static String msgEcmProtocol;
	
	/**
	 * ECM Broadcaster group identifier. Code used to identify broadcaster
	 * groups in conditional access system operation. Combined with the work key
	 * identifier, specifies the work.
	 */
	private static String msgEcmBroadcastId;
	
	/**
	 * ECM Work key identifier. Specifies the work key used to encrypt ECM, is
	 * combined with the broadcaster group identifier.
	 */
	private static String msgEcmWorkKeyId;
	
	/**
	 * ECM Control Word (CW), Scrambling key odd.
	 */
	private static String msgEcmCwOdd;
	
	/**
	 *
	 * ECM Control Word (CW), Scrambling key even.
	 */
	private static String msgEcmCwEven;
	
	/**
	 * ECM Program type. Indicates the viewing program type (free, tier, PPV, etc.).
	 */
	private static String msgEcmProgramType;
	
	/**
	 * ECM Date Time. Indicates the current date/time to check authorization of viewing.
	 */
	private static String msgEcmDateTime;
	
	/**
	 * ECM Recording control. Indicate the recording conditions for the program
	 * in question (recordable, not recordable, recordable by subscribers only,
	 * etc.).
	 */
	private static String msgEcmRecordControl;
	
	/**
	 * ECM Payload
	 */
	private static String msgEcmVariablePart;
	
	/**
	 * ECM Message Authentication Code (MAC, 4 Bytes).
	 */
	private static String msgEcmMAC;
	
	/**
	 * ECM Cyclic Redundancy Check (CRC, 4 Bytes).
	 */
	private static String msgEcmCRC;
	
	/**
	 * Master Private Key (256 bit).
	 */
	private static String emmKey;
	
	/**
	 * EMM Section Header
	 */
	private static String msgEmmHeader;
	
	/**
	 * EMM Smartcard ID
	 */
	private static String msgEmmSmartcardId;
	
	/**
	 * EMM Length from Protocol Number to the MAC Field
	 */
	private static String msgEmmLength;
	
	/**
	 * EMM Protocol
	 */
	private static String msgEmmProtocol;
	
	/**
	 * EMM Broadcast Group Identifier
	 */
	private static String msgEmmBroadcastId;
	
	/**
	 * EMM Update number. Number that is increased when individual information is updated.
	 */
	private static String msgEmmUpdateId;
	
	/**
	 * EMM Expiration date. Indicates when individual information expires. 
	 */
	private static String msgEmmExpirationDate;
	
	/**
	 * EMM Payload
	 */
	private static String msgEmmVariablePart;
	
	/**
	 * EMM Message Authentication Code (MAC, 4 Bytes).
	 */
	private static String msgEmmMAC;
	
	/**
	 * EMM Cyclic Redundancy Check (CRC, 4 Bytes).
	 */
	private static String msgEmmCRC;
	
	/**
	 * emmHeader = EMM Section Header + Smartcard id + Length + Protocol number
	 * + Broadcast group id + Update number + Expiration date
	 */
	private static String emmHeader;
	
	
	/**
	 * emmPayload = msgEmmVariablePart
	 */
	private static String emmPayload;
	
	/**
	 * ecmPayloadDecrypted = emmPayload + Payload MAC
	 */
	private static String emmPayloadDecrypted;
	
	/**
	 * emmDecrypted = emmHeader + emmPayloadDecrypted + Section CRC
	 */
	private static String emmDecrypted;

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
		
		decryptionEMM = new DecryptionEMM();

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
	private static void receiveMessage() throws Exception {
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

		while (model.getDecryptionState()) {
			// Wait to receive a datagram
			socket.receive(packet);
			// Convert the contents to a string, and display them
			String msg = new String(buffer, 0, packet.getLength());
			
			System.out.println("KEY 0 : " + model.getAuthorizationOutputKey0());

			// ECM Message, Prüfe ob Keys vorhanden sind
			if (msg.length() == ECM_LENGTH && (!model.getAuthorizationOutputKey0().equals("") || !model.getAuthorizationOutputKey1().equals(""))) {
				receivedECM(msg);
			}
			// EMM Message
			if (msg.length() == EMM_LENGTH) {
				receivedEMM(msg);
			}

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
		
		// set new Authorization Key 0 and 1 from the GUI
		model.setAuthorizationOutputKey0(view.getAk0OutTF().getText());
		model.setAuthorizationOutputKey1(view.getAk1OutTF().getText());

		// get the current Authorization Key
		if (msgEcmWorkKeyId.equals("00")) {
			ecmWorkKey = model.getAuthorizationOutputKey0();
		} else {
			ecmWorkKey = model.getAuthorizationOutputKey1();
		}
		
		// GUI update current Authorization Key 0 and 1
		view.getAk0OutTF().setText(model.getAuthorizationOutputKey0());
		view.getAk1OutTF().setText(model.getAuthorizationOutputKey1());

		// set ECM Header
		ecmHeader = msgEcmHeader + msgEcmProtocol + msgEcmBroadcastId + msgEcmWorkKeyId;

		// set ECM Playload
		ecmPayload = msgEcmCwOdd + msgEcmCwEven + msgEcmProgramType + msgEcmDateTime + msgEcmRecordControl
				+ msgEcmVariablePart;

		// String to Validation
		String validCRC = ecmHeader + ecmPayload + msgEcmMAC;

		// check CRC
		if (!validateCRC(msgEcmCRC, validCRC)) {
			System.err.println("ECM CRC Mismatch!");
			return;
		}

		// Decrypted ECM and separate Payload and MAC
		String decrypted = decryptedECM(ecmPayload + msgEcmMAC);
		ecmPayloadDecrypted = decrypted.substring(0, 56);
		String validMAC = decrypted.substring(56, 64);

		// check MAC
		if (!validateMAC(validMAC, ecmHeader + ecmPayloadDecrypted, "ecm")) {
			System.err.println("ECM MAC Mismatch!");
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

			// GUI updates, Video Player View Button freigeben
			view.getVideoOutputButton().setDisable(false);
			// view decrypted ECM
			view.getEcmDecryptedTA().setText(ecmDecrypted);
			
		} // end if

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
	 *            Aktueller MAC.
	 * @param getMAC
	 *            String zum Überprüfen.
	 * @param msgTyp
	 *            Aktueller Nachrichtentyp.
	 * @return true if MAC Match or false if MAC Fail.
	 */
	private static boolean validateMAC(String validMAC, String getMAC, String msgTyp) {
		SecretKeySpec macKey = null;
		// check message type
		if (msgTyp.equals("ecm")) {
			macKey = new SecretKeySpec(ecmWorkKey.getBytes(), "HmacSHA1");
		} else if (msgTyp.equals("emm")) {
			macKey = new SecretKeySpec(emmKey.getBytes(), "HmacSHA1");
		}
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
	 * @return Gibt die Decryption ECM zurück.
	 */
	public static DecryptionECM getDecryptionECM() {
		return decryptionECM;
	}

	/**
	 * Empfange EMM Nachricht.
	 * 
	 * @param msg
	 *            Aktuelle EMM Nachricht
	 * @throws Exception
	 *             Fehlerhafte EMM Nachricht
	 */
	private static void receivedEMM(String msg) throws Exception {
		// spit msg into substrings
		msgEmmHeader = msg.substring(0, 16);
		msgEmmSmartcardId = msg.substring(16, 28);
		msgEmmLength= msg.substring(28, 30);
		msgEcmProtocol = msg.substring(30, 32);
		msgEcmBroadcastId = msg.substring(32, 34);
		msgEmmUpdateId = msg.substring(34, 36);
		msgEmmExpirationDate = msg.substring(36, 40);
		msgEmmVariablePart = msg.substring(40, 128);
		msgEmmMAC = msg.substring(128, 136);
		msgEmmCRC = msg.substring(136, 144);
				
		// check if EMM is update Authorization Keys, Protocol Type CC
		if (!msgEcmProtocol.equals("CC")) {
			return;
		}

		// set EMM Header
		emmHeader = msgEmmHeader + msgEmmSmartcardId + msgEmmLength + msgEcmProtocol + msgEcmBroadcastId
				+ msgEmmUpdateId + msgEmmExpirationDate;

		// set EMM Playload
		emmPayload = msgEmmVariablePart;

		// String to Validation
		String validCRC = emmHeader + emmPayload + msgEmmMAC;

		// check CRC
		if (!validateCRC(msgEmmCRC, validCRC)) {
			System.err.println("EMM CRC Mismatch!");
			return;
		}
		
		// hohle EMM Key
		emmKey = view.getMpkOutTA().getText().toString();

		// Decrypted ECM and separate Payload and MAC
		String decrypted = decryptedEMM(emmPayload + msgEmmMAC);
		
		System.out.println("decrypted:" + decrypted);
		
		
		emmPayloadDecrypted = decrypted.substring(0, 88);
		String validMAC = decrypted.substring(88, 96);

		// check MAC
		if (!validateMAC(validMAC, emmHeader + emmPayloadDecrypted, "emm")) {
			System.err.println("EMM MAC Mismatch!");
			return;
		}

		emmDecrypted = emmHeader + decrypted + msgEmmCRC;
		// set new Authorization Key 0 and 1
		model.setAuthorizationOutputKey0(decrypted.substring(0, 32));
		model.setAuthorizationOutputKey1(decrypted.substring(32, 64));
		
		// save valid EMM
		decryptionEMM.setEmmHeader(msgEmmHeader);
		decryptionEMM.setEmmSmartcardId(msgEmmSmartcardId);
		decryptionEMM.setEmmLength(msgEmmLength);
		decryptionEMM.setEmmProtocol(msgEmmProtocol);
		decryptionEMM.setEmmBroadcastId(msgEmmBroadcastId);
		decryptionEMM.setEmmExpirationDate(msgEmmExpirationDate);
		decryptionEMM.setEmmVariablePart(decrypted);
		decryptionEMM.setEmmMAC(validMAC);
		decryptionEMM.setEmmCRC(msgEmmCRC);
		
		// GUI Updates
		view.getAk0OutTF().setText(model.getAuthorizationOutputKey0());
		view.getAk1OutTF().setText(model.getAuthorizationOutputKey1());
		view.getEmmDecryptedTA().setText(emmDecrypted);
		
	}
	
	/**
	 * Entschlüsselt die EMM Nachricht (emmPayload + Payload MAC).
	 * 
	 * @param emm
	 *           Nachricht zum Entschlüsseln.
	 * @return Entschlüsselte EMM Nachricht.
	 */
	private static String decryptedEMM(String ecm) {
		// generate the decrypted key with the current Authorization Key
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(emmKey), "AES");
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

}
