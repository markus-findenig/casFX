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
import javafx.concurrent.Task;
import model.ConfigModel;
import model.EncryptionECM;
import model.SimulatorModel;
import view.SimulatorView;

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

	public static ScheduledExecutorService ecmExecutor;

	public static ScheduledExecutorService ffmpegExecutor;

	public static ScheduledExecutorService vlcExecutor;

	public static ScheduledExecutorService sendECMExecutor;
	

	// Encryption Thread
	public static Thread thGenerateECM;

	/**
	 * @true Odd ECM
	 * @false Even ECM
	 */
	static Boolean stateECMType;

	/**
	 * ecmHeader = ECM Section Header + Protocol number + Broadcast group id + AK id
	 */
	private static String ecmHeader;

	/**
	 * ecmPayload = CW (odd) + CW (even) + Program type + Date/Time +
	 * Recording control + Variable part
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
	 * ECM Start Zeit 0 Sekunden (erzeugen)
	 */
	private static int ECM_EXECUTOR_INIT_DELAY;
	
	/**
	 * ECM Intervall Verz�gerung model.getCwTime() (erneut erzeugen)
	 */
	private static int ECM_EXECUTOR_DELAY;
	
	/**
	 * FFmpeg Start Verz�gerung 1 Sekunde (erstelle odd/even Datei)
	 */
	private static int FFMPEG_EXECUTOR_INIT_DELAY;
	
	/**
	 * FFmpeg Intervall Verz�gerung model.getCwTime() (erneut erzeugen odd/even Datei)
	 */
	private static int FFMPEG_EXECUTOR_DELAY;
	
	/**
	 * VLC Stream Verz�gerung 3 Sekunden (streamt odd/even Datei)
	 */
	private static int VLC_EXECUTOR_DELAY;
	
	/**
	 * ECM Nachricht, sende Verz�gerung der ersten Nachricht 1 Sekunde
	 */
	private static int SEND_ECM_EXECUTOR_INIT_DELAY;
	
	/**
	 * ECM Nachricht, sende Intervall model.getCwTime() / 2 (H�lfte der CW Intervall Zeit)
	 */
	private static int SEND_ECM_EXECUTOR_DELAY;


