package controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javafx.application.Platform;
import javafx.concurrent.Task;
import model.ConfigModel;
import model.EncryptionECM;
import model.SimulatorModel;
import view.SimulatorView;

public class Encryption {

	private static SimulatorView view;

	private static SimulatorModel model;
	
	// Encryption ECM Model
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


	public Encryption() {
		// model = simulatorModel;
		// view = inputView;
		// activateEncryption();

	}

	public static void runEncryption() {
		model = SimulatorViewController.getModel();
		view = SimulatorViewController.getView();

		encryptionECM = new EncryptionECM();
		
		// TODO
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

		// Constant CW
		if (model.getCwTime() == 0) {
			constantECM();
			VlcServerController.streamVlcFile(model.getInputFile().toString());
		}
		// Intervall CW
		else {
			Runnable encryptionRunnableFirst = new Runnable() {
				@Override
				public void run() {
					System.out.println("encryptionRunnableFirst");
					// TODO
					generateECM();
					//constantECM();

				}
			};

			Runnable encryptionRunnableSecond = new Runnable() {
				@Override
				public void run() {
					System.out.println("encryptionRunnableSecond");

					// TODO aktuell
					FFmpegController.runFFmpeg();
					
					
				
					//FFmpegController.runFFmpegTask().run();
					
					
					//FFmpegController.runFFmpegProcessBuilder();

				}
			};

			Runnable encryptionRunnableThird = new Runnable() {
				@Override
				public void run() {
					System.out.println("encryptionRunnableThird");

//					VlcServerController.mediaPlayer.stop();
//					VlcServerController.mediaPlayer.release();
//					VlcServerController.mediaPlayerFactory.release();
					
					
					//VlcServerController.streamVLCmediaPlayerInputFileOhneTask();
					
					
					//VlcServerController.streamVLCmediaPlayerInputFile();
					
					//VlcServerController.streamVLC();
					//VlcServerController.streamVLCtask();
					
					
					
//					VlcServerController.p.destroy();
//					VlcServerController.p.destroyForcibly();
					//VlcServerController.streamVLCProcessBuilder();
					
					
					//VlcServerController.streamVLCProcessBuilder_test();
					
					
					// TODO aktuell
					VlcServerController.streamVLCmediaPlayer();
					
					
					System.out.println("ende encryptionRunnableThird");

				}
			};
			
			

			ecmExecutor = Executors.newScheduledThreadPool(1);
			ecmExecutor.scheduleWithFixedDelay(encryptionRunnableFirst, 0, model.getCwTime(), TimeUnit.SECONDS);

			ffmpegExecutor = Executors.newScheduledThreadPool(1);
			ffmpegExecutor.scheduleWithFixedDelay(encryptionRunnableSecond, 1, model.getCwTime(), TimeUnit.SECONDS);

			vlcExecutor = Executors.newScheduledThreadPool(1);
			//vlcExecutor.scheduleWithFixedDelay(encryptionRunnableThird, 3, model.getCwTime(), TimeUnit.SECONDS);
			vlcExecutor.schedule(encryptionRunnableThird, 3, TimeUnit.SECONDS);
				
		}

		// TODO send ECM
		Runnable sendECMRunnable = new Runnable() {
			@Override
			public void run() {
				System.out.println("sendECMRunnable");

				sendECM();

			}

		};

		sendECMExecutor = Executors.newScheduledThreadPool(1);
		sendECMExecutor.scheduleWithFixedDelay(sendECMRunnable, 1, 5, TimeUnit.SECONDS);

	}

	public static void stopEncryption() {
		// TODO
		if (model.getCwTime() == 0) {
			VlcServerController.mediaPlayer.stop();
			VlcServerController.mediaPlayer.release();
			VlcServerController.mediaPlayerFactory.release();
//			sendECMExecutor.shutdown();
		} else {
			ecmExecutor.shutdown();
			ffmpegExecutor.shutdown();
			vlcExecutor.shutdown();
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

	public static void generateECM() {
		Task<Void> taskGenerateECM = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
				LocalDateTime dateTime;
				// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

				// sperre die CW Time
				view.getCwTimeTF().setDisable(true);

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
				} else {
					encryptionECM.setEcmCwEven(cw);
				}

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

				// GUI updaten
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						guiECMUpdate();
					}
				});

				// switch odd/even Scrambling Control
				if (isStateECMType()) {
					model.setScramblingControl("11");
					setStateECMType(false);
				} else {
					model.setScramblingControl("10");
					setStateECMType(true);
				}

				return null;
			} // end call
		};

		// start the task
		thGenerateECM = new Thread(taskGenerateECM);
		thGenerateECM.setDaemon(true);
		thGenerateECM.start();

	}

	public static Runnable constantECM() {
		Task<Void> taskConstantECM = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
				LocalDateTime dateTime;
				// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

				// sperre die CW Time
				view.getCwTimeTF().setDisable(true);

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
		return null;
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
	 * Erzeugt einen Message Authentication Code (MAC, 4 Bytes länge) von der ECM (Header + Payload).
	 * 
	 * @return Gibt einen MAC in Hex zurück.
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

	private static void sendECM() {
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
			 	//InetAddress dst = InetAddress.getLocalHost();
			 	InetAddress group = InetAddress.getByName("239.0.0.1");
			    int port = 5005;
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
	 * 
	 * @return
	 */
	public static boolean isStateECMType() {
		return stateECMType;
	}

	/**
	 * Setzt anhand des Parameters @true für Odd und @false für Even
	 * 
	 * @param state Status Typ der aktuellen ECM.
	 */
	public static void setStateECMType(boolean state) {
		stateECMType = state;
	}
	
	public static EncryptionECM getEncryptionECM () {
	return encryptionECM;
}

}
