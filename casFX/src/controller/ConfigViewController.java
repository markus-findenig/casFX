package controller;

import java.io.File;
import java.net.UnknownHostException;

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
	 * @param configModel
	 *            -
	 * @throws UnknownHostException
	 */
	public ConfigViewController(ConfigModel cModel) throws UnknownHostException {
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
	 * Zeigt den Config Dialog
	 */
	public static void show() {
		// setze die model daten in der view
		configView.getFfmpegPath().setText(configModel.getFFmpegPath());
		configView.getVlcPath().setText(configModel.getVlcPath());
		configView.getServer().setText(configModel.getServer());
		configView.getClient().setText(configModel.getClient());
		configView.getConstantCW().setText(configModel.getConstantCw());
		// zeige den Config Dialog an
		configView.show(configModel.getDialogStage());
	}

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
					configModel.setConstantCw(configView.getConstantCW().getText());

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
	 * �berpr�ft die Benutzer Eingaben in den Text Feldern.
	 * 
	 * @return true wenn die Eingaben passen.
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
		if (configView.getConstantCW().getText() == null || configView.getConstantCW().getText().length() != 16) {
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
	 * @return Liefert das Config Model zur�ck.
	 */
	public static ConfigModel getConfigModel() {
		return configModel;
	}

	/**
	 * �berpr�ft ob die Dateien vlc.exe und ffmpeg.exe vorhanden sind.
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
	 * �berpr�ft ob die Datei Existiert.
	 * 
	 * @param filePathString
	 *            - Pfad zur Datei.
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
