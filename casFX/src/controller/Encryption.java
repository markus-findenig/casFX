package controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.media.MediaPlayer.Status;
import model.SimulatorModel;
import view.InputView;

public class Encryption {
	
	private static InputView view;
	
	private static SimulatorModel model;
	

	// Encryption Thread
	public static Thread thGenerateECM;

	// ECM Payload
	private static String payload;

	public Encryption() {
//		model = simulatorModel;
//		view = inputView;
//		activateEncryption();

	}

	public static void runEncryption() {
		
		// TODO
		// 1. generate ECM odd
		// 2. cut file odd
		// 3. vlc stream odd
		// 4. generate ECM even
		// 5. cut file even
		// 6. vlc stream odd
		// 7. goto 1
		
		
		
	}
	
	
	
	public static void generateECM() {
		
		model = InputViewController.getModel();
		view = InputViewController.getView();
		
		Task<Void> taskGenerateECM = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
				LocalDateTime dateTime;
				// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
				
				while (!isCancelled() && model.getEncryptionState()) {
				
					// Time (sec), hole die Zeit vom Timer Eingabefeld
					model.setCwTime(Integer.parseInt(view.getCwTimeTF().getText().toString()));
					
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
					
					// ECM Scrambling Control Pointer for CW even
					if (model.getScramblingControl() == "10") {
						model.setEcmCwEven(cw);
					}
					// Scrambling Control Pointer for CW odd
					else {
						model.setEcmCwOdd(cw);
					}
					
					// ECM Program type
					model.setEcmProgramType("C8");
					

					// ECM Date/Time setzen
					dateTime = LocalDateTime.now();
					model.setEcmDateTime(dateTime.format(formatter));
					
					// Record control
					model.setEcmRecordControl("D5");
					
					// Variable part
					model.setEcmVariablePart("00000000");

					// setze ECM MAC
					model.setEcmMac(getMAC());

					// setze ECM CRC
					model.setEcmCrc(getCRC());
					
					// GUI updaten
					Platform.runLater(new Runnable() {
						public void run() {
							// Input Player
							view.getCwTF().setText(model.getControlWordInput());
							
							// TS
							view.getScramblingControlTF().setText(model.getScramblingControl());
							
							// ECM ---------------------------------------
							// ECM Section Header
							// Protocol number
							// Broadcast group id
							view.getEcmWorkKey().setText(model.getEcmWorkKeyId());
							view.getEcmCwOddTF().setText(model.getEcmCwOdd());
							view.getEcmCwEvenTF().setText(model.getEcmCwEven());
							// Program type
							view.getEcmDateTime().setText(model.getEcmDateTime());
							// Record control
							// Variable part
							view.getEcmMacTF().setText(model.getEcmMac());
							view.getEcmCrcTF().setText(model.getEcmCrc());
							// -------------------------------------------
							
						}

					});
					
					// Thread wait
					try {
						// time in seconds
						Thread.sleep(model.getCwTime() * 1000);

						// Scrambling Control switch
						if (model.getScramblingControl() == "10") {
							model.setScramblingControl("11");
						} else {
							model.setScramblingControl("10");
						}
					} catch (InterruptedException interrupted) {
						break;
					}
					
					
					
					
				} // end while
				

				return null;
			} // end call
		};

		// start the task
		thGenerateECM = new Thread(taskGenerateECM);
		thGenerateECM.setDaemon(true);
		thGenerateECM.start();
		
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
		
		System.out.println("get MAC" + model.getEcmHeader());
		
		payload = model.getEcmHeader() + model.getEcmProtocol() + model.getEcmBroadcastId() + model.getEcmWorkKeyId()
				+ model.getEcmCwOdd() + model.getEcmCwEven() + model.getEcmProgramType() + model.getEcmDateTime()
				+ model.getEcmRecordControl() + model.getEcmVariablePart();

		String ecmWorkKey = null;
		String macString = null;
		
		System.out.println("get payload: " + payload);

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
		System.out.println("get MAC String" + macString);
		
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
}
