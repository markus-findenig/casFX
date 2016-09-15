package controller;

import java.io.IOException;
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
 * Decryption Controller. Controls the decryption.
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
	private static int EMM_LENGTH = 146;

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
	 * ECM Program type. Indicates the viewing program type (free, tier, PPV,
	 * etc.).
	 */
	private static String msgEcmProgramType;

	/**
	 * ECM Date Time. Indicates the current date/time to check authorization of
	 * viewing.
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
	 * EMM Update number. Number that is increased when individual information
	 * is updated.
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
	 * Thread for Receive Message.
	 */
	private static Thread thReceiveMessage;

	/**
	 * Error Message.
	 */
	private static String errorMessage;

	/**
	 * Run the decryption.
	 */
	public static void runDecryption() {
		model = SimulatorViewController.getModel();
		view = SimulatorViewController.getView();
		configModel = ConfigViewController.getConfigModel();

		view.getDecryption().setText("ON");
		model.setDecryptionState(true);

		decryptionECM = new DecryptionECM();
		decryptionEMM = new DecryptionEMM();
		errorMessage = new String();

		OutputPlayerController.initOutputPlayer();

		model.setAuthorizationOutputKey0(view.getAk0OutTF().getText());
		model.setAuthorizationOutputKey1(view.getAk1OutTF().getText());

		// init Date/Time
		decryptionECM.setEcmDateTime("0");

		Runnable runReceiveMessage = new Runnable() {
			@Override
			public void run() {
				receiveMessage();
			}
		};

		// start the task
		thReceiveMessage = new Thread(runReceiveMessage);
		thReceiveMessage.setDaemon(true);
		thReceiveMessage.start();

	}

	/**
	 * Stop the decryption.
	 */
	@SuppressWarnings("deprecation")
	public static void stopDecryption() {
		thReceiveMessage.stop();
		view = SimulatorViewController.getView();
		view.getDecryption().setSelected(false);
		view.getDecryption().setText("OFF");
		model.setDecryptionState(false);
		view.getVideoOutputButton().setDisable(true);

		view.getCwOutTF().setText("-- WAIT FOR ECM/EMM --");
		OutputPlayerController.stopOutputPlayer();

	}

	/**
	 * Receives the broadcast messages. Directs messages based on their type
	 * (ECM / EMM) continues.
	 */
	private static void receiveMessage() {
		model = SimulatorViewController.getModel();
		view = SimulatorViewController.getView();
		configModel = ConfigViewController.getConfigModel();

		// default server = rtp://239.0.0.1:5004
		String client = configModel.getClient();
		String[] rtpSplit = client.split("://");
		// rtp = rtpSplit[0]
		String ipPort = rtpSplit[1];
		String[] ip = ipPort.split(":");

		MulticastSocket clientSocket = null;
		InetAddress group = null;
		try {
			// port = 5005
			int port = Integer.parseInt(ip[1].trim()) + 1;
			clientSocket = new MulticastSocket(port);
			// group = 239.0.0.1
			group = InetAddress.getByName(ip[0].trim());
			clientSocket.joinGroup(group);
			byte[] buffer = new byte[2048];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			while (model.isDecryptionState()) {
				// Wait to receive a datagram
				clientSocket.receive(packet);
				// Convert the contents to a string, and display them
				String msg = new String(buffer, 0, packet.getLength());

				// ECM Message, Prüfe ob Keys vorhanden sind
				if (msg.length() == ECM_LENGTH && (!model.getAuthorizationOutputKey0().equals("")
						|| !model.getAuthorizationOutputKey1().equals(""))) {
					receivedECM(msg);
				}
				// EMM Message
				if (msg.length() == EMM_LENGTH) {
					receivedEMM(msg);
				}

				// Reset the length of the packet before reusing it.
				packet.setLength(buffer.length);
			}

		} catch (Exception e) {
			try {
				clientSocket.leaveGroup(group);
				clientSocket.close();
			} catch (IOException e1) {
				view.getErrorOutputTA().setText("Receive Message, " + e1.getMessage());
			}
			view.getErrorOutputTA().setText("Receive Message, " + e.getMessage());
		}
	}

	/**
	 * Receive ECM message.
	 * 
	 * @param msg
	 *            Current ECM message.
	 */
	private static void receivedECM(String msg) {
		// reset Error Messages
		view.getErrorOutputTA().setText("");
		errorMessage = "";

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

		// get the current Authorization Key from the GUI
		if (msgEcmWorkKeyId.equals("00")) {
			ecmWorkKey = view.getAk0OutTF().getText();
		} else {
			ecmWorkKey = view.getAk1OutTF().getText();
		}

		// set ECM Header
		ecmHeader = msgEcmHeader + msgEcmProtocol + msgEcmBroadcastId + msgEcmWorkKeyId;

		// set ECM Playload
		ecmPayload = msgEcmCwOdd + msgEcmCwEven + msgEcmProgramType + msgEcmDateTime + msgEcmRecordControl
				+ msgEcmVariablePart;

		// String to Validation
		String message = ecmHeader + ecmPayload + msgEcmMAC;

		// check CRC
		if (!validateCRC(message, msgEcmCRC)) {
			errorMessage = errorMessage.concat("ECM CRC Mismatch! \n");
		}

		// Decrypted ECM and separate Payload and MAC
		String decrypted = decryptedMessage(ecmPayload + msgEcmMAC, ecmWorkKey);
		ecmPayloadDecrypted = decrypted.substring(0, 56);
		String validMAC = decrypted.substring(56, 64);

		// check MAC
		if (!validateMAC(ecmHeader + ecmPayloadDecrypted, validMAC, ecmWorkKey)) {
			errorMessage = errorMessage.concat("ECM MAC Mismatch! \n");
		}

		ecmDecrypted = ecmHeader + decrypted + msgEcmCRC;

		// check Date/Time save only new ecm
		// String ecmDateTime = ecmPayloadDecrypted.substring(34, 44);
		// if (Integer.parseInt(ecmDateTime.trim()) >
		// Integer.parseInt(decryptionECM.getEcmDateTime().trim())) {

		// Save each ECM, whether it is correct or incorrect
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

		// GUI updates, Video Player View Button freigeben
		view.getVideoOutputButton().setDisable(false);
		// view decrypted ECM
		view.getEcmDecryptedTA().setText(ecmDecrypted);
		view.getErrorOutputTA().setText(errorMessage);

		// } // end if

	}

	/**
	 * Checks the Cyclic Redundancy Check (CRC) of the message {@link message}
	 * with {@link crc}.
	 * 
	 * @param message
	 *            Current Message.
	 * @param crc
	 *            The CRC to valid.
	 * @return true if CRC Match or false if CRC Fail.
	 */
	private static boolean validateCRC(String message, String crc) {
		java.util.zip.CRC32 x = new java.util.zip.CRC32();
		byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
		x.update(bytes);
		String check = String.format("%02X", x.getValue());

		// check crc with message
		if (check.equals(crc)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Decrypts the message {@link message} by the key {@link key}.
	 * 
	 * @param message
	 *            Message to decrypt.
	 * @param key
	 *            Key to decrypt.
	 * @return Entschlüsselte Nachricht.
	 */
	private static String decryptedMessage(String message, String key) {
		byte[] result = null;
		try {
			// generate the key
			SecretKey secretKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			result = cipher.doFinal(DatatypeConverter.parseHexBinary(message));
		} catch (Exception e) {
			errorMessage = errorMessage.concat("Decrypted ECM " + e.getMessage() + "\n");
		}
		return DatatypeConverter.printHexBinary(result);
	}

	/**
	 * Checks the Message Authentication Code (MAC) {@link mac} of the message
	 * {@link message} with {@link key}.
	 * 
	 * @param message
	 *            The Message.
	 * @param mac
	 *            The MAC to check.
	 * @param key
	 *            The Key for MAC.
	 * @return true if MAC Match or false if MAC Fail.
	 */
	private static boolean validateMAC(String message, String mac, String key) {
		// generate the MAC from the message
		String macMessage = getMAC(message, key);
		// check MAC
		if (macMessage.equals(mac)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates a Message Authentication Code (MAC, 4 byte length) in Hex Number.
	 * 
	 * @param message
	 *            Message for the MAC.
	 * @param key
	 *            Key for MAC
	 * @return The MAC from Message.
	 */
	private static String getMAC(String message, String key) {
		// generate a mac key
		SecretKeySpec macKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");

		String macString = null;
		// ALG_DES_MAC4_ISO9797_M1 for SmartCards
		try {
			Mac mac = Mac.getInstance(macKey.getAlgorithm());
			mac.init(macKey);

			// get the string as UTF-8 bytes
			byte[] b = message.toString().getBytes(Charset.forName("UTF-8"));
			// create a digest from the byte array
			byte[] digest = mac.doFinal(b);

			// cut first 4 bytes in hex
			macString = String.valueOf(Hex.encodeHex(digest)).substring(0, 8).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			errorMessage = errorMessage.concat("No Such Algorithm:" + e.getMessage() + "\n");
		} catch (InvalidKeyException e) {
			errorMessage = errorMessage.concat("Invalid Key:" + e.getMessage() + "\n");
		}
		return macString;
	}

	/**
	 * Returns the Decryption ECM {@link decryptionECM}.
	 * 
	 * @return The decryption ECM.
	 */
	public static DecryptionECM getDecryptionECM() {
		return decryptionECM;
	}

	/**
	 * Receive EMM message.
	 * 
	 * @param msg
	 *            Current EMM message.
	 */
	private static void receivedEMM(String msg) {
		// reset Error Messages
		view.getErrorOutputTA().setText("");
		errorMessage = "";

		// spit msg into substrings
		msgEmmHeader = msg.substring(0, 16);
		msgEmmSmartcardId = msg.substring(16, 28);
		msgEmmLength = msg.substring(28, 30);
		msgEcmProtocol = msg.substring(30, 32);
		msgEcmBroadcastId = msg.substring(32, 34);
		msgEmmUpdateId = msg.substring(34, 38);
		msgEmmExpirationDate = msg.substring(38, 42);
		msgEmmVariablePart = msg.substring(42, 130);
		msgEmmMAC = msg.substring(130, 138);
		msgEmmCRC = msg.substring(138, 146);

		// check if EMM is update Authorization Keys, Protocol Type CC
		if (!msgEcmProtocol.equals("CC")) {
			errorMessage = errorMessage.concat("EMM not Valid! \n");
		}

		// set EMM Header
		emmHeader = msgEmmHeader + msgEmmSmartcardId + msgEmmLength + msgEcmProtocol + msgEcmBroadcastId
				+ msgEmmUpdateId + msgEmmExpirationDate;

		// set EMM Playload
		emmPayload = msgEmmVariablePart;

		// String to Validation
		String message = emmHeader + emmPayload + msgEmmMAC;

		// check CRC
		if (!validateCRC(message, msgEmmCRC)) {
			errorMessage = errorMessage.concat("EMM CRC Mismatch! \n");
		}

		// hohle EMM Key
		emmKey = view.getMpkOutTA().getText().toString();

		// Decrypted ECM and separate Payload and MAC
		String decrypted = decryptedMessage(emmPayload + msgEmmMAC, emmKey);
		emmPayloadDecrypted = decrypted.substring(0, 88);
		String validMAC = decrypted.substring(88, 96);

		// check MAC
		if (!validateMAC(emmHeader + emmPayloadDecrypted, validMAC, emmKey)) {
			errorMessage = errorMessage.concat("EMM MAC Mismatch! \n");
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
		view.getCwOutTF().setText("-- WAIT FOR ECM --");
		view.getErrorOutputTA().setText(errorMessage);

	}

}
