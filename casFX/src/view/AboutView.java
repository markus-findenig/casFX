package view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * About View.
 */
public class AboutView {

	public AboutView() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information about CAS-Simulator");
		alert.setHeaderText("Conditional Access System (CAS) Simulator (2016)");
		alert.setContentText(
				"The CAS-Simulator required the FFmpeg Libary (http://ffmpeg.org) and the VLC Libary (http://www.videolan.org/vlc). \n"
				+ "\n"
				+ "There are two operating options: \n"
				+ "First, Control Word (CW) Time (sec) is 0. That means constant Control Word, never change. \n"
				+ "Second, Control Word (CW) Time (sec) is between 10 and 60. That means intervall Control Word, interval change. \n"
				+ "\n"
				+ "Autor: Findenig Markus mailto: mfindeni@edu.aau.at"
				);

		alert.showAndWait();

	}

}
