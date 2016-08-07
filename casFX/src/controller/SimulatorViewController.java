package controller;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Toggle;
import javafx.stage.FileChooser;
import model.ConfigModel;
import model.SimulatorModel;
import view.SimulatorView;

/**
 * Input View Controller
 */
public class SimulatorViewController {
	
	/**
	 * Simulator Model
	 */
	private static SimulatorModel model;
	
	/**
	 * Simulator View
	 */
	private static SimulatorView view;
	
	/**
	 * Input View Controller
	 * 
	 * @param sModel
	 *            - Simulator Model
	 */
	public SimulatorViewController(SimulatorModel sModel) {
		model = sModel;
		view = new SimulatorView();
		
		CasEventHandler casEventHandler = new CasEventHandler();

		// Cas Event handler registrieren
		view.getOpen().setOnAction(casEventHandler);
		view.getExit().setOnAction(casEventHandler);

		// Config Popup
		view.getConfig().setOnAction(casEventHandler);

		// Encryption Toggle Button registrieren
		view.getEncryption().setOnAction(casEventHandler);
		
		// Decryption Toggle Button registrieren
		view.getDecryption().setOnAction(casEventHandler);
		
		// Video Player
		view.getVideoInputButton().setOnAction(casEventHandler);
		view.getVideoOutputButton().setOnAction(casEventHandler);
		
		

		view.getRadioButtonGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
			}
		});

		// Test Function
		view.test().setOnAction(event -> {

			// TODO
			VlcServerController.streamVLCmediaPlayer();
			
			//VlcServerController.streamVlcFile(model.getInputFile().toString());
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

		@Override
		public void handle(ActionEvent event) {


			// Input Video Open
			if (event.getSource() == view.getOpen()) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				File inputFile = fileChooser.showOpenDialog(model.getPrimaryStage());
				if (inputFile != null) {
					model.setInputFile(inputFile);
					// Button Encryption aktivieren
					view.getEncryption().setDisable(false);
				}

				// set 128 bit Authorization Keys input and output
				String key0 = EncryptionController.getRandomHex(32);
				String key1 = EncryptionController.getRandomHex(32);

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
				
			}
			
			// Encryption State ON (true) or OFF (false)
			if (event.getSource() == view.getEncryption()) {
				System.out.println("INFO: " + model.getInputFile());
				// get Encryption Button State + check if file is exists
				if (view.getEncryption().isSelected()) {
					view.getEncryption().setText("ON");
					view.getVideoInputButton().setDisable(false);
					model.setEncryptionState(true);
					// run Encryption
					EncryptionController.runEncryption();
				} else {
					// stop Encryption
					EncryptionController.stopEncryption();
					view.getEncryption().setText("OFF");
					view.getVideoInputButton().setDisable(true);
					model.setEncryptionState(false);
					// no scrambling
					model.setScramblingControl("00");
					view.getScramblingControlTF().setText("00");
					// Entsperre die CW Time
					view.getCwTimeTF().setDisable(false);
				}
			}

			// Decryption State ON (true) or OFF (false)
			if (event.getSource() == view.getDecryption()) {
				if (view.getDecryption().isSelected()) {
					view.getDecryption().setText("ON");
					model.setDecryptionState(true);
					view.getVideoOutputButton().setDisable(false);
					// run Decryption
					DecryptionController.runDecryption();
				} else {
					// stop Decryption
					view.getDecryption().setText("OFF");
					model.setDecryptionState(false);
					view.getVideoOutputButton().setDisable(true);
					PlayerViewController.exitOutputPlayerView();
				}
			
			}
			
			// Input Player
			if (event.getSource() == view.getVideoInputButton()) {
				PlayerViewController.runPlayerInput();
			}

			// Output Player
			if (event.getSource() == view.getVideoOutputButton()) {
				PlayerViewController.getPlayerOutput();
			}
			
			// Config Popup
			if (event.getSource() == view.getConfig()) {
				ConfigViewController.show();
			}

			// Exit
			if (event.getSource() == view.getExit()) {
				// GUI exit
				Platform.exit();
				System.exit(0);
			}

		} // end handle

	} // end casEventHandler



	public static SimulatorView getView() {
		return view;
	}

	public static SimulatorModel getModel() {
		return model;
	}

}
