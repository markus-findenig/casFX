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
import model.SimulatorModel;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import view.SimulatorView;

/**
 * Encryption Controller. Steuert die Verschlüsselung.
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
	 * Scheduled Executor Service. Sendet statisch alle 2 Sekunden die aktuelle
	 * ECM Nachricht.
	 */
	public static ScheduledExecutorService sendECMExecutor;

	/**
	 * Aktueller ECM Nachrichten Status.
	 * 
	 * @true - odd ECM
	 * @false - even ECM
	 */
	public static Boolean stateECMType;

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
	 * ecmPayloadEncrypted = ecmPayload + Payload MAC
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
	 * Startet die Verschlüsselung.
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

		// run Encryption
		// 1. cut file odd
		// 2. generate ECM odd
		// 3. vlc stream odd
		// 4. cut file even
		// 5. generate ECM even
		// 6. vlc stream even
		// 7. goto 1

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

		// Constant CW
		if (model.getCwTime() == 0) {
			constantECM();
			InputPlayerController.streamInputPlayerRTP(model.getInputFile().toString());
		}
		// Intervall CW
		else {

			InputPlayerController.initIntervallInputPlayer();

			FFmpegController.runFFmpeg();
			generateECM();
			sendECM();
			// TODO
			InputPlayerController.runInputPlayer();
			// InputPlayerController.streamInputPlayer();

		}

		// send ECM
		Runnable sendECMRunnable = new Runnable() {
			@Override
			public void run() {
				sendECM();
			}
		};

		sendECMExecutor = Executors.newScheduledThreadPool(1);
		// CW, statisch alle 2 Sekunden
		sendECMExecutor.scheduleWithFixedDelay(sendECMRunnable, 1, 2, TimeUnit.SECONDS);

	}

	/**
	 * Stoppt die Verschlüsselung
	 */
	public static void stopEncryption() {

		view.getEncryption().setText("OFF");
		view.getVideoInputButton().setDisable(true);
		model.setEncryptionState(false);
		// no scrambling
		model.setScramblingControl("00");
		view.getScramblingControlTF().setText("00");
		// Entsperre die CW Time
		view.getCwTimeTF().setDisable(false);
		// TODO
		InputPlayerController.stopInputPlayer();
		sendECMExecutor.shutdownNow();

	}

	/**
	 * Erzeugt eine neue ECM Nachricht mit Protokoll Typ "AA"
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
		encryptionECM.setEcmMAC(getMAC());

		// setze ECM CRC
		encryptionECM.setEcmCRC(getCRC());

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
	 * Erzeugt eine Konstante ECM mit Protokoll Typ "BB"
	 */
	public static void constantECM() {
		// Task<Void> taskConstantECM = new Task<Void>() {
		// @Override
		// protected Void call() throws Exception {
		String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
		LocalDateTime dateTime;
		// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

		// hohle constant cw in der config
		String cw = ConfigModel.getConstantCw();
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
		encryptionECM.setEcmMAC(getMAC());

		// setze ECM CRC
		encryptionECM.setEcmCRC(getCRC());

		// TS Scrambling Control auf odd
		model.setScramblingControl("11");

		// GUI updaten
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				guiECMUpdate();
			}
		});

	}

	/**
	 * Aktualisiert die Parameter des Simulators.
	 */
	private static void guiECMUpdate() {
		// Input Player
		if (isStateECMType()) {
			view.getCwTF().setText("odd:" + model.getControlWordInput());
		} else {
			view.getCwTF().setText("even:" + model.getControlWordInput());
		}

		// TS
		view.getScramblingControlTF().setText(model.getScramblingControl());

		// ECM ---------------------------------------
		view.getEcmHeaderTF().setText(encryptionECM.getEcmHeader());
		view.getEcmProtocolTF().setText(encryptionECM.getEcmProtocol());
		// Broadcast group id
		view.getEcmWorkKey().setText(encryptionECM.getEcmWorkKeyId());
		view.getEcmCwOddTF().setText(encryptionECM.getEcmCwOdd());
		view.getEcmCwEvenTF().setText(encryptionECM.getEcmCwEven());
		// Program type
		view.getEcmDateTime().setText(encryptionECM.getEcmDateTime());
		// Record control
		view.getEcmVariablePartTF().setText(encryptionECM.getEcmVariablePart());
		view.getEcmMacTF().setText(encryptionECM.getEcmMAC());
		view.getEcmCrcTF().setText(encryptionECM.getEcmCRC());
		// -------------------------------------------

	}

	/**
	 * Erzeugt eine Random Hex Nummer
	 * 
	 * @param length
	 *            - Länge der Random Hex Nummer
	 * @return Gibt eine Random Hex Nummer der Länge length zurück.
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
	 * Erzeugt einen Message Authentication Code (MAC, 4 Bytes länge) von der
	 * ECM (Header + Payload).
	 * 
	 * @return Gibt einen MAC in Hex zurück.
	 * @throws Exception
	 */
	private static String getMAC() {
		ecmHeader = encryptionECM.getEcmHeader() + encryptionECM.getEcmProtocol() + encryptionECM.getEcmBroadcastId()
				+ encryptionECM.getEcmWorkKeyId();
		ecmPayload = encryptionECM.getEcmCwOdd() + encryptionECM.getEcmCwEven() + encryptionECM.getEcmProgramType()
				+ encryptionECM.getEcmDateTime() + encryptionECM.getEcmRecordControl()
				+ encryptionECM.getEcmVariablePart();

		String getMAC = ecmHeader + ecmPayload;
		// String ecmWorkKey = null;
		String macString = null;

		if (encryptionECM.getEcmWorkKeyId() == "00") {
			ecmWorkKey = model.getAuthorizationInputKey0();
		} else {
			ecmWorkKey = model.getAuthorizationInputKey1();
		}

		// generate a key
		SecretKeySpec macKey = new SecretKeySpec(ecmWorkKey.getBytes(), "HmacSHA1");

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
		return macString;

	}

	/**
	 * Erzeugt einen Cyclic Redundancy Check (CRC) vom aktuellen ECM.
	 * 
	 * @return Gibt den CRC anhand der aktuellen ECM Header und Payload zurück.
	 */
	public static String getCRC() {
		ecmPayloadEncrypted = encryptedECM(ecmPayload + encryptionECM.getEcmMAC());
		String getCRC = ecmHeader + ecmPayloadEncrypted;
		java.util.zip.CRC32 x = new java.util.zip.CRC32();
		byte[] bytes = getCRC.getBytes(Charset.forName("UTF-8"));
		x.update(bytes);
		return String.format("%02X", x.getValue());
	}

	/**
	 * Sendet die aktuelle ECM Nachricht mittels UDP an das Broadcast Netzwerk.
	 */
	public static void sendECM() {
		// get ecm and ecmEncrypted
		ecm = ecmHeader + ecmPayload + encryptionECM.getEcmMAC() + encryptionECM.getEcmCRC();
		ecmEncrypted = ecmHeader + ecmPayloadEncrypted + encryptionECM.getEcmCRC();

		// update GUI
		view.getEcmTA().setText(ecm);
		view.getEcmEncryptedTA().setText(ecmEncrypted);

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
	 * Verschlüsselt den input Parameter mittels aktuell ausgewählten
	 * AuthorizationKey (AK)
	 * 
	 * @param ecm
	 *            ECM Nachricht zum Verschlüsseln
	 * @return Verschlüsselte ECM Nachricht.
	 */
	private static String encryptedECM(String ecm) {
		// generate the encrypted key with the current Authorization Key
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(ecmWorkKey), "AES");

		Cipher cipher;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			result = cipher.doFinal(DatatypeConverter.parseHexBinary(ecm));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return DatatypeConverter.printHexBinary(result);

	}

	/**
	 * Liefert den aktuellen ECM Nachrichten Typ {@link stateECMType}.
	 * 
	 * @return - Gibt true für Odd und false für Even zurück.
	 */
	public static boolean isStateECMType() {
		return stateECMType;
	}

	/**
	 * Setzt den ECM Type {@link stateECMType}: @true für Odd und @false für
	 * Even.
	 * 
	 * @param type
	 *            - Typ der aktuellen ECM.
	 */
	public static void setStateECMType(boolean type) {
		stateECMType = type;
	}

	/**
	 * Liefert die Encryption ECM {@link encryptionECM}.
	 * 
	 * @return - Gibt die Encryption ECM zurück.
	 */
	public static EncryptionECM getEncryptionECM() {
		return encryptionECM;
	}

}
