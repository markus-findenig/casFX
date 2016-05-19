package controller;

import java.io.File;
import java.security.SecureRandom;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;

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
	 * @param simulatorModel - Simulator Model
	 */
	public InputViewController(SimulatorModel simulatorModel) {
		this.model = simulatorModel;
		// instance with dummy file video
		setInputFile(new File("resorces\\5seconds.mp4"));
		setOutputFile(new File("resorces\\5seconds.mp4"));

		// TODO dummy Data for BarChart
		model.observableArrayList = getChartData();

		view = new InputView(model);

		MenuEventHandler menuEventHandler = new MenuEventHandler();

		// Menu Eventhandler registrieren
		view.getOpen().setOnAction(menuEventHandler);
		view.getExit().setOnAction(menuEventHandler);

		//CasEventHandler casEventHandler = new CasEventHandler();
		
		// Test Function
		view.test().setOnAction(event -> {
            Task<Void> task = new Task<Void>() {
                @Override 
                public Void call() throws Exception {
                	Status status = view.mediaPlayerInput.getStatus();
					while (status == Status.PLAYING) {
						model.controlWordInput = getRandomHex(16);
						updateMessage(model.controlWordInput);
                        model.cwTime = Integer.parseInt(view.getCwTimeTF().getText().toString());
                        Thread.sleep(model.cwTime*1000); // time in seconds
                    }
                    return null;
                }
            };
            task.messageProperty().addListener((obs, oldMessage, newMessage) -> view.getCwTF().setText(newMessage));
            new Thread(task).start();
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

				// view.initPlayer1();
				// view.initPlayer2();
				// view.init();

				// Video Player Input
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
				new Thread(taskInitPlayerInput).start();
			}

			// Exit
			if (event.getSource() == view.getExit()) {
				System.exit(0);
			}

		}
	}

//	class CasEventHandler implements EventHandler<ActionEvent> {
//
//		@Override
//		public void handle(ActionEvent event) {
//
//			
//			// Update CW
//			if (event.getSource() ==  view.test()) {
//		
//
//				Task<Void> taskSetCW = new Task<Void>() {
//					@Override
//					protected Void call() throws Exception {
//						Status status = view.mediaPlayerInput.getStatus();
//						while (status == Status.PLAYING) {
//							// set cw
//							//view.getCwTF().setText(getRandomHex(16));
//							updateMessage(getRandomHex(16));
//							int waitTime = Integer.parseInt(view.getCwTimeTF().toString());
//							
//							Thread.sleep(1000);
//						}
//						return null;
//					}
//				};
//				taskSetCW.messageProperty().addListener((obs, oldMessage, newMessage) -> view.getCwTF().setText(newMessage));
//				// start the background task
//				new Thread(taskSetCW).start();
//			}
//		}
//	}

	/**
	 * Setze die Parameter im Model für die Input Datei
	 * 
	 * @param inputFile - Input File
	 */
	public void setInputFile(File inputFile) {
		model.inputFile = inputFile;
		model.mediaInputUrl = inputFile.toURI().toString();
		model.mediaInput = new Media(model.mediaInputUrl);
	}

	/**
	 * Setze die Parameter im Model für die Output Datei
	 * 
	 * @param outputFile - Output File
	 */
	public void setOutputFile(File outputFile) {
		model.inputFile = outputFile;
		model.mediaOutputUrl = outputFile.toURI().toString();
		model.mediaOutput = new Media(model.mediaOutputUrl);
	}

	/**
	 * Erzeugt eine Random Hex Nummer
	 * 
	 * @param length - Länge der Random Hex Nummer
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

}
