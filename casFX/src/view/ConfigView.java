package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.ConfigModel;

public class ConfigView {

	// Config Scene und Grid
	private Scene sceneConfig;
	private GridPane grid;
	private ConfigModel configModel;

	// Config Parameter
	private static Label serverL;
	private static TextField serverTF;

	private static Label clientL;
	private static TextField clientTF;

	private static Button okB;
	private static Button cancelB;

	/**
	 * Config View
	 * 
	 * @param configModel
	 *            ConfigModel
	 */
	public ConfigView(ConfigModel configModel) {
		this.configModel = configModel;

		// Layout
		grid = new GridPane();
		grid.setPadding(new Insets(0, 0, 0, 0));
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(5);
		grid.setVgap(5);

		init();

		sceneConfig = new Scene(grid, 400, 200);
	
	}

	public void show(Stage dialogStage) {
		dialogStage.setTitle("Config");
		dialogStage.setScene(sceneConfig);
		dialogStage.show();
	}

	public void init() {

		// TODO Debug
		// grid.setGridLinesVisible(true);

		serverL = new Label("Input Player (Server):");
		grid.add(serverL, 1, 1);
		serverTF = new TextField("http://127.0.0.1:7777");
		serverTF.setTooltip(new Tooltip("Server IP and Port, Default http://127.0.0.1:7777"));
		grid.add(serverTF, 2, 1);

		clientL = new Label("Output Player (Client):");
		grid.add(clientL, 1, 3);
		clientTF = new TextField("http://127.0.0.1:7777");
		clientTF.setTooltip(new Tooltip("Client IP and Port, Default http://127.0.0.1:7777"));
		grid.add(clientTF, 2, 3);

		okB = new Button("OK");
		okB.setPrefWidth(80);
		grid.add(okB, 1, 20);

		cancelB = new Button("CANCEL");
		cancelB.setPrefWidth(80);
		grid.add(cancelB, 2, 20);

	}

	public TextField getServer() {
		return serverTF;
	}

	public TextField getClient() {
		return clientTF;
	}

	public Button getOk() {
		return okB;
	}

	public Button getCancel() {
		return cancelB;
	}

}
