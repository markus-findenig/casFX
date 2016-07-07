package controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Toggle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import model.SimulatorModel;
import view.InputView;

/**
 * Input View Controller
 */
public class InputViewController {

	// Model
	private SimulatorModel model;

	// View
	private static InputView view;
	
	// Player Threads
	private Thread thInitPlayerInput;
	private Thread thInitPlayerOutput;
	
	// Encryption Thread
	private Thread thActivateEncryption;
	
	// ECM Payload
	private String payload;

	/**
	 * Input View Controller
	 * 
	 * @param simulatorModel
	 *            - Simulator Model
	 */
	public InputViewController(SimulatorModel sModel) {
		model = sModel;

		// instance with dummy file video
		setInputFile(new File("resorces\\dummy.mp4"));
		//setOutputFile(new File("resorces\\dummy.mp4"));
		

		// TODO dummy Data for BarChart
		SimulatorModel.observableArrayList = getChartData();

		view = new InputView();
		
		CasEventHandler casEventHandler = new CasEventHandler();

		// Cas Event handler registrieren
		view.getOpen().setOnAction(casEventHandler);
		view.getExit().setOnAction(casEventHandler);
		
		// Config Popup
		view.getConfig().setOnAction(casEventHandler);

		// Encryption Toggle Button registrieren
		view.getEncryption().setOnAction(casEventHandler);

		view.getRadioButtonGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
			}
		});
		
		

		// Test Function
		view.test().setOnAction(event -> {

						
			
//			HttpServer httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 7777), 0);
//			httpServer.createContext("/", new CustomHttpHandler("/dir/to/files/to/play"));
//			httpServer.start();
					
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
//			LocalDateTime dateTime = LocalDateTime.now();
//			String formattedDateTime = dateTime.format(formatter);
//
//			System.out.println(formattedDateTime);

			// Task<Void> task = new Task<Void>() {
			// @Override
			// public Void call() throws Exception {
			// Status status = view.mediaPlayerInput.getStatus();
			// while (status == Status.PLAYING) {
			// model.controlWordInput = getRandomHex(16);
			// updateMessage(model.controlWordInput);
			// model.cwTime =
			// Integer.parseInt(view.getCwTimeTF().getText().toString());
			// Thread.sleep(model.cwTime * 1000); // time in seconds
			// }
			// return null;
			// }
			// };
			// task.messageProperty().addListener((obs, oldMessage, newMessage)
			// -> view.getCwTF().setText(newMessage));
			// new Thread(task).start();
		});
	}

	public void show() {
		view.show(model.getPrimaryStage());
	}

	class CasEventHandler implements EventHandler<ActionEvent> {

		@SuppressWarnings("deprecation")
		@Override
		public void handle(ActionEvent event) {

			// Encryption State ON (true) or OFF (false)
			if (event.getSource() == view.getEncryption()) {
				// get Toggle Button State
				if (view.getEncryption().isSelected()) {
					view.getEncryption().setText("ON");
					model.setEncryptionState(true);
					// set scrambling, CW odd
					model.setScramblingControl("11");
					// run Encryption
					activateEncryption();
				} else {
					// stop Encryption Thread
					thActivateEncryption.stop();
					view.getEncryption().setText("OFF");
					model.setEncryptionState(false);
					// no scrambling
					model.setScramblingControl("00");
					view.getScramblingControlTF().setText("00");
					
				}
			}

			// Input Video Open
			if (event.getSource() == view.getOpen()) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				File inputFile = fileChooser.showOpenDialog(model.getPrimaryStage());
				if (inputFile != null) {
					setInputFile(inputFile);
				}

				// set 64 bit Control Word Input
				model.setControlWordInput(getRandomHex(16));
				// view.getCwTF().setText(model.controlWordInput);

				// set 128 bit Authorization Keys input and output
				String key0 = getRandomHex(32);
				String key1 = getRandomHex(32);

				// setze Authorizations Keys im Model
				model.setAuthorizationInputKey0(key0);
				model.setAuthorizationOutputKey0(key0);
				model.setAuthorizationInputKey1(key1);
				model.setAuthorizationOutputKey1(key1);

				// Update GUI
				view.getAk0InTF().setText(model.getAuthorizationInputKey0());
				view.getAk1InTF().setText(model.getAuthorizationInputKey1());
				view.getAk0OutTF().setText(model.getAuthorizationInputKey0());
				view.getAk1OutTF().setText(model.getAuthorizationInputKey1());

				// Video Player Input Initialisieren
				Task<Void> taskInitPlayerInput = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								view.initPlayerInput();

							}
						});
						return null;
					}
				};
				// start the task
				thInitPlayerInput = new Thread(taskInitPlayerInput);
				thInitPlayerInput.setDaemon(true);
				thInitPlayerInput.start();

				// Video Player Output Initialisieren
				Task<Void> taskInitPlayerOutput = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// view.getOutputPlayer().getOnPlaying();
								setOutputFile(model.getInputFile());
								view.initPlayerOutput();

							}
						});
						return null;
					}
				};
				// start the task
				thInitPlayerOutput = new Thread(taskInitPlayerOutput);
				thInitPlayerOutput.setDaemon(true);
				thInitPlayerOutput.start();

			}
			
			// Config Popup
			if (event.getSource() == view.getConfig()) {
				ConfigViewController.show();
			}

			// Exit
			if (event.getSource() == view.getExit()) {
				// stop all Threads
//				thActivateEncryption.stop();
//				thInitPlayerOutput.stop();
//				thInitPlayerInput.stop();
				// GUI exit
				Platform.exit();
				System.exit(0);
			}

		}

	}

	/**
	 * Setze die Parameter im Model für die Input Datei
	 * 
	 * @param inputFile
	 *            - Input File
	 */
	public void setInputFile(File inputFile) {
		model.setInputFile(inputFile);
		model.setMediaInputUrl(inputFile.toURI().toString());
		model.setMediaInput(new Media(model.getMediaInputUrl()));
	}

	/**
	 * Setze die Parameter im Model für die Output Datei
	 * 
	 * @param outputFile
	 *            - Output File
	 */
	public void setOutputFile(File outputFile) {
		model.setOutputFile(outputFile);
		model.setMediaOutputUrl(outputFile.toURI().toString());
		model.setMediaOutput(new Media(model.getMediaOutputUrl()));
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
	 * @return Gibt einen MAC in Hex zurück.
	 * @throws Exception
	 */
	public String getMAC() {
		payload = 
		model.getEcmHeader() + 
		model.getEcmProtocol() +
		model.getEcmBroadcastId() +
		model.getEcmWorkKeyId() + 
		model.getEcmCwOdd() + 
		model.getEcmCwEven() +
		model.getEcmProgramType() + 
		model.getEcmDateTime() + 
		model.getEcmRecordControl() +
		model.getEcmVariablePart();
		
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
	public String getCRC() {
		java.util.zip.CRC32 x = new java.util.zip.CRC32();
		byte[] bytes = payload.getBytes(Charset.forName("UTF-8"));
		x.update(bytes);
		return Long.toHexString(x.getValue()).toUpperCase();
	}

	/**
	 * Dummy Funktion zum Befüllen der BarCharts
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ObservableList<XYChart.Series<String, Number>> getChartData() {
		int aValue = 128; // Byte Array 128

		ObservableList<XYChart.Series<String, Number>> observableArrayList = FXCollections.observableArrayList();
		// model.observableArrayList = FXCollections.observableArrayList();
		Series<String, Number> aSeries = new Series<String, Number>();

		// aSeries.setName("a");

		for (int i = 0; i < 127; i++) {
			aSeries.getData().add(new XYChart.Data(Integer.toString(i), aValue));
			aValue = (int) (aValue + Math.random() - 1);
		}
		observableArrayList.addAll(aSeries);
		return observableArrayList;
	}

	/**
	 * Aktiviert die Verschlüsselung Aktualisiert das Control Word in der GUI
	 * anhand der Zeit im Timer Eingabefeld.
	 */
	public void activateEncryption() {
		Task<String> taskActivateEncryption = new Task<String>() {
			
			@Override
			protected String call() throws Exception {
				// erster Status
				Status mpStatus = view.getMediaPlayerInput().getStatus();
				String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
				LocalDateTime dateTime;
				// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
				
				while (!isCancelled()) {
					if (model.getEncryptionState() == true && mpStatus == Status.PLAYING) {
						
						// erzeuge ein cw
						String cw = getRandomHex(16);
						// setzte das CW
						model.setControlWordInput(cw);

						// Scrambling Control Pointer for CW even
						if (model.getScramblingControl() == "10") {
							model.setEcmCwEven(cw);
						}
						// Scrambling Control Pointer for CW odd
						else {
							model.setEcmCwOdd(cw);
							
						}

						
						// hole die Zeit vom Timer Eingabefeld
						model.setCwTime(Integer.parseInt(view.getCwTimeTF().getText().toString()));

						// Radio Button Status setzen
						if (rbStatus == "00") {
							model.setEcmWorkKeyId("00");
						} else {
							model.setEcmWorkKeyId("01");
						}

						

						// ECM Date/Time setzen
						dateTime = LocalDateTime.now();
						model.setEcmDateTime(dateTime.format(formatter));

						// setze ECM MAC
						model.setEcmMac(getMAC());
						
						// setze ECM CRC
						model.setEcmCrc(getCRC());
						
						// GUI updaten
						Platform.runLater(new Runnable() {
							public void run() {
								view.getCwTF().setText(model.getControlWordInput());
								
								view.getScramblingControlTF().setText(model.getScramblingControl());
								view.getEcmWorkKey().setText(model.getEcmWorkKeyId());
								
								view.getEcmCwOddTF().setText(model.getEcmCwOdd());
								view.getEcmCwEvenTF().setText(model.getEcmCwEven());
								
								view.getEcmDateTime().setText(model.getEcmDateTime());
							
								view.getEcmMacTF().setText(model.getEcmMac());
								view.getEcmCrcTF().setText(model.getEcmCrc());
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
					} else {
						// no scrambling
						model.setScramblingControl("00");
						// Thread beenden
						isCancelled();
						
					}
					// Status jedes mal überprüfen
					mpStatus = view.getMediaPlayerInput().getStatus();
					rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
					
				}
				return null;
			}
		};
		
		// start the task
		thActivateEncryption = new Thread(taskActivateEncryption);
		thActivateEncryption.setDaemon(true);
		thActivateEncryption.start();
	}
	


}
