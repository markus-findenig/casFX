package controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

import javafx.application.Platform;
import model.ConfigModel;
import model.EncryptionECM;
import model.EncryptionEMM;
import model.SimulatorModel;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import view.SimulatorView;

/**
 * Encryption Controller. Controls the encryption.
 */
public class EncryptionController {

	/**
	 * Simulator View
	 */
	private static SimulatorView view;

	/**
	 * Simulator Model
	 */
	private static SimulatorModel model;

	/**
	 * Config Model
	 */
	private static ConfigModel configModel;

	/**
	 * Encryption ECM Model
	 */
	private static EncryptionECM encryptionECM;

	/**
	 * Scheduled Executor Service. Sends statically every 2 seconds the current
	 * ECM message.
	 */
	private static ScheduledExecutorService sendECMExecutor;

	/**
	 * Current ECM message status.
	 * 
	 * @true odd ECM.
	 * @false even ECM.
	 */
	private static Boolean stateECMType;

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
	 * ecmPayloadEncrypted = ecmPayload + MAC
	 */
	private static String ecmPayloadEncrypted;

	/**
	 * ecm = ecmHeader + ecmPayload + MAC + Section CRC
	 */
	private static String ecm;

	/**
	 * ecmEncrypted = ecmHeader + ecmPayloadEncrypted + Section CRC
	 */
	private static String ecmEncrypted;

	/**
	 * Aktueller Autorisation Key
	 */
	private static String ecmWorkKey;

	/**
	 * Encryption EMM Model
	 */
	private static EncryptionEMM encryptionEMM;

	/**
	 * emmHeader = EMM Section Header + Smartcard id + Length + Protocol Number
	 * + Broadcast group id + Update id + Expiration Date
	 */
	private static String emmHeader;

	/**
	 * emmPayload = Authorization Key 0 + Authorization Key 1
	 */
	private static String emmPayload;

	/**
	 * emmEncrypted = emmHeader + emmPayloadEncrypted + Section CRC
	 */
	private static String emmEncrypted;

	/**
	 * Master Private Key (256 bit).
	 */
	private static String emmKey;

	/**
	 * emmPayloadEncrypted = emmPayload + MAC
	 */
	private static String emmPayloadEncrypted;

	/**
	 * emm = emmHeader + emmPayload + MAC + Section CRC
	 */
	private static String emm;

