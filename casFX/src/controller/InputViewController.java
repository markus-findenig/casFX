package controller;

import java.io.File;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import com.sun.javafx.collections.MappingChange.Map;

import app.MediaControl;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Toggle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.util.converter.LocalDateTimeStringConverter;
import model.SimulatorModel;
import view.InputView;

public class InputViewController {

	// Model
	private SimulatorModel model;

	// View
	private static InputView view;

	/**
	 * Constructor InputViewController
	 * 
	 * @param simulatorModel
	 *            - Simulator Model
	 */
	public InputViewController(SimulatorModel simulatorModel) {
		this.model = simulatorModel;
		// instance with dummy file video
		setInputFile(new File("resorces\\dummy.mp4"));
		setOutputFile(new File("resorces\\dummy.mp4"));

		// TODO dummy Data for BarChart
		model.observableArrayList = getChartData();

		view = new InputView(model);

		MenuEventHandler menuEventHandler = new MenuEventHandler();

		// Menu Eventhandler registrieren
		view.getOpen().setOnAction(menuEventHandler);
		view.getExit().setOnAction(menuEventHandler);

		view.getRadioButtonGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
			}
		});

		// Test Function
		view.test().setOnAction(event -> {
			
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
			LocalDateTime dateTime = LocalDateTime.now();
			String formattedDateTime = dateTime.format(formatter);
			
			System.out.println(formattedDateTime);
			
			
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

	class MenuEventHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {

			// Input Video Open
			if (event.getSource() == view.getOpen()) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				File inputFile = fileChooser.showOpenDialog(SimulatorModel.PRIMARY_STAGE);
				if (inputFile != null) {
					setInputFile(inputFile);
				}

				// set 64 bit Control Word Input
				model.controlWordInput = getRandomHex(16);
				view.getCwTF().setText(model.controlWordInput);

				// set 128 bit Authorization Keys input and output
				model.authorizationInputKey0 = model.authorizationOutputKey0 = getRandomHex(32);
				model.authorizationInputKey1 = model.authorizationOutputKey1 = getRandomHex(32);
				view.getAk0InTF().setText(model.authorizationInputKey0);
				view.getAk1InTF().setText(model.authorizationInputKey1);
				view.getAk0OutTF().setText(model.authorizationInputKey0);
				view.getAk1OutTF().setText(model.authorizationInputKey1);

				// Video Player Input Initialisieren
				Task<Void> taskInitPlayerInput = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								view.initPlayerInput();

								// TODO
								view.getVideoResolutionTF()
										.setText(model.mediaInput.getWidth() + "x" + model.mediaInput.getHeight());
							}
						});
						return null;
					}
				};
				// start the task
				Thread thInitPlayerInput = new Thread(taskInitPlayerInput);
				thInitPlayerInput.setDaemon(true);
				thInitPlayerInput.start();

				// view.getVideoResolutionTF().setText(model.mediaInput.getWidth()
				// + "x" + model.mediaInput.getHeight());

				// setze das CW in dem Input Player
				setControlWord();
				

				//setECM();

				// Video Player Output Initialisieren
				Task<Void> taskInitPlayerOutput = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// view.getOutputPlayer().getOnPlaying();
								setOutputFile(model.inputFile);
								view.initPlayerOutput();

							}
						});
						return null;
					}
				};
				// start the task
				Thread thInitPlayerOutput = new Thread(taskInitPlayerOutput);
				thInitPlayerOutput.setDaemon(true);
				thInitPlayerOutput.start();

			}

			// Exit
			if (event.getSource() == view.getExit()) {
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
		model.inputFile = inputFile;
		model.mediaInputUrl = inputFile.toURI().toString();
		model.mediaInput = new Media(model.mediaInputUrl);
	}

	/**
	 * Setze die Parameter im Model für die Output Datei
	 * 
	 * @param outputFile
	 *            - Output File
	 */
	public void setOutputFile(File outputFile) {
		model.outputFile = outputFile;
		model.mediaOutputUrl = outputFile.toURI().toString();
		model.mediaOutput = new Media(model.mediaOutputUrl);
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
	 * Aktualisiert das Control Word in der GUI anhand der Zeit im Timer
	 * Eingabefeld.
	 */
	public void setControlWord() {
		Task<String> taskSetCW = new Task<String>() {
			@Override
			protected String call() throws Exception {
				// erster Status
				Status mpStatus = view.mediaPlayerInput.getStatus();
				String rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
			
				while (!isCancelled()) {
					if (mpStatus == Status.PLAYING) {
						
						// setzte das CW
						model.controlWordInput = getRandomHex(16);
						//updateMessage(model.controlWordInput);
						
						// hole die Zeit vom Timer Eingabefeld
						model.cwTime = Integer.parseInt(view.getCwTimeTF().getText().toString());
						
						
						// Radio Button Status setzen
						if (rbStatus == "00") {
							model.ecmWorkKeyId = "00";
						} else {
							model.ecmWorkKeyId = "01";
						}
						
						// ECM Date/Time setzen
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
						LocalDateTime dateTime = LocalDateTime.now();
						model.ecmDateTime = dateTime.format(formatter);
						
						
						// GUI updaten
						view.getCwTF().setText(model.controlWordInput);
						view.getEcmWorkKey().setText(model.ecmWorkKeyId);
						view.getEcmDateTime().setText(model.ecmDateTime);
						
						
//						Platform.runLater(new Runnable() {
//							public void run() {
//								view.getECM().setText(model.ecmWorkKeyId);
//						}
//						});
	                	
	                	
//						// UI updaten
//			            Platform.runLater(new Runnable() {
//			                @Override
//			                public void run() {
//			                    // entsprechende UI Komponente updaten
//			                	if (view.getRadioButtonGroup().getSelectedToggle().getUserData().toString() == "ak0InRB") {
//									model.ecmWorkKeyId = "00";
//								} else {
//									model.ecmWorkKeyId = "01";
//								}
//			                	view.getECM().setText(model.ecmWorkKeyId);
//			                }
//			            });

			            
						
						
						// Thread wait
						try {
							// time in seconds
							Thread.sleep(model.cwTime * 1000);
						} catch (InterruptedException interrupted) {
						}
					} else {
						// Thread beenden
						isCancelled();
					}
					// Status jedes mal überprüfen
					mpStatus = view.mediaPlayerInput.getStatus();
					rbStatus = view.getRadioButtonGroup().getSelectedToggle().getUserData().toString();
				}
				// return model.controlWordInput;
				return null;
			}

		};
		// Setze in der GUI das CW
//		taskSetCW.messageProperty().addListener((obs, oldMessage, newMessage) -> {
//				view.getCwTF().setText(newMessage);
//		});
		
		// start the task
		Thread thSetCW = new Thread(taskSetCW);
		thSetCW.setDaemon(true);
		thSetCW.start();
	}

	
}
