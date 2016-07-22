package app;

import javafx.application.Application;
import javafx.stage.Stage;
import model.ConfigModel;
import model.SimulatorModel;
import controller.ConfigViewController;
import controller.InputViewController;
import controller.PlayerViewController;

public class Main extends Application {

	/**
     * @param args the command line arguments
     */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// session scope/application scope Beans initialisieren!
			// muss von Controller zu Controller weitergegeben werden
			SimulatorModel simulatorModel = new SimulatorModel(primaryStage);

			// Controller Config Popup initialisieren
			Stage dialogStage = new Stage();
			ConfigModel configModel = new ConfigModel(dialogStage);
			@SuppressWarnings("unused")
			ConfigViewController configVC = new ConfigViewController(configModel);
			// show im InputViewController
			//configVC.show();
			
			// Controller View aufrufen
			InputViewController inputVC = new InputViewController(simulatorModel, configModel);
			inputVC.show();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
