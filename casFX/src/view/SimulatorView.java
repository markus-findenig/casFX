package view;

import javafx.stage.WindowEvent;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.SimulatorModel;

/**
 * Input View
 */
public class SimulatorView {

	private Scene scene;
	private GridPane grid;

	// private SimulatorModel model;

	// Menu
	private final Menu menu1 = new Menu("File");
	private final Menu menu2 = new Menu("Options");
	private final Menu menu3 = new Menu("Help");

	private final MenuItem open = new MenuItem("Open");

	private final MenuItem exit = new MenuItem("Exit");

	private final MenuItem test = new MenuItem("Run Test Funktion");

	private final MenuItem config = new MenuItem("Simulator Config");

	// Encryption Text Field and Toggle Button
	private static Label encryptionL;
	private static ToggleButton encryptionTB;
	
	// Decryption Text Field and Toggle Button
	private static Label decryptionL;
	private static ToggleButton decryptionTB;

	// Time interval for ControlWord (CW)
	private static Label cwTimeL;
	private static TextField cwTimeTF;

	// ECM Input Keys
	private static Label cwInL;
	private static TextField cwInTF;
	private static Label ak0InL;
	private static TextField ak0InTF;
	private static Label ak1InL;
	private static TextField ak1InTF;
	private static Label mpkInL;
	private static TextArea mpkInTA;
	private static ToggleGroup groupRB;
	private static RadioButton ak0InRB;
	private static RadioButton ak1InRB;

	// Transport Stream Header
	private static Label scramblingControlL;
	private static TextField scramblingControlTF;

	// ECM Header Fields
	private static Label ecmHeaderL;
	private static TextField ecmHeaderTF;
	private static Label ecmProtocolL;
	private static TextField ecmProtocolTF;
	private static Label ecmBroadcastIdL;
	private static TextField ecmBroadcastIdTF;
	private static Label ecmWorkKeyIdL;
	private static TextField ecmWorkKeyIdTF;
	private static Label ecmCwOddL;
	private static TextField ecmCwOddTF;
	private static Label ecmCwEvenL;
	private static TextField ecmCwEvenTF;
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

	// ECM's
	private static Label ecmL;
	private static TextArea ecmTA;
	private static Label ecmEncryptedL;
	private static TextArea ecmEncryptedTA;
	private static Label ecmDecryptedL;
	private static TextArea ecmDecryptedTA;

		// Video Input Player
	private static Label videoInputL;
	private static Button videoInputB;

	// Video Output Player
	private static Label videoOutputL;
	private static Button videoOutputB;

	/**
	 * Input View
	 * 
	 * @param model
	 *            SimulatorModel
	 */
	public SimulatorView() {
		// model = sModel;

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
		// menuBar.prefWidthProperty().bind(SimulatorModel.getPrimaryStage().widthProperty());
		grid.add(menuBar, 0, 0);

		menu1.getItems().add(open);
		menu1.getItems().add(exit);

		menu2.getItems().add(config);

		menu3.getItems().add(test);

		initGUI();
		// initPlayerInput();
		// initPlayerOutput();
		// initBarChartInput();

		scene = new Scene(grid, 1024, 650);
		scene.setRoot(grid);
		// show(model.getPrimaryStage());

	}

