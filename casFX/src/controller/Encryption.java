package controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.sun.jna.NativeLibrary;

import javafx.application.Platform;
import javafx.concurrent.Task;
import model.SimulatorModel;
import view.SimulatorView;

public class Encryption {

	private static SimulatorView view;

	private static SimulatorModel model;

	public static ScheduledExecutorService encryptionExecutor;

	// Encryption Thread
	public static Thread thGenerateECM;

	/**
	 * @true Odd ECM
	 * @false Even ECM
	 */
	static Boolean stateECMType;
	


	// ECM Payload
	private static String payload;

	public Encryption() {
		// model = simulatorModel;
		// view = inputView;
		// activateEncryption();

	}

	public static void runEncryption() {
		model = SimulatorViewController.getModel();
		view = SimulatorViewController.getView();

		// TODO
		// 1. generate ECM odd
		// 2. cut file odd
		// 3. vlc stream odd
		// 4. generate ECM even
		// 5. cut file even
		// 6. vlc stream odd
		// 7. goto 1

		// Time (sec), hole die Zeit vom Timer Eingabefeld
		model.setCwTime(Integer.parseInt(view.getCwTimeTF().getText().toString()));

		// first init parameter
		FFmpegController.initFFmpegController();
		VlcServerController.initVLC();
		setStateECMType(true);

		// constant CW
		if (model.getCwTime() == 0) {
			constantECM();

			VlcServerController.streamVlcFile(model.getInputFile().toString());

			// Intervall CW
		} else {
			Runnable encryptionRunnable = new Runnable() {
				public void run() {
					System.out.println("Runnable running");

					generateECM();
				

					FFmpegController.runFFmpeg();

					// VlcServerController.streamVLC();

				}
			};

			encryptionExecutor = Executors.newScheduledThreadPool(3);
			encryptionExecutor.scheduleWithFixedDelay(encryptionRunnable, 0, model.getCwTime(), TimeUnit.SECONDS);

			// generateECM();
			// FFmpegController.runFFmpeg();
			// VlcServerController.streamVLC();

			// TODO stream odd and even
			// VlcServerController.streamVlcFile(model.getInputFile().toString());
		}

	}

	public static void stopEncryption() {
		// TODO
		encryptionExecutor.shutdown();
		thGenerateECM.stop();
		FFmpegController.thFFmpeg.stop();
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

				// ECM Section Header
				model.setEcmHeader("8200000000000000");

				// ECM Protocol number
				model.setEcmProtocol("AA");

				// ECM Broadcast group id
				model.setEcmBroadcastId("FF");

				// ECM AK id
				if (rbStatus == "00") {
					model.setEcmWorkKeyId("00");
				} else {
					model.setEcmWorkKeyId("01");
				}

				// ECM Scrambling Control Pointer for CW odd
				if (isStateECMType()) {
					model.setEcmCwOdd(cw);
				} else {
					model.setEcmCwEven(cw);
				}

				// ECM Program type
				model.setEcmProgramType("C8");

				// ECM Date/Time setzen
				dateTime = LocalDateTime.now();
				model.setEcmDateTime(dateTime.format(formatter));

				// Record control
				model.setEcmRecordControl("D5");

				// Variable part
				model.setEcmVariablePart(Integer.toString(model.getCwTime()));

				// setze ECM MAC
				model.setEcmMac(getMAC());

				// setze ECM CRC
				model.setEcmCrc(getCRC());
				
				// GUI updaten
				Platform.runLater(new Runnable() {
					public void run() {
						guiUpdate();
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

	public static void constantECM() {
		Task<Void> taskConstantECM = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
				LocalDateTime dateTime;
				// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");

				// sperre die CW Time
				view.getCwTimeTF().setDisable(true);

				// erzeuge ein cw
				String cw = "0123456789ABCDEF";
				// setzte das CW im Input Player
				model.setControlWordInput(cw);

				// ECM Section Header
				model.setEcmHeader("8200000000000000");

				// ECM Protocol number (BB for constantCW)
				model.setEcmProtocol("BB");

				// ECM Broadcast group id
				model.setEcmBroadcastId("FF");

				// ECM AK id
				if (rbStatus == "00") {
					model.setEcmWorkKeyId("00");
				} else {
					model.setEcmWorkKeyId("01");
				}

				// set cw for odd and even
				model.setEcmCwOdd(cw);
				model.setEcmCwEven(cw);

				// ECM Program type
				model.setEcmProgramType("C8");

				// ECM Date/Time setzen
				dateTime = LocalDateTime.now();
				model.setEcmDateTime(dateTime.format(formatter));

				// Record control
				model.setEcmRecordControl("D5");

				// Variable part
				model.setEcmVariablePart(Integer.toString(model.getCwTime()));

				// setze ECM MAC
				model.setEcmMac(getMAC());

				// setze ECM CRC
				model.setEcmCrc(getCRC());

				// GUI updaten
				Platform.runLater(new Runnable() {
					public void run() {
						guiUpdate();
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

	private static void guiUpdate() {
		// Input Player
		view.getCwTF().setText(model.getControlWordInput());

		// TS
		view.getScramblingControlTF().setText(model.getScramblingControl());

		// ECM ---------------------------------------
		// ECM Section Header
		view.getEcmProtocolTF().setText(model.getEcmProtocol());
		// Broadcast group id
		view.getEcmWorkKey().setText(model.getEcmWorkKeyId());
		view.getEcmCwOddTF().setText(model.getEcmCwOdd());
		view.getEcmCwEvenTF().setText(model.getEcmCwEven());
		// Program type
		view.getEcmDateTime().setText(model.getEcmDateTime());
		// Record control
		view.getEcmVariablePartTF().setText(model.getEcmVariablePart());
		view.getEcmMacTF().setText(model.getEcmMac());
		view.getEcmCrcTF().setText(model.getEcmCrc());
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
	 * Erzeugt einen MAC (4 Bytes) von der ECM Payload.
	 * 
	 * @return Gibt einen MAC in Hex zurück.
	 * @throws Exception
	 */
	private static String getMAC() {
		// get payload elements
		payload = model.getEcmHeader() + model.getEcmProtocol() + model.getEcmBroadcastId() + model.getEcmWorkKeyId()
				+ model.getEcmCwOdd() + model.getEcmCwEven() + model.getEcmProgramType() + model.getEcmDateTime()
				+ model.getEcmRecordControl() + model.getEcmVariablePart();

		String ecmWorkKey = null;
		String macString = null;

		if (model.getEcmWorkKeyId() == "00") {
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
			byte[] b = payload.getBytes("UTF-8");
			// create a digest from the byte array
			byte[] digest = mac.doFinal(b);

			// cut lsb to 4 bytes in hex
			macString = String.format("%02X ", new BigInteger(1, digest.toString().substring(4, 8).getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No Such Algorithm:" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding:" + e.getMessage());
		} catch (InvalidKeyException e) {
			System.out.println("Invalid Key:" + e.getMessage());
		}

		return macString;

	}

	/**
	 * Erzeugt einen Cyclic Redundancy Check (CRC) vom aktuellen ECM Payload.
	 * 
	 * @return Gibt den CRC anhand der aktuellen ECM Payload zurück.
	 */
	public static String getCRC() {
		java.util.zip.CRC32 x = new java.util.zip.CRC32();
		byte[] bytes = payload.getBytes(Charset.forName("UTF-8"));
		x.update(bytes);
		return Long.toHexString(x.getValue()).toUpperCase();
	}
	

	public static boolean isStateECMType() {
		return stateECMType;
	}

	public static void setStateECMType(boolean s) {
		stateECMType = s;
	}
	
}
