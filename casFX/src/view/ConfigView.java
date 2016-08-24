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

	/**
	 * Config Scene.
	 */
	private Scene sceneConfig;

	/**
	 * Config Grid Pane.
	 */
	private GridPane grid;

	/**
	 * Width of the Config View.
	 */
	private double width = 500;

	/**
	 * Height of the Config View.
	 */
	private double height = 220;

	/**
	 * Text Field for FFmpeg Path.
	 */
	private static TextField ffmpegPathTF;

	/**
	 * Text Field for VLC Path.
	 */
	private static TextField vlcPathTF;

	/**
	 * Text Field for Server. Default is rtp://239.0.0.1:5004.
	 */
	private static TextField serverTF;

	/**
	 * Text Field for Client. Default is rtp://239.0.0.1:5004.
	 */
	private static TextField clientTF;

	/**
	 * Text Field for constant ControlWord (CW).
	 */
	private static TextField constantCwTF;

	/**
	 * OK Button.
	 */
	private static Button okB;

	/**
	 * Cancel Button.
	 */
	private static Button cancelB;

	/**
	 * Config View
	 */
	public ConfigView() {
		// Layout
		grid = new GridPane();
		grid.setPadding(new Insets(0, 0, 0, 0));
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(5);
		grid.setVgap(5);
		// Initialisiere Config Parameter
		initConfig();

		sceneConfig = new Scene(grid, width, height);
	}

	/**
	 * Show the Config View.
	 * 
	 * @param dialogStage
	 *            current dialog Stage.
	 */
	public void show(Stage dialogStage) {
		dialogStage.setTitle("Config");
		dialogStage.setScene(sceneConfig);
		dialogStage.show();
	}

	/**
	 * Initializes the Config View.
	 */
	public void initConfig() {

		// TODO Debug // Spalte, Zeile
		// grid.setGridLinesVisible(true);

		Label ffmpegPathL = new Label("FFMPEG Libary Path:");
		grid.add(ffmpegPathL, 1, 1);
		ffmpegPathTF = new TextField();
		ffmpegPathTF.setMinWidth(300);
		GridPane.setColumnSpan(ffmpegPathTF, 2);
		ffmpegPathTF.setTooltip(new Tooltip("Path to the FFMPEG Libary (ffmpeg.exe)."));
		grid.add(ffmpegPathTF, 2, 1);

		Label vlcPathL = new Label("VLC Libary Path:");
		grid.add(vlcPathL, 1, 2);
		vlcPathTF = new TextField();
		GridPane.setColumnSpan(vlcPathTF, 2);
		vlcPathTF.setTooltip(new Tooltip("Path to the VLC Libary (libvlc, vlc.exe)."));
		grid.add(vlcPathTF, 2, 2);

		Label serverL = new Label("Input Player (Server):");
		grid.add(serverL, 1, 3);
		serverTF = new TextField();
		serverTF.setTooltip(new Tooltip("Server IP and Port, Default rtp://239.0.0.1:5004"));
		grid.add(serverTF, 2, 3);

		Label clientL = new Label("Output Player (Client):");
		grid.add(clientL, 1, 4);
		clientTF = new TextField();
		clientTF.setTooltip(new Tooltip("Client IP and Port, Default rtp://239.0.0.1:5004"));
		grid.add(clientTF, 2, 4);

		Label constantCwL = new Label("Constant CW:");
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

	/**
	 * @return the ffmpeg Path Text Field.
	 */
	public TextField getFfmpegPath() {
		return ffmpegPathTF;
	}

	/**
	 * @return the vlcPath Text Field.
	 */
	public TextField getVlcPath() {
		return vlcPathTF;
	}

	/**
	 * @return the server Text Field.
	 */
	public TextField getServer() {
		return serverTF;
	}

	/**
	 * @return the client Text Field.
	 */
	public TextField getClient() {
		return clientTF;
	}

	/**
	 * @return the constant CW Text Field.
	 */
	public TextField getConstantCw() {
		return constantCwTF;
	}

	/**
	 * @return the ok Button.
	 */
	public Button getOk() {
		return okB;
	}

	/**
	 * @return the cancel Button.
	 */
	public Button getCancel() {
		return cancelB;
	}

}
