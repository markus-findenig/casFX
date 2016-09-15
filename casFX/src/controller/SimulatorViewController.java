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
import view.AboutView;
import view.SimulatorView;

/**
 * Simulator View Controller. Controls the Simulator.
 */
public class SimulatorViewController {

	/**
	 * Simulator Model.
	 */
	private static SimulatorModel model;

	/**
	 * Simulator View.
	 */
	private static SimulatorView view;

	/**
	 * Player Instance List.
	 */
	private static List<PlayerInstance> players;

	/**
	 * Input View Controller.
	 * 
	 * @param sModel
	 *            Simulator Model
	 */
	public SimulatorViewController(SimulatorModel sModel) {
		model = sModel;
		view = new SimulatorView();

		// set constant Master Private Keys for Input and Output
		model.setMasterPrivateKeyInput("00112233445566778899AABBCCDDEEFFFFEEDDCCBBAA99887766554433221100");
		model.setMasterPrivateKeyOutput("00112233445566778899AABBCCDDEEFFFFEEDDCCBBAA99887766554433221100");
		// Update GUI Master Private Keys
		view.getMpkInTA().setText(model.getMasterPrivateKeyInput());
		view.getMpkOutTA().setText(model.getMasterPrivateKeyOutput());

		players = new ArrayList<PlayerInstance>();

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

		// About Menu
		view.getAbout().setOnAction(casEventHandler);

	}

	/**
	 * Show the Simulator View.
	 */
	public void show() {
		view.show(SimulatorModel.getPrimaryStage());
	}

	/**
	 * Cas Event Handler. Controls all events in CAS Simulator.
	 */
	public class CasEventHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {

			// -------------------------------------------------------
			// Input Video Open
			if (event.getSource() == view.getOpen()) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				File inputFile = fileChooser.showOpenDialog(SimulatorModel.getPrimaryStage());
				if (inputFile != null && ConfigViewController.checkPath()) {
					model.setInputFile(inputFile);
					// Button Encryption aktivieren
					view.getEncryption().setDisable(false);
					// Button Send EMM aktivieren
					view.getSendEMMButton().setDisable(false);

					// Server aktivieren
					EncryptionController.initServer();

					// set 128 bit Authorization Keys input and output
					String key0 = EncryptionController.getRandomHex(32);
					String key1 = EncryptionController.getRandomHex(32);

					// setze Authorizations Keys im Model
					model.setAuthorizationInputKey0(key0);
					model.setAuthorizationInputKey1(key1);
					// model.setAuthorizationOutputKey0(key0);
					// model.setAuthorizationOutputKey1(key1);

					// GUI Update Autorisation Keys
					view.getAk0InTF().setText(model.getAuthorizationInputKey0());
					view.getAk1InTF().setText(model.getAuthorizationInputKey1());
					// view.getAk0OutTF().setText(model.getAuthorizationInputKey0());
					// view.getAk1OutTF().setText(model.getAuthorizationInputKey1());

				} // end if
			}

			// -------------------------------------------------------
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

			// -------------------------------------------------------
			// Decryption State ON (true) or OFF (false)
			if (event.getSource() == view.getDecryption()) {
				if (view.getDecryption().isSelected()) {
					// run Decryption
					System.out.println("Decryption run");
					view.getDecryption().setSelected(true);
					DecryptionController.runDecryption();
				} else {
					// stop Decryption
					System.out.println("Decryption stop");
					view.getDecryption().setSelected(false);
					DecryptionController.stopDecryption();
				}
			}

			// -------------------------------------------------------
			// Input Player
			if (event.getSource() == view.getVideoInputButton()) {
				InputPlayerController.getInputPlayer();
			}

			// -------------------------------------------------------
			// Output Player
			if (event.getSource() == view.getVideoOutputButton()) {
				OutputPlayerController.getOutputPlayer();
			}

			// -------------------------------------------------------
			// EMM Send Button
			if (event.getSource() == view.getSendEMMButton()) {
				EncryptionController.generateEMM();
			}

			// -------------------------------------------------------
			// Config Popup
			if (event.getSource() == view.getConfig()) {
				ConfigViewController.show();
			}

			// -------------------------------------------------------
			// About
			if (event.getSource() == view.getAbout()) {
				new AboutView();
			}

			// -------------------------------------------------------
			// Exit
			if (event.getSource() == view.getExit()) {
				// GUI exit
				Platform.exit();
				System.exit(0);
			}

		} // end handle

	} // end casEventHandler

	/**
	 * Gets the current Simulator View {@link view}.
	 * 
	 * @return The current Simulator View.
	 */
	public static SimulatorView getView() {
		return view;
	}

	/**
	 * Gets the current Simulator Model {@link model}.
	 * 
	 * @return The current Simulator Model.
	 */
	public static SimulatorModel getModel() {
		return model;
	}

	/**
	 * Gets the current player list {@link players}.
	 * 
	 * @return The current player list.
	 */
	public static List<PlayerInstance> getPlayers() {
		return players;
	}

}