	/**
	 * Run the Encryption.
	 */
	public static void runEncryption() {
		model = SimulatorViewController.getModel();
		view = SimulatorViewController.getView();
		configModel = ConfigViewController.getConfigModel();

		// Button update status
		view.getEncryption().setText("ON");
		// View Video Button freigeben
		view.getVideoInputButton().setDisable(false);
		model.setEncryptionState(true);

		encryptionECM = new EncryptionECM();

		// erster status odd
		setStateECMType(true);

		// first init parameter
		FFmpegController.initFFmpegController();
		InputPlayerController.initInputPlayer();

		// Time (sec), hole die Zeit vom Timer Eingabefeld
		model.setCwTime(Integer.parseInt(view.getCwTimeTF().getText().toString()));

		// sperre die eingabe der CW Time
		view.getCwTimeTF().setDisable(true);

		// init first cw's
		encryptionECM.setEcmCwOdd(view.getEcmCwOddTF().getText());
		encryptionECM.setEcmCwEven(view.getEcmCwEvenTF().getText());

		// init Authorization Keys
		model.setAuthorizationInputKey0(view.getAk0InTF().getText());
		model.setAuthorizationInputKey1(view.getAk1InTF().getText());

		// run Intervall Encryption
		// 1. cut file odd
		// 2. generate ECM odd
		// 3. vlc stream odd
		// 4. cut file even
		// 5. generate ECM even
		// 6. vlc stream even
		// 7. goto 1

		// Constant CW
		if (model.getCwTime() == 0) {
			constantECM();
			InputPlayerController.streamInputPlayerRTP(model.getInputFile().toString());
		}
		// Intervall CW
		else {
			// Initialisiere den Intervall Input Player
			InputPlayerController.initIntervallInputPlayer();
			// schneide Video Datei
			FFmpegController.runFFmpeg();
			try {
				// Warte, für die erste Ausführung auf FFmpeg
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			generateECM();
			sendECM();
			InputPlayerController.runInputPlayer();

		} // end else

		// send ECM
		Runnable sendECMRunnable = new Runnable() {
			@Override
			public void run() {
				sendECM();
			}
		};

		sendECMExecutor = Executors.newScheduledThreadPool(1);
		// ECM, statisch alle 2 Sekunden
		sendECMExecutor.scheduleWithFixedDelay(sendECMRunnable, 1, 2, TimeUnit.SECONDS);

	}

	/**
	 * Stop the Encryption.
	 */
	public static void stopEncryption() {
		// view.getEncryption().setSelected(false);
		view.getEncryption().setText("OFF");
		view.getVideoInputButton().setDisable(true);
		model.setEncryptionState(false);
		// no scrambling
		model.setScramblingControl("00");
		view.getScramblingControl().setText("00");
		// Entsperre die CW Time
		view.getCwTimeTF().setDisable(false);
		// VLC reset
		view.getParameterVLCstream().setText("");
		InputPlayerController.stopInputPlayer();
		sendECMExecutor.shutdownNow();

	}

	/**
	 * Creates a new ECM message with protocol type "AA".
	 */
	public static void generateECM() {
		MediaPlayerFactory factory = new MediaPlayerFactory();
		HeadlessMediaPlayer mp = factory.newHeadlessMediaPlayer();

		String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
		LocalDateTime dateTime;
		// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

		// erzeuge ein cw
		String cw = getRandomHex(16);
		// setzte das CW im Input Player
		model.setControlWordInput(cw);

		String ecmType;
		if (isStateECMType()) {
			ecmType = "80";
			mp.prepareMedia(InputPlayerController.getInFileOdd());
		} else {
			ecmType = "81";
			mp.prepareMedia(InputPlayerController.getInFileEven());
		}

		// ECM Section Header
		// 0x80 odd, 0x81 even
		encryptionECM.setEcmHeader(ecmType + "00000000000000");

		// ECM Protocol number
		encryptionECM.setEcmProtocol("AA");

		// ECM Broadcast group id
		encryptionECM.setEcmBroadcastId("FF");

		// ECM AK id
		if (rbStatus == "00") {
			encryptionECM.setEcmWorkKeyId("00");
		} else {
			encryptionECM.setEcmWorkKeyId("01");
		}

		// ECM Scrambling Control Pointer for CW odd
		if (isStateECMType()) {
			encryptionECM.setEcmCwOdd(cw);
			// encryptionECM.setEcmCwEven("0000000000000000");
		} else {
			// encryptionECM.setEcmCwOdd("0000000000000000");
			encryptionECM.setEcmCwEven(cw);
		}

		// ECM Program type
		encryptionECM.setEcmProgramType("C8");

		// Parse die Media Datei
		mp.parseMedia();
		// ECM Date/Time setzen
		int durationInSeconds = (int) (mp.getMediaMeta().getLength() / 1000);
		System.out.println("durationInSeconds : " + durationInSeconds);
		// aktuelle Zeit plus file duration Länge
		dateTime = LocalDateTime.now().plusSeconds(durationInSeconds);
		encryptionECM.setEcmDateTime(dateTime.format(formatter));

		// Record control
		encryptionECM.setEcmRecordControl("D5");

		// Variable part
		// CW Time
		encryptionECM.setEcmVariablePart(String.format("%010d", model.getCwTime()));

		// setze ECM MAC
		encryptionECM.setEcmMAC(generateEcmMAC());

		// setze ECM CRC
		encryptionECM.setEcmCRC(getEcmCRC());

		// odd/even Scrambling Control
		if (isStateECMType()) {
			model.setScramblingControl("11");
		} else {
			model.setScramblingControl("10");
		}

		// GUI updaten
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				guiECMUpdate();
			}
		});

	}

	/**
	 * Creates a constant ECM with protocol type "BB".
	 */
	private static void constantECM() {
		String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
		LocalDateTime dateTime;
		// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

		// hohle constant cw in der config
		String cw = configModel.getConstantCw();
		// setzte das CW im Input Player
		model.setControlWordInput(cw);

		// ECM Section Header
		// 0x80 odd, 0x81 even
		encryptionECM.setEcmHeader("8000000000000000");

		// ECM Protocol number (BB for constantCW)
		encryptionECM.setEcmProtocol("BB");

		// ECM Broadcast group id
		encryptionECM.setEcmBroadcastId("FF");

		// ECM AK id
		if (rbStatus == "00") {
			encryptionECM.setEcmWorkKeyId("00");
		} else {
			encryptionECM.setEcmWorkKeyId("01");
		}

		// set cw for odd and even
		encryptionECM.setEcmCwOdd(cw);
		encryptionECM.setEcmCwEven(cw);

		// ECM Program type
		encryptionECM.setEcmProgramType("C8");

		// ECM Date/Time setzen
		dateTime = LocalDateTime.now();
		encryptionECM.setEcmDateTime(dateTime.format(formatter));

		// Record control
		encryptionECM.setEcmRecordControl("D5");

		// Variable part
		// CW Time
		encryptionECM.setEcmVariablePart(String.format("%010d", model.getCwTime()));

		// setze ECM MAC
		encryptionECM.setEcmMAC(generateEcmMAC());

		// setze ECM CRC
		encryptionECM.setEcmCRC(getEcmCRC());

		// TS Scrambling Control auf odd
		model.setScramblingControl("11");

		// View Constant VLC input Stream Parameter
		view.getParameterVLCstream().setText("vlc " + configModel.getServer().toString() + "\n --ts-csa-ck=" + cw);

		// GUI updaten
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				guiECMUpdate();
			}
		});

	}

	/**
	 * Updates the parameters of the simulator GUI elements.
	 */
	private static void guiECMUpdate() {
		// Input Player
		if (isStateECMType()) {
			view.getCwInTF().setText("odd:" + model.getControlWordInput());
		} else {
			view.getCwInTF().setText("even:" + model.getControlWordInput());
		}

		// TS
		view.getScramblingControl().setText(model.getScramblingControl());

		// ECM ---------------------------------------
		view.getEcmHeaderTF().setText(encryptionECM.getEcmHeader());
		view.getEcmProtocolTF().setText(encryptionECM.getEcmProtocol());
		// Broadcast group id
		view.getEcmWorkKeyIdTF().setText(encryptionECM.getEcmWorkKeyId());
		view.getEcmCwOddTF().setText(encryptionECM.getEcmCwOdd());
		view.getEcmCwEvenTF().setText(encryptionECM.getEcmCwEven());
		// Program type
		view.getEcmDateTimeTF().setText(encryptionECM.getEcmDateTime());
		// Record control
		view.getEcmVariablePartTF().setText(encryptionECM.getEcmVariablePart());
		view.getEcmMacTF().setText(encryptionECM.getEcmMAC());
		view.getEcmCrcTF().setText(encryptionECM.getEcmCRC());
		// end ECM -----------------------------------

	}

	/**
	 * Creates a Random Hex Number.
	 * 
	 * @param length
	 *            Length of the Random Hex Number.
	 * @return Returns a Random Hex Number.
	 */
	public static String getRandomHex(int length) {
		SecureRandom randomService = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(Integer.toHexString(randomService.nextInt()));
		}
		sb.setLength(length);
		return sb.toString().toUpperCase();
	}

	/**
	 * Creates a Message Authentication Code (MAC, 4 byte length) of the ECM
	 * (Header + payload) in Hex.
	 * 
	 * @return The created MAC from ECM Message.
	 */
	private static String generateEcmMAC() {
		// get Message
		ecmHeader = encryptionECM.getEcmHeader() + encryptionECM.getEcmProtocol() + encryptionECM.getEcmBroadcastId()
				+ encryptionECM.getEcmWorkKeyId();
		ecmPayload = encryptionECM.getEcmCwOdd() + encryptionECM.getEcmCwEven() + encryptionECM.getEcmProgramType()
				+ encryptionECM.getEcmDateTime() + encryptionECM.getEcmRecordControl()
				+ encryptionECM.getEcmVariablePart();
		// get Authorization Key
		if (encryptionECM.getEcmWorkKeyId().equals("00")) {
			ecmWorkKey = model.getAuthorizationInputKey0();
		} else {
			ecmWorkKey = model.getAuthorizationInputKey1();
		}
		return getMAC(ecmHeader + ecmPayload, ecmWorkKey);
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
			System.out.println("No Such Algorithm:" + e.getMessage());
		} catch (InvalidKeyException e) {
			System.out.println("Invalid Key:" + e.getMessage());
		}
		return macString;
	}

	/**
	 * Creates a Cyclic Redundancy Check (CRC) of the current ECM.
	 * 
	 * @return The CRC based on the current ECM header and payload.
	 */
	private static String getEcmCRC() {
		ecmPayloadEncrypted = encryptedMessage(ecmPayload + encryptionECM.getEcmMAC(), ecmWorkKey);
		return getCRC(ecmHeader + ecmPayloadEncrypted);
	}

	/**
	 * Creates a Cyclic Redundancy Check (CRC) of string {@link getCRC}.
	 * 
	 * @param getCRC
	 *            Input String.
	 * @return The CRC from Input String {@link getCRC}.
	 */
	private static String getCRC(String getCRC) {
		java.util.zip.CRC32 x = new java.util.zip.CRC32();
		byte[] bytes = getCRC.getBytes(Charset.forName("UTF-8"));
		x.update(bytes);
		return String.format("%02X", x.getValue());
	}

	/**
	 * Sends the current ECM message via UDP to the broadcast network.
	 */
	public static void sendECM() {
		// get ecm and ecmEncrypted
		ecm = ecmHeader + ecmPayload + encryptionECM.getEcmMAC() + encryptionECM.getEcmCRC();
		ecmEncrypted = ecmHeader + ecmPayloadEncrypted + encryptionECM.getEcmCRC();
		// update GUI
		view.getEcmTA().setText(ecm);
		view.getEcmEncryptedTA().setText(ecmEncrypted);
		// send Message
		try {
			// default server = rtp://239.0.0.1:5004
			String server = configModel.getServer();
			String[] rtpSplit = server.split("://");
			// rtp = rtpSplit[0]
			String ipPort = rtpSplit[1];
			String[] ip = ipPort.split(":");
			// group = 239.0.0.1;
			InetAddress group = InetAddress.getByName(ip[0].trim());
			// port = default server port + 1 (5005);
			int port = Integer.parseInt(ip[1].trim()) + 1;

			byte[] outbuf = ecmEncrypted.getBytes();

			DatagramPacket packet = new DatagramPacket(outbuf, outbuf.length, group, port);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Encrypts the message {@link message} by the key {@link key}.
	 * 
	 * @param message
	 *            Message to encrypt.
	 * @param key
	 *            Key for encrypt.
	 * @return Encrypted message.
	 */
	private static String encryptedMessage(String message, String key) {
		SecretKey secretKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), "AES");
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			result = cipher.doFinal(DatatypeConverter.parseHexBinary(message));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return DatatypeConverter.printHexBinary(result);
	}

	/**
	 * Returns the current ECM message type {@link stateECMType}.
	 * 
	 * @return true for odd and false for even.
	 */
	public static boolean isStateECMType() {
		return stateECMType;
	}

	/**
	 * Sets the ECM type {@link stateECMType}: true for odd and false for even.
	 * 
	 * @param type
	 *            Type of the current ECM.
	 */
	public static void setStateECMType(boolean type) {
		stateECMType = type;
	}

	/**
	 * Getter of encryption ECM {@link encryptionECM}.
	 * 
	 * @return The encryption ECM.
	 */
	public static EncryptionECM getEncryptionECM() {
		return encryptionECM;
	}

	/**
	 * Creates a new EMM message with protocol type "CC" which contains the
	 * Authorization Key 0 and Authorization Key 1.
	 */
	public static void generateEMM() {
		view = SimulatorViewController.getView();
		model = SimulatorViewController.getModel();
		encryptionEMM = new EncryptionEMM();

		LocalDateTime dateTime = LocalDateTime.now();
		// Datum Formatieren: Monat Tag
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");

		System.out.println("ERROR : " + view.getAk0InTF().getText());

		// Update Input Keys
		model.setAuthorizationInputKey0(view.getAk0InTF().getText());
		model.setAuthorizationInputKey1(view.getAk1InTF().getText());

		encryptionEMM.setEmmHeader("8400000000000000");
		encryptionEMM.setEmmSmartcardId("111111111111");
		encryptionEMM.setEmmLength("2D");
		encryptionEMM.setEmmProtocol("CC");
		encryptionEMM.setEmmBroadcastId("FF");
		encryptionEMM.setEmmUpdateId("01");
		encryptionEMM.setEmmExpirationDate(dateTime.format(formatter));
		encryptionEMM.setEmmVariablePart(
				model.getAuthorizationInputKey0() + model.getAuthorizationInputKey1() + "000000000000000000000000");
		encryptionEMM.setEmmMAC(generateEmmMAC());
		encryptionEMM.setEmmCRC(generateEmmCRC());

		sendEMM();
	}

	/**
	 * Creates a Message Authentication Code (MAC, 4 byte length) of the EMM
	 * (Header + payload) in Hex.
	 * 
	 * @return The created MAC from EMM Message.
	 */
	private static String generateEmmMAC() {
		emmHeader = encryptionEMM.getEmmHeader() + encryptionEMM.getEmmSmartcardId() + encryptionEMM.getEmmLength()
				+ encryptionEMM.getEmmProtocol() + encryptionEMM.getEmmBroadcastId() + encryptionEMM.getEmmUpdateId()
				+ encryptionEMM.getEmmExpirationDate();
		emmPayload = encryptionEMM.getEmmVariablePart();

		// get EMM key
		emmKey = view.getMpkInTA().getText().toString().trim();
		return getMAC(emmHeader + emmPayload, emmKey);
	}

	/**
	 * Creates a Cyclic Redundancy Check (CRC) of the current EMM.
	 * 
	 * @return The CRC based on the current EMM header and payload.
	 */
	private static String generateEmmCRC() {
		emmPayloadEncrypted = encryptedMessage(emmPayload + encryptionEMM.getEmmMAC(), emmKey);
		return getCRC(emmHeader + emmPayloadEncrypted);
	}

	/**
	 * Sends one EMM message via UDP to that broadcast network.
	 */
	public static void sendEMM() {
		configModel = ConfigViewController.getConfigModel();
		// get emm and emmEncrypted
		emm = emmHeader + emmPayload + encryptionEMM.getEmmMAC() + encryptionEMM.getEmmCRC();
		emmEncrypted = emmHeader + emmPayloadEncrypted + encryptionEMM.getEmmCRC();

		// update GUI
		view.getEmmTA().setText(emm);
		view.getEmmEncryptedTA().setText(emmEncrypted);

		try {
			// default server = rtp://239.0.0.1:5004
			String server = configModel.getServer();
			String[] rtpSplit = server.split("://");
			// rtp = rtpSplit[0]
			String ipPort = rtpSplit[1];
			String[] ip = ipPort.split(":");
			// group = 239.0.0.1;
			InetAddress group = InetAddress.getByName(ip[0].trim());
			// port = default server port + 1 (5005);
			int port = Integer.parseInt(ip[1].trim()) + 1;

			byte[] outbuf = emmEncrypted.getBytes();

			DatagramPacket packet = new DatagramPacket(outbuf, outbuf.length, group, port);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
