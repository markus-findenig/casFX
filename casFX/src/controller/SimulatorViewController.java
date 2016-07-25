package controller;

import java.io.File;

import com.sun.jna.NativeLibrary;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Toggle;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import model.ConfigModel;
import model.SimulatorModel;
import view.SimulatorView;
import controller.FFmpegController;

/**
 * Input View Controller
 */
public class SimulatorViewController {
	
	// Model
	private static SimulatorModel model;
	
	// Config Model
	private static ConfigModel configModel;

	// View
	private static SimulatorView view;
	
	
	/**
	 * Input View Controller
	 * 
	 * @param sModel
	 *            - Simulator Model
	 * @param cModel
	 *            - Config Model
	 */
	public SimulatorViewController(SimulatorModel sModel, ConfigModel cModel) {
		model = sModel;
		configModel = cModel;
		
		// instance with dummy file video
		//setInputFile(new File("resorces\\dummy.mp4"));
		// setOutputFile(new File("resorces\\dummy.mp4"));

		// TODO dummy Data for BarChart
		//SimulatorModel.observableArrayList = getChartData();

		view = new SimulatorView();

		CasEventHandler casEventHandler = new CasEventHandler();

		// Cas Event handler registrieren
		view.getOpen().setOnAction(casEventHandler);
		view.getExit().setOnAction(casEventHandler);

		// Config Popup
		view.getConfig().setOnAction(casEventHandler);

		// Encryption Toggle Button registrieren
		view.getEncryption().setOnAction(casEventHandler);
		
		// Video Player
		view.getVideoInputButton().setOnAction(casEventHandler);
		view.getVideoOutputButton().setOnAction(casEventHandler);
		
		

		view.getRadioButtonGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
			}
		});

		// Test Function
		view.test().setOnAction(event -> {

			// TODO
			VlcServerController.streamVlcFile(model.getInputFile().toString());
//			
//			Runnable myRunnable = new Runnable(){
//			     public void run(){
//			    	 
//			    	 VlcServerController.streamVlcFile(model.getInputFile().toString());
//			     }
//			   };
			   
			//FFmpegController.runFFmpeg();
			
//			System.out.println(model.getInputFile().toString());
//			
//			System.out.println("error" + configModel.getServer());
//			
//			   myRunnable.run();
			

		});
	}

	public void show() {
		view.show(model.getPrimaryStage());
	}

	class CasEventHandler implements EventHandler<ActionEvent> {

		@SuppressWarnings("deprecation")
		@Override
		public void handle(ActionEvent event) {


			// Input Video Open
			if (event.getSource() == view.getOpen()) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				File inputFile = fileChooser.showOpenDialog(model.getPrimaryStage());
				if (inputFile != null) {
					model.setInputFile(inputFile);
					// Button aktivieren
					view.getEncryption().setDisable(false);
					view.getVideoInputButton().setDisable(false);
					view.getVideoOutputButton().setDisable(false);
				}

//				String cw = Encryption.getRandomHex(16);
				// set ECM 64 bit Control Word
				model.setEcmCwOdd(view.getEcmCwOddTF().getText());
				model.setEcmCwEven(view.getEcmCwEvenTF().getText());

				// set 128 bit Authorization Keys input and output
				String key0 = Encryption.getRandomHex(32);
				String key1 = Encryption.getRandomHex(32);

				// setze Authorizations Keys im Model
				model.setAuthorizationInputKey0(key0);
				model.setAuthorizationOutputKey0(key0);
				model.setAuthorizationInputKey1(key1);
				model.setAuthorizationOutputKey1(key1);

				// GUI Update Autorisation Keys
				view.getAk0InTF().setText(model.getAuthorizationInputKey0());
				view.getAk1InTF().setText(model.getAuthorizationInputKey1());
				view.getAk0OutTF().setText(model.getAuthorizationInputKey0());
				view.getAk1OutTF().setText(model.getAuthorizationInputKey1());
				
				// Initialisiere Input & Ouput Video Player
				//new PlayerViewController(model, configModel);

			}
			
			// Encryption State ON (true) or OFF (false)
			if (event.getSource() == view.getEncryption()) {
				System.out.println("INFO: " + model.getInputFile());
				// get Encryption Button State + check if file is exists
				if (view.getEncryption().isSelected()) {
					view.getEncryption().setText("ON");
					model.setEncryptionState(true);
					// set scrambling, CW odd
					model.setScramblingControl("11");
					
					// run Encryption
					// TODO 
					Encryption.runEncryption();
					
					
				} else {
					// stop Encryption Thread
					Encryption.stopEncryption();
					view.getEncryption().setText("OFF");
					model.setEncryptionState(false);
					// no scrambling
					model.setScramblingControl("00");
					view.getScramblingControlTF().setText("00");
					// Entsperre die CW Time
					view.getCwTimeTF().setDisable(false);
				}
			}

			// Input Player
			if (event.getSource() == view.getVideoInputButton()) {
				PlayerViewController.runPlayerInput();
			}

			// Output Player
			if (event.getSource() == view.getVideoOutputButton()) {
				PlayerViewController.runPlayerOutput();
			}
			
			// Config Popup
			if (event.getSource() == view.getConfig()) {
				ConfigViewController.show();
			}

			// Exit
			if (event.getSource() == view.getExit()) {
				// stop all Threads
				// thActivateEncryption.stop();
				// thInitPlayerOutput.stop();
				// thInitPlayerInput.stop();
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



//	/**
//	 * Dummy Funktion zum Befüllen der BarCharts
//	 * 
//	 * @return
//	 */
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public ObservableList<XYChart.Series<String, Number>> getChartData() {
//		int aValue = 128; // Byte Array 128
//
//		ObservableList<XYChart.Series<String, Number>> observableArrayList = FXCollections.observableArrayList();
//		// model.observableArrayList = FXCollections.observableArrayList();
//		Series<String, Number> aSeries = new Series<String, Number>();
//
//		// aSeries.setName("a");
//
//		for (int i = 0; i < 127; i++) {
//			aSeries.getData().add(new XYChart.Data(Integer.toString(i), aValue));
//			aValue = (int) (aValue + Math.random() - 1);
//		}
//		observableArrayList.addAll(aSeries);
//		return observableArrayList;
//	}



	public static SimulatorView getView() {
		return view;
	}

	public static SimulatorModel getModel() {
		return model;
	}

}
