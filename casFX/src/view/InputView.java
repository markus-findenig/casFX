package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.SimulatorModel;
import app.MediaControl;

public class InputView {

	private Scene scene;
	public GridPane grid;

	// Menu
	final Menu menu1 = new Menu("File");
	final Menu menu2 = new Menu("Options");
	final Menu menu3 = new Menu("Help");

	final MenuItem open = new MenuItem("Open");

	final MenuItem exit = new MenuItem("Exit");
	private SimulatorModel model;

	// Video Input Informations
	private static Label videoTypeL;
	private static TextField videoTypeTF;
	private static Label videoCodecL;
	private static TextField videoCodecTF;
	private static Label videoResolutionL;
	private static TextField videoResolutionTF;

	// Time interval for ControlWord (CW)
	private static Label cwTimeL;
	private static TextField cwTimeTF;

	// ECM Input Keys
	private static Label cwL;
	private static TextField cwTF;
	private static Label ak0L;
	private static TextField ak0TF;
	private static Label ak1L;
	private static TextField ak1TF;
	private static Label mpkL;
	private static TextArea mpkTA;
	private static RadioButton ak0RB;
	private static RadioButton ak1RB;

	// Transport Stream Header
	private static Label tsScramblingControlL;
	private static TextField tsScramblingControlTF;

	// ECM Header Fields
	private static Label ecmHeaderL;
	private static TextField ecmHeaderTF;
	private static Label ecmProtocolL;
	private static TextField ecmProtocolTF;
	private static Label ecmBroadcastIdL;
	private static TextField ecmBroadcastIdTF;
	private static Label ecmWorkKeyIdL;
	private static TextField ecmWorkKeyIdTF;
	private static Label ecmScramblingKeyOddL;
	private static TextField ecmScramblingKeyOddTF;
	private static Label ecmScramblingKeyEvenL;
	private static TextField ecmScramblingKeyEvenTF;
	private static Label ecmProgramTypeL;
	private static TextField ecmProgramTypeTF;
	private static Label ecmDateTimeL;
	private static TextField ecmDateTimeTF;
	private static Label ecmRecordControlL;
	private static TextField ecmRecordControlTF;
	private static Label ecmVariablePartL;
	private static TextField ecmVariablePartTF;
	private static Label ecmMacL;
	private static TextField ecmMacTF;
	private static Label ecmCrcL;
	private static TextField ecmCrcTF;

	// ECM Outpt Keys
	private static Label cwOutL;
	private static TextField cwOutTF;
	private static Label ak0OutL;
	private static TextField ak0OutTF;
	private static Label ak1OutL;
	private static TextField ak1OutTF;
	private static Label mpkOutL;
	private static TextArea mpkOutTA;

	/**
	 * InputView
	 * 
	 * @param model
	 *            SimulatorModel
	 */
	public InputView(SimulatorModel model) {
		this.model = model;

		// Layout
		grid = new GridPane();
		grid.setPadding(new Insets(0, 0, 0, 0));
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(5);
		grid.setVgap(5);

		// Menu Bar
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menu1, menu2, menu3);
		GridPane.setColumnSpan(menuBar, 20);
		menuBar.prefWidthProperty().bind(SimulatorModel.PRIMARY_STAGE.widthProperty());
		grid.add(menuBar, 0, 0);

		menu1.getItems().add(open);
		menu1.getItems().add(exit);

		init();
		initPlayer1();
		initPlayer2();

