package controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.ConfigModel;
import view.ConfigView;

public class ConfigViewController {

	// Config View
	private static ConfigView configView;

	// Config Modell
	private static ConfigModel configModel;

	/**
	 * Config View Controller
	 * 
	 * @param configModel
	 * @throws UnknownHostException 
	 */
	public ConfigViewController(ConfigModel cModel) throws UnknownHostException {
		configModel = cModel;
		configView = new ConfigView();
		
		// set default Config parameter
		configModel.setServer("rtsp://" + InetAddress.getLocalHost().getHostAddress() + ":8080/cas.sdp");
		configModel.setClient("rtsp://" + InetAddress.getLocalHost().getHostAddress() + ":8080/cas.sdp");

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
		configView.getServer().setText(configModel.getServer());
		configView.getClient().setText(configModel.getClient());
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
					configModel.setServer(configView.getServer().getText());
					configModel.setClient(configView.getClient().getText());
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
     * Überprüft die Benutzer Eingaben in den Text Feldern.
     * 
     * @return true wenn die Eingaben passen.
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (configView.getServer().getText() == null || configView.getServer().getText().length() == 0) {
            errorMessage += "No valid server config! (rtsp://127.0.0.1:8080/cas.sdp)\n"; 
        }
        if (configView.getClient().getText() == null || configView.getClient().getText().length() == 0) {
            errorMessage += "No valid client config! (rtsp://127.0.0.1:8080/cas.sdp)\n"; 
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
	
	
}
