package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Toggle;
import javafx.stage.FileChooser;
import model.SimulatorModel;
import uk.co.caprica.vlcj.test.multi.PlayerInstance;
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
	 * Player Instance List
	 */
	private static List<PlayerInstance> players = new ArrayList<PlayerInstance>();
	
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
		
		// Send EMM Button registrieren
		view.getSendEMMButton().setOnAction(casEventHandler);
		
		// Decryption Toggle Button registrieren
		view.getDecryption().setOnAction(casEventHandler);
		
		// Video Player
		view.getVideoInputButton().setOnAction(casEventHandler);
		view.getVideoOutputButton().setOnAction(casEventHandler);
		
		
		// Radio Buttons
		view.getRadioButtonGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
			}
		});

		// Test Function
		view.test().setOnAction(event -> {

			// TODO
			
			//VlcServerController.streamVlcFile(model.getInputFile().toString());
			
			Runnable myRunnable = new Runnable(){
			     @Override
				public void run(){
			    	 
			    	 try {
						OutputPlayerController.initIntervallOutputPlayer();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			     }
			   };
			   
			//FFmpegController.runFFmpeg();
			
//			System.out.println(model.getInputFile().toString());
//			
//			System.out.println("error" + configModel.getServer());
			
			   myRunnable.run();
			

		});
		
	}

	/**
	 * Zeigt die Simulator View an.
	 */
	public void show() {
		view.show(model.getPrimaryStage());
	}

	/**
	 * Cas Event Handler. Steuert alle Events im CAS-Simulator.
	 */
	public class CasEventHandler implements EventHandler<ActionEvent> {

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
					// Button Send EMM aktivieren
					view.getSendEMMButton().setDisable(false);
				}

				// TODO
				// set 128 bit Authorization Keys input and output
				String key0 = EncryptionController.getRandomHex(32);
				String key1 = EncryptionController.getRandomHex(32);

				// setze Authorizations Keys im Model
				model.setAuthorizationInputKey0(key0);
				//model.setAuthorizationOutputKey0(key0);
				model.setAuthorizationInputKey1(key1);
				//model.setAuthorizationOutputKey1(key1);

				// GUI Update Autorisation Keys
				view.getAk0InTF().setText(model.getAuthorizationInputKey0());
				view.getAk1InTF().setText(model.getAuthorizationInputKey1());
//				view.getAk0OutTF().setText(model.getAuthorizationInputKey0());
//				view.getAk1OutTF().setText(model.getAuthorizationInputKey1());
				
			}
			
			// Encryption State ON (true) or OFF (false)
			if (event.getSource() == view.getEncryption()) {
				System.out.println("INFO: " + model.getInputFile());
				// get Encryption Button State + check if file is exists
				if (view.getEncryption().isSelected()) {
					// run Encryption
					EncryptionController.runEncryption();
				} else {
					// stop Encryption
					EncryptionController.stopEncryption();
				}
			}

			// Decryption State ON (true) or OFF (false)
			if (event.getSource() == view.getDecryption()) {
				if (view.getDecryption().isSelected()) {
					// run Decryption
					DecryptionController.runDecryption();
				} else {
					// stop Decryption
					DecryptionController.stopDecryption();
				}
			}
			
			// Input Player
			if (event.getSource() == view.getVideoInputButton()) {
				InputPlayerController.getInputPlayer();
			}

			// Output Player
			if (event.getSource() == view.getVideoOutputButton()) {
				OutputPlayerController.getOutputPlayer();
			}
			
			// EMM Send Button
			if (event.getSource() == view.getSendEMMButton()) {
				EncryptionController.generateEMM();
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



	/**
	 * Liefert die aktuelle Simulator View {@link view}.
	 * 
	 * @return Gibt die aktuelle Simulator View zur�ck.
	 */
	public static SimulatorView getView() {
		return view;
	}

	/**
	 * Liefert die aktuelle Simulator Model {@link model}.
	 * 
	 * @return Gibt das aktuelle Simulator Model zur�ck.
	 */
	public static SimulatorModel getModel() {
		return model;
	}

	/**
	 * Liefert die aktuelle Player Liste {@link players}.
	 * 
	 * @return Gibt das aktuelle Player Liste zur�ck.
	 */
	public static List<PlayerInstance> getPlayers() {
		return players;
	}

}
