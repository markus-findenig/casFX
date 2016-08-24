package app;

import javafx.application.Application;
import javafx.stage.Stage;
import model.ConfigModel;
import model.SimulatorModel;
import controller.ConfigViewController;
import controller.SimulatorViewController;

/**
 * Run the Conditional Access System (CAS) Simulator (2016).
 * 
 * @author Findenig Markus
 * @version 1.0
 * @since 2016
 */
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
			SimulatorViewController inputVC = new SimulatorViewController(simulatorModel);
			inputVC.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