//	public EncryptionController() {
//		// model = simulatorModel;
//		// view = inputView;
//		// activateEncryption();
//		model = SimulatorViewController.getModel();
//		
//		ECM_EXECUTOR_INIT_DELAY = 0;
//		ECM_EXECUTOR_DELAY = model.getCwTime();
//		
//		FFMPEG_EXECUTOR_INIT_DELAY = 1;
//		FFMPEG_EXECUTOR_DELAY = model.getCwTime();
//		
//		VLC_EXECUTOR_DELAY = 3;
//		
//		SEND_ECM_EXECUTOR_INIT_DELAY = 1;
//		SEND_ECM_EXECUTOR_DELAY = model.getCwTime() / 2;
//
//	}

	/**
	 * Startet die Verschl�sselung
	 */
	public static void runEncryption() {
		model = SimulatorViewController.getModel();
		view = SimulatorViewController.getView();
		configModel = ConfigViewController.getConfigModel();
		
		// set scrambling, CW odd for first init
		//model.setScramblingControl("11");

		encryptionECM = new EncryptionECM();
		
		// run Encryption
		// 1. generate ECM odd
		// 2. cut file odd
		// 3. vlc stream odd
		// 4. generate ECM even
		// 5. cut file even
		// 6. vlc stream even
		// 7. goto 1

		// Time (sec), hole die Zeit vom Timer Eingabefeld
		model.setCwTime(Integer.parseInt(view.getCwTimeTF().getText().toString()));

		// init first cw's
		encryptionECM.setEcmCwOdd(view.getEcmCwOddTF().getText());
		encryptionECM.setEcmCwEven(view.getEcmCwEvenTF().getText());
		
		// init Authorization Keys
		model.setAuthorizationInputKey0(view.getAk0InTF().getText());
		model.setAuthorizationInputKey1(view.getAk1InTF().getText());
		
		
		// first init parameter
		FFmpegController.initFFmpegController();
		VlcServerController.initVLC();
		
		setStateECMType(true);
		

		// sperre die CW Time
		view.getCwTimeTF().setDisable(true);

		// Constant CW
		if (model.getCwTime() == 0) {
			constantECM();
			VlcServerController.streamVlcFile(model.getInputFile().toString());
		}
		// Intervall CW
		else {
			
			FFmpegController.runFFmpeg();
			generateECM();
			sendECM();
			VlcServerController.streamVLCmediaPlayer();
			
			
			
			
//			Runnable runGenerateECM = new Runnable() {
//				@Override
//				public void run() {
//					generateECM();
//				}
//			};
//
//			Runnable runFFmpeg = new Runnable() {
//				@Override
//				public void run() {
//					FFmpegController.runFFmpeg();
//				}
//			};
//
//			Runnable runVLCmediaPlayer = new Runnable() {
//				@Override
//				public void run() {
//					VlcServerController.streamVLCmediaPlayer();
//				}
//			};
//
//			ecmExecutor = Executors.newScheduledThreadPool(1);
//			ecmExecutor.scheduleWithFixedDelay(runGenerateECM, 0, model.getCwTime(), TimeUnit.SECONDS);
//
//			ffmpegExecutor = Executors.newScheduledThreadPool(1);
//			ffmpegExecutor.scheduleWithFixedDelay(runFFmpeg, 1, model.getCwTime(), TimeUnit.SECONDS);
//
//			vlcExecutor = Executors.newScheduledThreadPool(1);
//			//vlcExecutor.scheduleWithFixedDelay(encryptionRunnableThird, 3, model.getCwTime(), TimeUnit.SECONDS);
//			vlcExecutor.schedule(runVLCmediaPlayer, 3, TimeUnit.SECONDS);
				
		}

		// send ECM
		Runnable sendECMRunnable = new Runnable() {
			@Override
			public void run() {
				sendECM();
			}
		};

		sendECMExecutor = Executors.newScheduledThreadPool(1);
		// Constant CW, statisch alle 5 Sekunden
		if (model.getCwTime() == 0) {
			sendECMExecutor.scheduleWithFixedDelay(sendECMRunnable, 1, 5, TimeUnit.SECONDS);
		} 
		// Intervall CW, halbe CW Time
		else {
			//sendECMExecutor.scheduleWithFixedDelay(sendECMRunnable, 1, model.getCwTime() / 2, TimeUnit.SECONDS);
		}
		

	}

	/**
	 * Stoppt die Verschl�sselung
	 */
	public static void stopEncryption() {
		// TODO
		if (model.getCwTime() == 0) {
			VlcServerController.mediaPlayer.stop();
			VlcServerController.mediaPlayer.release();
			VlcServerController.mediaPlayerFactory.release();
			thGenerateECM.stop();
			sendECMExecutor.shutdownNow();
		} else {
			//thGenerateECM.stop();
//			ecmExecutor.shutdownNow();
//			ffmpegExecutor.shutdownNow();
//			vlcExecutor.shutdownNow();
			VlcServerController.headlessMediaPlayer.stop();
			VlcServerController.headlessMediaPlayer.release();
			VlcServerController.mediaPlayerFactory.release();
		}

		// thGenerateECM.stop();
		// FFmpegController.thFFmpeg.stop();
		// Encryption.encryptionExecutor.shutdown();
		// PlayerViewController.thInitPlayerOutput.stop();
		// PlayerViewController.thInitPlayerOutput.stop();

	}

	/**
	 * Erzeugt eine neue ECM Nachricht mit Protokoll Typ "AA"
	 */
	public static void generateECM() {
//		Task<Void> taskGenerateECM = new Task<Void>() {
//			@Override
//			protected Void call() throws Exception {
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
				} else {
					ecmType = "81";
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
					encryptionECM.setEcmCwEven("0000000000000000");
				} else {
					encryptionECM.setEcmCwOdd("0000000000000000");
					encryptionECM.setEcmCwEven(cw);
				}

				// ECM Program type
				encryptionECM.setEcmProgramType("C8");

				// ECM Date/Time setzen
				dateTime = LocalDateTime.now();
				//dateTime = LocalDateTime.now().plusSeconds(3);
				// plus die VLC verz�gerung
				// plus die ECM CW Time G�ltigkeitsdauer
				//dateTime.plusSeconds(3 + model.getCwTime());
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

				

