package controller;

import java.io.File;
import java.security.SecureRandom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private InputView view;

	public InputViewController(SimulatorModel model) {
		this.model = model;
		// instance with dummy file video
		setInputFile(new File("resorces\\5seconds.mp4"));

		// TODO dummy Data for BarChart
		this.model.observableArrayList = getChartData();

		this.view = new InputView(model);

		MenuEventHandler menuEventHandler = new MenuEventHandler();

		// Eventhandler registrieren
		view.getOpen().setOnAction(menuEventHandler);
		view.getExit().setOnAction(menuEventHandler);

		// view.getAddBtn().setOnAction(new addBtnEventHandler());
		// view.getOkBtn().setOnAction(new OkBtnEventHandler());
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

				// model.cwTime =
				// Integer.parseInt(InputView.getCwTimeTF().toString());
				// model.controlWord = InputView.setCwTimeTF("10");

				// get cw Time
				// model.cwTime =
				// Integer.parseInt(view.getCwTimeTF().getText());

				// set cw
				view.getCwTF().setText(getRandomHex(16));

				// set ak0 and ak1 for input and output
				String ak0 = getRandomHex(32);
				String ak1 = getRandomHex(32);
				view.getAk0TF().setText(ak0);
				view.getAk1TF().setText(ak1);
				view.getAk0OutTF().setText(ak0);
				view.getAk1OutTF().setText(ak1);

				view.initPlayer1();
				// view.initPlayer2();
				// view.init();

				Status status = view.mediaPlayerInput.getStatus();
				
				//System.out.println(status);
			
				//if (status == Status.PAUSED) {
				while (status == Status.PLAYING) {
					
				}
				


			}

			// Exit
			if (event.getSource() == view.getExit()) {
				System.exit(0);
			}

		}
	}

	/**
	 * Setze die Parameter im Model für die Input Datei
	 * @param inputFile
	 */
	public void setInputFile(File inputFile) {
		model.inputFile = inputFile;
		model.mediaInputUrl = inputFile.toURI().toString();
		model.mediaInput = new Media(model.mediaInputUrl);

	}

	/**
	 * Erzeugt eine Random Hex Nummer 
	 * @param length Länge der Random Hex Nummer
	 * @return Gibt eine Random Hex Nummer der Länge length zurück.
	 */
	public String getRandomHex(int length) {
		SecureRandom randomService = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(Integer.toHexString(randomService.nextInt()));
		}
		sb.setLength(length);
		return sb.toString().toUpperCase();
	}

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
