package app;

import javafx.application.Application;
import javafx.stage.Stage;
import model.ConfigModel;
import model.SimulatorModel;
import controller.ConfigViewController;
import controller.InputViewController;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// session scope/application scope Beans initialisieren!
			// muss von Controller zu Controller weitergegeben werden
			SimulatorModel dataBean = new SimulatorModel(primaryStage);

			// Ersten Controller aufrufen
			InputViewController inputVC = new InputViewController(dataBean);
			inputVC.show();
			
			// Zweiten Controller Config initialisieren
			Stage dialogStage = new Stage();
			ConfigModel configModel = new ConfigModel(dialogStage);
			@SuppressWarnings("unused")
			ConfigViewController configVC = new ConfigViewController(configModel);
			// show im InputViewController 
			//configVC.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