	public void show(Stage stage) {
		stage.setTitle("CAS-Simulator");
		stage.setScene(scene);
		// overwrite Exit event
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				System.out.println("Stage is closing");
				Platform.exit();
				System.exit(0);
			}
		});
		stage.show();
	}

	public MenuItem getOpen() {
		return open;
	}

	public MenuItem getExit() {
		return exit;
	}

	public MenuItem getConfig() {
		return config;
	}

	public MenuItem test() {
		return test;
	}

	public void initGUI() {

		// TODO Debug
		//grid.setGridLinesVisible(true);

		// --------------------------------------------------------------------
		// Encryption Toggle Button
		encryptionL = new Label("Encryption:");
		grid.add(encryptionL, 1, 2);
		encryptionTB = new ToggleButton("OFF");
		encryptionTB.setDisable(true);
		encryptionTB.setPrefWidth(80);
		grid.add(encryptionTB, 2, 2);

		// --------------------------------------------------------------------
		// Video Player Input
		videoInputL = new Label("Input\nPlayer:");
		videoInputL.setPrefWidth(80);
		videoInputB = new Button("View");
		videoInputB.setDisable(true);
		videoInputB.setPrefWidth(80);
		grid.add(videoInputL, 1, 4);
		grid.add(videoInputB, 2, 4);

		// --------------------------------------------------------------------
		// Time interval for ControlWord (CW)
		cwTimeL = new Label("Time (sec):");
		cwTimeTF = new TextField("0");
		cwTimeTF.setMaxWidth(120);
		cwTimeTF.setTooltip(new Tooltip("Time in seconds"));
		grid.add(cwTimeL, 1, 8);
		grid.add(cwTimeTF, 2, 8);

		// --------------------------------------------------------------------
		// ECM Input Keys
		cwInL = new Label("CW:");
		cwInTF = new TextField("0123456789ABCDEF");
		cwInTF.setStyle("-fx-background-color: transparent;");
		cwInTF.setEditable(false);
		cwInTF.setTooltip(new Tooltip("Control Word (64 bit)"));
		grid.add(cwInL, 1, 10);
		grid.add(cwInTF, 2, 10);

		groupRB = new ToggleGroup();
		ak0InRB = new RadioButton();
		ak1InRB = new RadioButton();
		ak0InRB.setToggleGroup(groupRB);
		ak1InRB.setToggleGroup(groupRB);
		ak0InRB.setUserData("00");
		ak1InRB.setUserData("01");
		ak0InRB.setSelected(true);
		grid.add(ak0InRB, 0, 12);
		grid.add(ak1InRB, 0, 13);

		ak0InL = new Label("AK 00:");
		ak0InTF = new TextField("00112233445566778899AABBCCDDEEFF");
		ak0InTF.setEditable(true);
		ak0InTF.setTooltip(new Tooltip("Authorization Key 0 (128 bit)"));
		ak0InTF.setMinWidth(230);
		grid.add(ak0InL, 1, 12);
		grid.add(ak0InTF, 2, 12);

		ak1InL = new Label("AK 01:");
		ak1InTF = new TextField("FFEEDDCCBBAA99887766554433221100");
		ak1InTF.setEditable(true);
		ak1InTF.setTooltip(new Tooltip("Authorization Key 1 (128 bit)"));
		ak1InTF.setMinWidth(230);
		grid.add(ak1InL, 1, 13);
		grid.add(ak1InTF, 2, 13);

		mpkInL = new Label("MPK:");
		mpkInTA = new TextArea("00112233445566778899AABBCCDDEEFFFFEEDDCCBBAA99887766554433221100");
		mpkInTA.setWrapText(true);
		mpkInTA.setEditable(false);
		mpkInTA.setTooltip(new Tooltip("Master Private Key (256 bit) \nMPK is not currently in use."));
		mpkInTA.setMaxSize(250, 45);
		grid.add(mpkInL, 1, 14);
		grid.add(mpkInTA, 2, 14);

		// --------------------------------------------------------------------
		// Current ECM

		ecmL = new Label("Current\nECM:");
		ecmTA = new TextArea("");
		ecmTA.setWrapText(true);
		ecmTA.setEditable(false);
		ecmTA.setTooltip(new Tooltip("Current ECM."));
		ecmTA.setMaxSize(240, 60);
		grid.add(ecmL, 1, 17);
		grid.add(ecmTA, 2, 17);

		// --------------------------------------------------------------------
		// Transport Stream Header
		scramblingControlL = new Label("Scrambling\nControl (2 bits):");
		scramblingControlL.setPrefWidth(100);
		scramblingControlTF = new TextField("00");
		scramblingControlTF.setStyle("-fx-background-color: transparent;");
		scramblingControlTF.setEditable(false);
		scramblingControlTF.setTooltip(new Tooltip("00: No scrambling\n" + "01: Not defined\n"
				+ "10: Control Word (even key)\n" + "11: Control Word (odd key) "));
		grid.add(scramblingControlL, 8, 2);
		grid.add(scramblingControlTF, 9, 2);

		// --------------------------------------------------------------------
		// ECM
		ecmHeaderL = new Label("ECM Section\nHeader:");
		ecmHeaderTF = new TextField("8000000000000000");
		ecmHeaderTF.setEditable(false);
		ecmHeaderTF.setStyle("-fx-background-color: transparent;");
		ecmHeaderTF.setTooltip(new Tooltip("ECM Section Header (64 bit)"));
		grid.add(ecmHeaderL, 8, 4);
		grid.add(ecmHeaderTF, 9, 4);

		ecmProtocolL = new Label("Protocol Nr.:");
		ecmProtocolTF = new TextField("AA");
		ecmProtocolTF.setEditable(false);
		ecmProtocolTF.setStyle("-fx-background-color: transparent;");
		ecmProtocolTF.setTooltip(new Tooltip("Protocol number (8 bit)"));
		grid.add(ecmProtocolL, 8, 5);
		grid.add(ecmProtocolTF, 9, 5);

		ecmBroadcastIdL = new Label("Broadcast ID:");
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

		ecmCwOddL = new Label("CW (odd):");
		ecmCwOddTF = new TextField("0123456789ABCDEF");
		ecmCwOddTF.setEditable(false);
		ecmCwOddTF.setStyle("-fx-background-color: transparent;");
		ecmCwOddTF.setTooltip(new Tooltip("Scrambling key odd (64 bit)"));
		grid.add(ecmCwOddL, 8, 8);
		grid.add(ecmCwOddTF, 9, 8);

		ecmCwEvenL = new Label("CW (even)");
		ecmCwEvenTF = new TextField("FEDCBA9876543210");
		ecmCwEvenTF.setEditable(false);
		ecmCwEvenTF.setStyle("-fx-background-color: transparent;");
		ecmCwEvenTF.setTooltip(new Tooltip("Scrambling key even (64 bit)"));
		grid.add(ecmCwEvenL, 8, 9);
		grid.add(ecmCwEvenTF, 9, 9);

		ecmProgramTypeL = new Label("Program:");
		ecmProgramTypeTF = new TextField("C8");
		ecmProgramTypeTF.setEditable(false);
		ecmProgramTypeTF.setStyle("-fx-background-color: transparent;");
		ecmProgramTypeTF.setTooltip(new Tooltip("Program type (8 bit)"));
		grid.add(ecmProgramTypeL, 8, 10);
		grid.add(ecmProgramTypeTF, 9, 10);

		ecmDateTimeL = new Label("Date/Time:");
		ecmDateTimeTF = new TextField("0123456789");
		ecmDateTimeTF.setEditable(false);
		ecmDateTimeTF.setStyle("-fx-background-color: transparent;");
		ecmDateTimeTF.setTooltip(new Tooltip("Date/Time (40 bit)"));
		grid.add(ecmDateTimeL, 8, 11);
		grid.add(ecmDateTimeTF, 9, 11);

		ecmRecordControlL = new Label("Recording:");
		ecmRecordControlTF = new TextField("D5");
		ecmRecordControlTF.setEditable(false);
		ecmRecordControlTF.setStyle("-fx-background-color: transparent;");
		ecmRecordControlTF.setTooltip(new Tooltip("Recording control (8 bit)"));
		grid.add(ecmRecordControlL, 8, 12);
		grid.add(ecmRecordControlTF, 9, 12);

		ecmVariablePartL = new Label("Variable part:");
		ecmVariablePartTF = new TextField("00000000");
		ecmVariablePartTF.setEditable(false);
		ecmVariablePartTF.setStyle("-fx-background-color: transparent;");
		ecmRecordControlTF
				.setTooltip(new Tooltip("Variable part \nCapable of accommodating various function information"));
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
		// Encrypted ECM

		ecmEncryptedL = new Label("Encrypted\nECM:");
		ecmEncryptedTA = new TextArea("");
		ecmEncryptedTA.setWrapText(true);
		ecmEncryptedTA.setEditable(false);
		ecmEncryptedTA.setTooltip(new Tooltip("Encrypted ECM."));
		ecmEncryptedTA.setMaxSize(240, 60);
		grid.add(ecmEncryptedL, 8, 17);
		grid.add(ecmEncryptedTA, 9, 17);

		
		// --------------------------------------------------------------------
		// Encryption Toggle Button
		decryptionL = new Label("Decryption:");
		grid.add(decryptionL, 16, 2);
		decryptionTB = new ToggleButton("OFF");
		decryptionTB.setPrefWidth(80);
		grid.add(decryptionTB, 17, 2);
				
		// --------------------------------------------------------------------
		// Video Output
		videoOutputL = new Label("Output\nPlayer:");
		videoOutputL.setPrefWidth(80);
		videoOutputB = new Button("View");
		videoOutputB.setDisable(true);
		videoOutputB.setPrefWidth(80);
		grid.add(videoOutputL, 16, 4);
		grid.add(videoOutputB, 17, 4);

		
		// --------------------------------------------------------------------
		// ECM Output Keys
		cwOutL = new Label("CW:");
		cwOutTF = new TextField("-- WAIT FOR ECM --");
		cwOutTF.setStyle("-fx-background-color: transparent;");
		cwOutTF.setEditable(false);
		cwOutTF.setTooltip(new Tooltip("Control Word (64 bit)"));
		grid.add(cwOutL, 16, 10);
		grid.add(cwOutTF, 17, 10);

		ak0OutL = new Label("AK 00:");
		ak0OutTF = new TextField("00112233445566778899AABBCCDDEEFF");
		ak0OutTF.setEditable(true);
		ak0OutTF.setTooltip(new Tooltip("Authorization Key 0 (128 bit)"));
		ak0OutTF.setMinWidth(230);
		grid.add(ak0OutL, 16, 12);
		grid.add(ak0OutTF, 17, 12);

		ak1OutL = new Label("AK 01:");
		ak1OutTF = new TextField("FFEEDDCCBBAA99887766554433221100");
		ak1OutTF.setEditable(true);
		ak1OutTF.setTooltip(new Tooltip("Authorization Key 1 (128 bit)"));
		ak1OutTF.setMinWidth(230);
		grid.add(ak1OutL, 16, 13);
		grid.add(ak1OutTF, 17, 13);

		mpkOutL = new Label("MPK:");
		mpkOutTA = new TextArea("00112233445566778899AABBCCDDEEFFFFEEDDCCBBAA99887766554433221100");
		mpkOutTA.setWrapText(true);
		mpkOutTA.setEditable(false);
		mpkOutTA.setTooltip(new Tooltip("Master Private Key (256 bit) \nMPK is not currently in use."));
		mpkOutTA.setMaxSize(250, 45);
		grid.add(mpkOutL, 16, 14);
		grid.add(mpkOutTA, 17, 14);

		// --------------------------------------------------------------------
		// Decrypted ECM

		ecmDecryptedL = new Label("Decrypted\nECM:");
		ecmDecryptedTA = new TextArea("");
		ecmDecryptedTA.setWrapText(true);
		ecmDecryptedTA.setEditable(false);
		ecmDecryptedTA.setTooltip(new Tooltip("Decrypted ECM."));
		ecmDecryptedTA.setMaxSize(240, 60);
		grid.add(ecmDecryptedL, 16, 17);
		grid.add(ecmDecryptedTA, 17, 17);

	}

	// /**
	// * Video Player Input
	// */
	// public void initPlayerInput() {
	// // create media player 1 fx
	// setMediaPlayerInput(new MediaPlayer(SimulatorModel.getMediaInput()));
	// mediaControlInput = new MediaControl(getMediaPlayerInput());
	// GridPane.setColumnSpan(mediaControlInput, 3);
	// GridPane.setRowSpan(mediaControlInput, 8);
	// mediaControlInput.setMinSize(300, 225);
	// mediaControlInput.setPrefSize(300, 225);
	// mediaControlInput.setMaxSize(300, 225);
	// grid.add(mediaControlInput, 1, 4);
	//
	// }