//				return null;
//			} // end call
//		};
//
//		// start the task
//		thGenerateECM = new Thread(taskGenerateECM);
//		thGenerateECM.setDaemon(true);
//		thGenerateECM.start();

	}

	/**
	 * Erzeugt eine Konstante ECM mit Protokoll Typ "BB"
	 */
	public static void constantECM() {
		Task<Void> taskConstantECM = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
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
				encryptionECM.setEcmCwEven("0000000000000000");

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

				return null;
			} // end call
		};

		// start the task
		thGenerateECM = new Thread(taskConstantECM);
		thGenerateECM.setDaemon(true);
		thGenerateECM.start();

		try {
			// wait for finish
			thGenerateECM.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void guiECMUpdate() {
		// Input Player
		view.getCwTF().setText(model.getControlWordInput());

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
	 *            - L�nge der Random Hex Nummer
	 * @return Gibt eine Random Hex Nummer der L�nge length zur�ck.
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
	 * Erzeugt einen Message Authentication Code (MAC, 4 Bytes l�nge) von der ECM (Header + Payload).
	 * 
	 * @return Gibt einen MAC in Hex zur�ck.
	 * @throws Exception
	 */
	private static String getMAC() {
		ecmHeader = encryptionECM.getEcmHeader() + encryptionECM.getEcmProtocol() + encryptionECM.getEcmBroadcastId() + encryptionECM.getEcmWorkKeyId();
		ecmPayload = encryptionECM.getEcmCwOdd() + encryptionECM.getEcmCwEven() + encryptionECM.getEcmProgramType()
				+ encryptionECM.getEcmDateTime() + encryptionECM.getEcmRecordControl() + encryptionECM.getEcmVariablePart();

		String getMAC = ecmHeader + ecmPayload;
		//String ecmWorkKey = null;
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
	 * @return Gibt den CRC anhand der aktuellen ECM Header und Payload zur�ck.
	 */
	public static String getCRC() {
		ecmPayloadEncrypted = encryptedECM(ecmPayload + encryptionECM.getEcmMAC());
		String getCRC = ecmHeader + ecmPayloadEncrypted;
		java.util.zip.CRC32 x = new java.util.zip.CRC32();
		byte[] bytes = getCRC.getBytes(Charset.forName("UTF-8"));
		x.update(bytes);
		return String.format("%02X", x.getValue());
	}

	public static void sendECM() {
		// get ecm and ecmEncrypted
		ecm = ecmHeader + ecmPayload + encryptionECM.getEcmMAC() + encryptionECM.getEcmCRC();
		ecmEncrypted = ecmHeader + ecmPayloadEncrypted + encryptionECM.getEcmCRC();

		// update GUI
		view.getEcmTA().setText(ecm);
		view.getEcmEncryptedTA().setText(ecmEncrypted);

//		System.err.println("ecm:" + ecm);
//		System.err.println("ecmEncrypted:" + ecmEncrypted);
//		
		
		// TODO send udp Datagram nicht notwendig 80 und 81
		// send model.getScramblingControl() + ecm 
		
		try {
			// default server = rtp://239.0.0.1:5004
			String server = configModel.getServer();
			String[] rtpSplit = server.split("://");
			// rtp = rtpSplit[0]
			String ipPort = rtpSplit[1];
			String[] ip = ipPort.split(":");
			InetAddress group = InetAddress.getByName(ip[0].trim());
			int port = Integer.parseInt(ip[1].trim()) + 1;
//			InetAddress group = InetAddress.getByName("239.0.0.1");
//			int port = 5005;
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
	 * Verschl�sselt den input Parameter mittels aktuell ausgew�hlten
	 * AuthorizationKey (AK)
	 * 
	 * @param ecm
	 *            ECM Nachricht zum Verschl�sseln
	 * @return Verschl�sselte ECM Nachricht.
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
	 * Gibt an welcher ECM Type gerade aktiv ist.
	 * 
	 * @return - @true f�r Odd und @false f�r Even
	 */
	public static boolean isStateECMType() {
		return stateECMType;
	}

	/**
	 * Setzt den ECM Type: @true f�r Odd und @false f�r Even.
	 * 
	 * @param type - Typ der aktuellen ECM.
	 */
	public static void setStateECMType(boolean type) {
		stateECMType = type;
	}
	
	public static EncryptionECM getEncryptionECM () {
		return encryptionECM;
}

}