		scene = new Scene(grid, 1024, 600);
		scene.setRoot(grid);
		show(SimulatorModel.PRIMARY_STAGE);

	}

	public void show(Stage stage) {
		stage.setTitle("CAS-Simulator");
		stage.setScene(scene);
		stage.show();
	}

	public MenuItem getOpen() {
		return open;
	}
	
	public MenuItem getExit() {
		return exit;
	}

	public void init() {

		// TODO Debug
		// grid.setGridLinesVisible(true);

		// --------------------------------------------------------------------
		// Video Input
		videoTypeL = new Label("Type:");
		videoTypeTF = new TextField("Video");
		videoTypeTF.setStyle("-fx-background-color: transparent;");
		videoTypeTF.setEditable(false);
		// videoTypeTF.setMaxWidth(100);

		grid.add(videoTypeL, 1, 1);
		grid.add(videoTypeTF, 2, 1);

		videoCodecL = new Label("Codec:");
		videoCodecTF = new TextField("H264");
		videoCodecTF.setStyle("-fx-background-color: transparent;");
		videoCodecTF.setEditable(false);
		grid.add(videoCodecL, 1, 2);
		grid.add(videoCodecTF, 2, 2);

		videoResolutionL = new Label("Resolution:");
		videoResolutionTF = new TextField(model.mediaInput.getWidth() + "x" + model.mediaInput.getHeight());
		videoResolutionTF.setStyle("-fx-background-color: transparent;");
		videoResolutionTF.setEditable(false);
		grid.add(videoResolutionL, 1, 3);
		grid.add(videoResolutionTF, 2, 3);

		// Media Player Input

		// TODO del
		// videoResolutionTF.setText(model.media.getWidth() + "x" +
		// model.media.getHeight());

		// Time interval for ControlWord (CW)
		cwTimeL = new Label("Time (sec):");
		cwTimeTF = new TextField("10");
		cwTimeTF.setMaxWidth(120);
		cwTimeTF.setTooltip(new Tooltip("Time in seconds"));
		grid.add(cwTimeL, 1, 12);
		grid.add(cwTimeTF, 2, 12);

		// ECM Input Keys
		cwL = new Label("CW:");
		cwTF = new TextField("0123456789ABCDEF");
		cwTF.setStyle("-fx-background-color: transparent;");
		cwTF.setEditable(false);
		cwTF.setTooltip(new Tooltip("Control Word (64 bit)"));
		grid.add(cwL, 1, 13);
		grid.add(cwTF, 2, 13);

		final ToggleGroup groupRB = new ToggleGroup();
		ak0RB = new RadioButton();
		ak1RB = new RadioButton();
		ak0RB.setToggleGroup(groupRB);
		ak1RB.setToggleGroup(groupRB);
		ak0RB.setSelected(true);
		grid.add(ak0RB, 0, 14);
		grid.add(ak1RB, 0, 15);

		ak0L = new Label("AK 00:");
		ak0TF = new TextField("00112233445566778899AABBCCDDEEFF");
		ak0TF.setEditable(true);
		ak0TF.setTooltip(new Tooltip("Authorization Key 0 (128 bit)"));
		ak0TF.setMinWidth(230);
		grid.add(ak0L, 1, 14);
		grid.add(ak0TF, 2, 14);

		ak1L = new Label("AK 01:");
		ak1TF = new TextField("FFEEDDCCBBAA99887766554433221100");
		ak1TF.setEditable(true);
		ak1TF.setTooltip(new Tooltip("Authorization Key 1 (128 bit)"));
		ak1TF.setMinWidth(230);
		grid.add(ak1L, 1, 15);
		grid.add(ak1TF, 2, 15);

		mpkL = new Label("MPK:");
		mpkTA = new TextArea("00112233445566778899AABBCCDDEEFF\nFFEEDDCCBBAA99887766554433221100");
		mpkTA.setEditable(false);
		mpkTA.setTooltip(new Tooltip("Master Private Key (256 bit) \nMPK is not currently in use."));
		mpkTA.setMaxSize(240, 45);
		grid.add(mpkL, 1, 16);
		grid.add(mpkTA, 2, 16);

		// --------------------------------------------------------------------
		// Transport Stream Header
		tsScramblingControlL = new Label("Scrambling Control (2 bits):");
		tsScramblingControlTF = new TextField("00");
		tsScramblingControlTF.setStyle("-fx-background-color: transparent;");
		tsScramblingControlTF.setEditable(false);
		tsScramblingControlTF.setTooltip(new Tooltip("00: No scrambling\n" + "01: Not defined\n"
				+ "10: Scrambled (even key)\n" + "11: Scrambled (odd key) "));
		grid.add(tsScramblingControlL, 8, 2);
		grid.add(tsScramblingControlTF, 9, 2);

		// --------------------------------------------------------------------
		// ECM
		ecmHeaderL = new Label("ECM Section Header:");
		ecmHeaderTF = new TextField("8200000000000000");
		ecmHeaderTF.setEditable(false);
		ecmHeaderTF.setStyle("-fx-background-color: transparent;");
		ecmHeaderTF.setTooltip(new Tooltip("ECM Section Header (64 bit)"));
		grid.add(ecmHeaderL, 8, 4);
		grid.add(ecmHeaderTF, 9, 4);

		ecmProtocolL = new Label("Protocol number:");
		ecmProtocolTF = new TextField("AA");
		ecmProtocolTF.setEditable(false);
		ecmProtocolTF.setStyle("-fx-background-color: transparent;");
		ecmProtocolTF.setTooltip(new Tooltip("Protocol number (8 bit)"));
		grid.add(ecmProtocolL, 8, 5);
		grid.add(ecmProtocolTF, 9, 5);

		ecmBroadcastIdL = new Label("Broadcast group id:");
		ecmBroadcastIdTF = new TextField("FF");
		ecmBroadcastIdTF.setEditable(false);
		ecmBroadcastIdTF.setStyle("-fx-background-color: transparent;");
		ecmBroadcastIdTF.setTooltip(new Tooltip("Broadcast group id (8 bit)"));
		grid.add(ecmBroadcastIdL, 8, 6);
		grid.add(ecmBroadcastIdTF, 9, 6);

		ecmWorkKeyIdL = new Label("AK id:");
		ecmWorkKeyIdTF = new TextField("00");
		ecmWorkKeyIdTF.setEditable(false);
		ecmWorkKeyIdTF.setStyle("-fx-background-color: transparent;");
		ecmWorkKeyIdTF.setTooltip(new Tooltip("Work key id (8 bit)"));
		grid.add(ecmWorkKeyIdL, 8, 7);
		grid.add(ecmWorkKeyIdTF, 9, 7);

		ecmScramblingKeyOddL = new Label("CW (odd):");
		ecmScramblingKeyOddTF = new TextField("0123456789ABCDEF");
		ecmScramblingKeyOddTF.setEditable(false);
		ecmScramblingKeyOddTF.setStyle("-fx-background-color: transparent;");
		ecmScramblingKeyOddTF.setTooltip(new Tooltip("Scrambling key odd (64 bit)"));
		grid.add(ecmScramblingKeyOddL, 8, 8);
		grid.add(ecmScramblingKeyOddTF, 9, 8);

		ecmScramblingKeyEvenL = new Label("CW (even)");
		ecmScramblingKeyEvenTF = new TextField("FEDCBA9876543210");
		ecmScramblingKeyEvenTF.setEditable(false);
		ecmScramblingKeyEvenTF.setStyle("-fx-background-color: transparent;");
		ecmScramblingKeyEvenTF.setTooltip(new Tooltip("Scrambling key even (64 bit)"));
		grid.add(ecmScramblingKeyEvenL, 8, 9);
		grid.add(ecmScramblingKeyEvenTF, 9, 9);

		ecmProgramTypeL = new Label("Program type:");
		ecmProgramTypeTF = new TextField("C8");
		ecmProgramTypeTF.setEditable(false);
		ecmProgramTypeTF.setStyle("-fx-background-color: transparent;");
		ecmScramblingKeyEvenTF.setTooltip(new Tooltip("Program type (8 bit)"));
		grid.add(ecmProgramTypeL, 8, 10);
		grid.add(ecmProgramTypeTF, 9, 10);

		ecmDateTimeL = new Label("Date/Time:");
		ecmDateTimeTF = new TextField("123456789A");
		ecmDateTimeTF.setEditable(false);
		ecmDateTimeTF.setStyle("-fx-background-color: transparent;");
		ecmDateTimeTF.setTooltip(new Tooltip("Date/Time (40 bit)"));
		grid.add(ecmDateTimeL, 8, 11);
		grid.add(ecmDateTimeTF, 9, 11);

		ecmRecordControlL = new Label("Recording control:");
		ecmRecordControlTF = new TextField("D5");
		ecmRecordControlTF.setEditable(false);
		ecmRecordControlTF.setStyle("-fx-background-color: transparent;");
		ecmRecordControlTF.setTooltip(new Tooltip("Recording control (8 bit)"));
		grid.add(ecmRecordControlL, 8, 12);
		grid.add(ecmRecordControlTF, 9, 12);

		ecmVariablePartL = new Label("Variable part:");
		ecmVariablePartTF = new TextField("null");
		ecmVariablePartTF.setEditable(false);
		ecmVariablePartTF.setStyle("-fx-background-color: transparent;");
		ecmRecordControlTF.setTooltip(new Tooltip("Variable part \nCapable of accommodating various function information"));
		grid.add(ecmVariablePartL, 8, 13);
		grid.add(ecmVariablePartTF, 9, 13);

		ecmMacL = new Label("Payload MAC:");
		ecmMacTF = new TextField("01234567");
		ecmMacTF.setEditable(false);
		ecmMacTF.setStyle("-fx-background-color: transparent;");
		ecmMacTF.setTooltip(new Tooltip("(Message Authentification Code (32 bit)"));
		grid.add(ecmMacL, 8, 14);
		grid.add(ecmMacTF, 9, 14);

		ecmCrcL = new Label("Section CRC:");
		ecmCrcTF = new TextField("89ABCDEF");
		ecmCrcTF.setEditable(false);
		ecmCrcTF.setStyle("-fx-background-color: transparent;");
		ecmMacTF.setTooltip(new Tooltip("Cyclic Redundancy Check (32 bit)"));
		grid.add(ecmCrcL, 8, 15);
		grid.add(ecmCrcTF, 9, 15);

		// --------------------------------------------------------------------
		// Video Output

		// Media Player Output

		// Internal or External Key Reader
		Label readerL = new Label("Reader:");
		ComboBox<String> readerCB = new ComboBox<String>();
		readerCB.getItems().addAll("Internal", "External");
		readerCB.setValue("Internal");

		grid.add(readerL, 16, 12);
		grid.add(readerCB, 17, 12);

		// ECM Output Keys
		cwOutL = new Label("CW:");
		cwOutTF = new TextField("0123456789ABCDEF");
		cwOutTF.setStyle("-fx-background-color: transparent;");
		cwOutTF.setEditable(false);
		cwOutTF.setTooltip(new Tooltip("Control Word (64 bit)"));
		grid.add(cwOutL, 16, 13);
		grid.add(cwOutTF, 17, 13);

		ak0OutL = new Label("AK 00:");
		ak0OutTF = new TextField("00112233445566778899AABBCCDDEEFF");
		ak0OutTF.setEditable(true);
		ak0OutTF.setTooltip(new Tooltip("Authorization Key 0 (128 bit)"));
		ak0OutTF.setMinWidth(230);
		grid.add(ak0OutL, 16, 14);
		grid.add(ak0OutTF, 17, 14);

		ak1OutL = new Label("AK 01:");
		ak1OutTF = new TextField("FFEEDDCCBBAA99887766554433221100");
		ak1OutTF.setEditable(true);
		ak1OutTF.setTooltip(new Tooltip("Authorization Key 1 (128 bit)"));
		ak1OutTF.setMinWidth(230);
		grid.add(ak1OutL, 16, 15);
		grid.add(ak1OutTF, 17, 15);

		mpkOutL = new Label("MPK:");
		mpkOutTA = new TextArea("00112233445566778899AABBCCDDEEFF\nFFEEDDCCBBAA99887766554433221100");
		mpkOutTA.setEditable(false);
		mpkOutTA.setTooltip(new Tooltip("Master Private Key (256 bit) \nMPK is not currently in use."));
		mpkOutTA.setMaxSize(240, 45);
		grid.add(mpkOutL, 16, 16);
		grid.add(mpkOutTA, 17, 16);

	}

	// Video Player Input
	public void initPlayer1() {
		// create media player 1 fx
		MediaPlayer mediaPlayerInput = new MediaPlayer(model.mediaInput);
		MediaControl mediaControl = new MediaControl(mediaPlayerInput, true);
		GridPane.setColumnSpan(mediaControl, 3);
		GridPane.setRowSpan(mediaControl, 8);
		mediaControl.setMinSize(300, 225);
		mediaControl.setPrefSize(300, 225);
		mediaControl.setMaxSize(300, 225);
		grid.add(mediaControl, 1, 4);
	}

	// Video Player Output
	public void initPlayer2() {
		// create media player 2 fx
		MediaPlayer mediaPlayerOutput = new MediaPlayer(model.mediaInput);
		MediaControl mediaControlOutput = new MediaControl(mediaPlayerOutput, true);
		GridPane.setColumnSpan(mediaControlOutput, 3);
		GridPane.setRowSpan(mediaControlOutput, 8);
		mediaControlOutput.setMinSize(300, 225);
		mediaControlOutput.setPrefSize(300, 225);
		mediaControlOutput.setMaxSize(300, 225);
		grid.add(mediaControlOutput, 16, 4);
	}

	public TextField getCwTimeTF() {
		return cwTimeTF;
	}

	public TextField getCwTF() {
		return cwTF;
	}

	public TextField getAk0TF() {
		return ak0TF;
	}

	public TextField getAk1TF() {
		return ak1TF;
	}

	public TextField getAk0OutTF() {
		return ak0OutTF;
	}

	public TextField getAk1OutTF() {
		return ak1OutTF;
	}

}
