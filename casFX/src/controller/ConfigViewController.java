package controller;

import java.io.File;
import com.sun.jna.NativeLibrary;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.ConfigModel;
import model.SimulatorModel;
import view.ConfigView;

/**
 * Config View Controller.
 */
public class ConfigViewController {

	/**
	 * Config View
	 */
	private static ConfigView configView;

	/**
	 * Config Model
	 */
	private static ConfigModel configModel;

	/**
	 * Config View Controller
	 * 
	 * @param cModel The Config Model.
	 */
	public ConfigViewController(ConfigModel cModel) {
		configModel = cModel;
		configView = new ConfigView();

		// set default Config parameter
		 configModel.setFFmpegPath("D:\\Securety\\Programms\\ffmpeg\\bin");
		 configModel.setVlcPath("C:\\ProgLoc\\VideoLAN\\VLC");
		// TODO default
//		configModel.setVlcPath("C:\\Program Files\\VideoLAN\\VLC");
//		configModel.setFFmpegPath("C:\\FFmpeg\\bin");
		configModel.setServer("rtp://239.0.0.1:5004");
		configModel.setClient("rtp://239.0.0.1:5004");

		configModel.setConstantCw("0123456789ABCDEF");

		// set VLC Native Library
		NativeLibrary.addSearchPath("libvlc", configModel.getVlcPath());
		System.setProperty("VLC_PLUGIN_PATH", configModel.getVlcPath() + "\\plugins");

		// System.setProperty("jna.library.path", "");

		ConfigEventHandler configEventHandler = new ConfigEventHandler();

		// Button Events registrieren
		configView.getOk().setOnAction(configEventHandler);
		configView.getCancel().setOnAction(configEventHandler);

	}

	/**
	 * Show the Config Dialog.
	 */
	public static void show() {
		// setze die model daten in der view
		configView.getFfmpegPath().setText(configModel.getFFmpegPath());
		configView.getVlcPath().setText(configModel.getVlcPath());
		configView.getServer().setText(configModel.getServer());
		configView.getClient().setText(configModel.getClient());
		configView.getConstantCw().setText(configModel.getConstantCw());
		// zeige den Config Dialog an
		configView.show(configModel.getDialogStage());
	}

	/**
	 * Config Event Handler. Controls all events of Config View.
	 */
	class ConfigEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {

			// Button OK
			if (event.getSource() == configView.getOk()) {
				if (isInputValid()) {
					// speichert die view daten ins model
					configModel.setFFmpegPath(configView.getFfmpegPath().getText());
					configModel.setVlcPath(configView.getVlcPath().getText());
					configModel.setServer(configView.getServer().getText());
					configModel.setClient(configView.getClient().getText());
					configModel.setConstantCw(configView.getConstantCw().getText());

					// update VLC Native Library
					NativeLibrary.addSearchPath("libvlc", configModel.getVlcPath());
					System.setProperty("VLC_PLUGIN_PATH", configModel.getVlcPath() + "\\plugins");

					configModel.getDialogStage().close();
				}
			}

			// Button CANCEL
			if (event.getSource() == configView.getCancel()) {
				configModel.getDialogStage().close();
			}

		}
	}

	/**
	 * Checks the user input in the text fields.
	 * 
	 * @return true if the Inputs valid or false not.
	 */
	public boolean isInputValid() {
		String errorMessage = "";
		// FFmpeg
		if (configView.getFfmpegPath().getText() == null || configView.getFfmpegPath().getText().length() == 0) {
			errorMessage += "No valid FFmpeg config! (Default: C:\\FFmpeg\\bin)\n";
		}
		// VLC
		if (configView.getServer().getText() == null || configView.getServer().getText().length() == 0) {
			errorMessage += "No valid VLC config! (Default: C:\\Program Files\\VideoLAN\\VLC)\n";
		}
		// Server
		if (configView.getServer().getText() == null || configView.getServer().getText().length() == 0) {
			errorMessage += "No valid server config! (Default: rtp://239.0.0.1:5004)\n";
		}
		// Client
		if (configView.getClient().getText() == null || configView.getClient().getText().length() == 0) {
			errorMessage += "No valid client config! (Default: rtp://239.0.0.1:5004)\n";
		}
		// Constant CW
		if (configView.getConstantCw().getText() == null || configView.getConstantCw().getText().length() != 16) {
			errorMessage += "No valid Constant Control Word (CW)! (Default: 0123456789ABCDEF)\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(configModel.getDialogStage());
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;
		}
	}

	/**
	 * @return The Config Model.
	 */
	public static ConfigModel getConfigModel() {
		return configModel;
	}

	/**
	 * Checks if the files vlc.exe and ffmpeg.exe available.
	 * 
	 * @return true if files exists or false if files not exists.
	 */
	public static boolean checkPath() {
		if (isFileExists(configModel.getVlcPath() + "\\vlc.exe")
				&& isFileExists(configModel.getFFmpegPath() + "\\ffmpeg.exe")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the file exists.
	 * 
	 * @param filePathString
	 *            Path to the file.
	 * @return true if File Exists or false if File not Exists.
	 */
	private static boolean isFileExists(String filePathString) {
		File f = new File(filePathString);
		if (f.exists()) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(SimulatorModel.getPrimaryStage());
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct the Config Parameter in Options.");
			alert.setContentText("File Path " + filePathString + " not exists.");
			alert.showAndWait();
			return false;
		}

	}

}
