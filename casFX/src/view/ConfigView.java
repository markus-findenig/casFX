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

public class ConfigView {

	// Config Scene und Grid
	private Scene sceneConfig;
	private GridPane grid;
	//private ConfigModel configModel;

	// Config Parameter
	private static Label ffmpegPathL;
	private static TextField ffmpegPathTF;
	
	private static Label vlcPathL;
	private static TextField vlcPathTF;
	
	private static Label serverL;
	private static TextField serverTF;

	private static Label clientL;
	private static TextField clientTF;
	
	private static Label constantCwL;
	private static TextField constantCwTF;

	private static Button okB;
	private static Button cancelB;

	/**
	 * Config View
	 * 
	 */
	public ConfigView() {
		//configModel = cModel;

		// Layout
		grid = new GridPane();
		grid.setPadding(new Insets(0, 0, 0, 0));
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(5);
		grid.setVgap(5);

		// Initialisiere Config Parameter
		initConfig();

		sceneConfig = new Scene(grid, 500, 220);
	}

	public void show(Stage dialogStage) {
		dialogStage.setTitle("Config");
		dialogStage.setScene(sceneConfig);
		dialogStage.show();
	}

	public void initConfig() {

		// TODO Debug // Spalte, Zeile
		// grid.setGridLinesVisible(true);
		
		ffmpegPathL = new Label("FFMPEG Libary Path:");
		grid.add(ffmpegPathL, 1, 1);
		ffmpegPathTF = new TextField();
		ffmpegPathTF.setMinWidth(300);
		GridPane.setColumnSpan(ffmpegPathTF, 2);
		ffmpegPathTF.setTooltip(new Tooltip("Path to the FFMPEG Libary (ffmpeg.exe)."));
		grid.add(ffmpegPathTF, 2, 1);
		
		vlcPathL = new Label("VLC Libary Path:");
		grid.add(vlcPathL, 1, 2);
		vlcPathTF = new TextField();
		GridPane.setColumnSpan(vlcPathTF, 2);
		vlcPathTF.setTooltip(new Tooltip("Path to the VLC Libary (libvlc)."));
		grid.add(vlcPathTF, 2, 2);

		serverL = new Label("Input Player (Server):");
		grid.add(serverL, 1, 3);
		serverTF = new TextField();
		serverTF.setTooltip(new Tooltip("Server IP and Port, Default http://127.0.0.1:7777"));
		grid.add(serverTF, 2, 3);

		clientL = new Label("Output Player (Client):");
		grid.add(clientL, 1, 4);
		clientTF = new TextField();
		clientTF.setTooltip(new Tooltip("Client IP and Port, Default http://127.0.0.1:7777"));
		grid.add(clientTF, 2, 4);
		
		
		constantCwL = new Label("Constant CW:");
		grid.add(constantCwL, 1, 5);
		constantCwTF = new TextField();
		constantCwTF.setTooltip(new Tooltip("If Timer 0, Constant CW in hex with 16 char."));
		grid.add(constantCwTF, 2, 5);

		okB = new Button("OK");
		okB.setPrefWidth(80);
		grid.add(okB, 1, 10);

		cancelB = new Button("CANCEL");
		cancelB.setPrefWidth(80);
		grid.add(cancelB, 2, 10);

	}

	public TextField getFfmpegPathTF() {
		return ffmpegPathTF;
	}
	
	public TextField getVlcPathTF() {
		return vlcPathTF;
	}

	public TextField getServer() {
		return serverTF;
	}

	public TextField getClient() {
		return clientTF;
	}
	
	public TextField getConstantCwTF() {
		return constantCwTF;
	}

	public Button getOk() {
		return okB;
	}

	public Button getCancel() {
		return cancelB;
	}

}