//	/**
//	 * BarChar Input Stream
//	 */
//	public void initBarChartInput() {
//		xAxis = new CategoryAxis();
//		yAxis = new NumberAxis();
//
//		xAxis.setLabel("X"); // Beschriftung
//		yAxis.setLabel("Y");
//
//		bc1 = new BarChart<String, Number>(xAxis, yAxis);
//		GridPane.setColumnSpan(bc1, 3);
//		GridPane.setRowSpan(bc1, 2);
//		bc1.setMaxWidth(350);
//		bc1.setData(SimulatorModel.observableArrayList);
//		// bc1.setScaleX(0.5);
//		// bc1.setScaleY(0.5);
//		// bc1.setMaxSize(10, 10);
//		// bc1.setMinSize(60, 20);
//		grid.add(bc1, 0, 17);
//	}

	public TextField getEcmHeaderTF() {
		return ecmHeaderTF;
	}

	public TextField getScramblingControlTF() {
		return scramblingControlTF;
	}

	public TextField getCwTimeTF() {
		return cwTimeTF;
	}

	public TextField getCwTF() {
		return cwInTF;
	}

	public TextField getAk0InTF() {
		return ak0InTF;
	}

	public TextField getAk1InTF() {
		return ak1InTF;
	}

	public TextField getAk0OutTF() {
		return ak0OutTF;
	}

	public TextField getAk1OutTF() {
		return ak1OutTF;
	}

	public ToggleGroup getRadioButtonGroup() {
		return groupRB;
	}

	public TextField getEcmWorkKey() {
		return ecmWorkKeyIdTF;
	}

	public TextField getEcmProtocolTF() {
		return ecmProtocolTF;
	}

	public TextField getEcmCwOddTF() {
		return ecmCwOddTF;
	}

	public TextField getEcmCwEvenTF() {
		return ecmCwEvenTF;
	}

	public TextField getEcmDateTime() {
		return ecmDateTimeTF;
	}

	public TextField getEcmVariablePartTF() {
		return ecmVariablePartTF;
	}

	public TextField getEcmMacTF() {
		return ecmMacTF;
	}

	public TextField getEcmCrcTF() {
		return ecmCrcTF;
	}

	public Button getVideoOutputButton() {
		return videoOutputB;
	}

	public Button getVideoInputButton() {
		return videoInputB;
	}
	
	public TextField getCwOutTF() {
		return cwOutTF;
	}

	public TextArea getEcmTA() {
		return ecmTA;
	}

	public TextArea getEcmEncryptedTA() {
		return ecmEncryptedTA;
	}

	public TextArea getEcmDecryptedTA() {
		return ecmDecryptedTA;
	}
	
	public ToggleButton getEncryption() {
		return encryptionTB;
	}

	public ToggleButton getDecryption() {
		return decryptionTB;
	}
	
}
