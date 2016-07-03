package app;

import javafx.application.Application;
import javafx.stage.Stage;
import model.SimulatorModel;
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
