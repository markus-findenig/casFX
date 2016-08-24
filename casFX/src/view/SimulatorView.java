package view;

import javafx.stage.WindowEvent;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.stage.Stage;

/**
 * Simulator View.
 */
public class SimulatorView {

	/**
	 * Scene for Simulator View.
	 */
	private Scene scene;

	/**
	 * Grid Pane for Simulator View.
	 */
	private GridPane grid;

	/**
	 * Width of the Simulator View.
	 */
	private double width = 1024;

	/**
	 * Height of the Simulator View.
	 */
	private double height = 650;

	/**
	 * Menu File.
	 */
	private final Menu menuFile = new Menu("File");

	/**
	 * Menu Options.
	 */
	private final Menu menuOptions = new Menu("Options");

	/**
	 * Menu Help.
	 */
	private final Menu menuHelp = new Menu("Help");

	/**
	 * Menu Item Open.
	 */
	private final MenuItem open = new MenuItem("Open");

	/**
	 * Menu Item Exit.
	 */
	private final MenuItem exit = new MenuItem("Exit");

	/**
	 * Menu Item About.
	 */
	private final MenuItem about = new MenuItem("About");

	/**
	 * Menu Item Config.
	 */
	private final MenuItem config = new MenuItem("Config");

	/**
	 * Encryption Toggle Button. Run the Encryption.
	 */
	private static ToggleButton encryptionTB;

	/**
	 * Decryption Toggle Button. Run the Decryption.
	 */
	private static ToggleButton decryptionTB;

	/**
	 * EMM Button. Send one EMM Message.
	 */
	private static Button sendEMMB;

	/**
	 * Time interval for ControlWord (CW)
	 */
	private static TextField cwTimeTF;

	/**
	 * Text Field for Control Word (CW) from Input Player.
	 */
	private static TextField cwInTF;

	/**
	 * Text Field for Authorization Key 0 (128 bit) from Input Player.
	 */
	private static TextField ak0InTF;

	/**
	 * Text Field for Authorization Key 1 (128 bit) from Input Player.
	 */
	private static TextField ak1InTF;

	/**
	 * Toggle Group for Radio Buttons. If selected Authorization Key 0 or
	 * Authorization Key 1.
	 */
	private static ToggleGroup groupRB;

	/**
	 * Text Area for Master Private Key (256 bit) from Input Player.
	 */
	private static TextArea mpkInTA;

	/**
	 * Text Area for VLC parameter at constant Control Word (CW).
	 */
	private static TextArea parameterVLCstreamTA;

	/**
	 * Text Field for scrambling Control Informations.
	 */
	private static TextField scramblingControlTF;

	/**
	 * Text Field for ECM Section Header.
	 */
	private static TextField ecmHeaderTF;

	/**
	 * Text Field for ECM Protocol number.
	 */
	private static TextField ecmProtocolTF;

	/**
	 * Text Field for ECM Broadcaster group identifier.
	 */
	private static TextField ecmBroadcastIdTF;

	/**
	 * Text Field for ECM Work key identifier.
	 */
	private static TextField ecmWorkKeyIdTF;

	/**
	 * Text Field for ECM Control Word (CW), Scrambling key odd.
	 */
	private static TextField ecmCwOddTF;

	/**
	 * Text Field for ECM Control Word (CW), Scrambling key even.
	 */
	private static TextField ecmCwEvenTF;

	/**
	 * Text Field for ECM Program type.
	 */
	private static TextField ecmProgramTypeTF;

	/**
	 * Text Field for ECM Date Time.
	 */
	private static TextField ecmDateTimeTF;

	/**
	 * Text Field for ECM Recording control.
	 */
	private static TextField ecmRecordControlTF;

	/**
	 * Text Field for ECM Payload.
	 */
	private static TextField ecmVariablePartTF;

	/**
	 * Text Field for ECM Message Authentication Code (MAC, 4 Bytes).
	 */
	private static TextField ecmMacTF;

	/**
	 * Text Field for ECM Cyclic Redundancy Check (CRC, 4 Bytes).
	 */
	private static TextField ecmCrcTF;

	/**
	 * Text Field for Control Word (CW) from Output Player.
	 */
	private static TextField cwOutTF;

	/**
	 * Text Field for Authorization Key 0 (128 bit) from Output Player.
	 */
	private static TextField ak0OutTF;

	/**
	 * Text Field for Authorization Key 1 (128 bit) from Output Player.
	 */
	private static TextField ak1OutTF;

	/**
	 * Text Area for Master Private Key (256 bit) from Output Player.
	 */
	private static TextArea mpkOutTA;

	/**
	 * Text Area for ECM.
	 */
	private static TextArea ecmTA;

	/**
	 * Text Area for encrypted ECM.
	 */
	private static TextArea ecmEncryptedTA;

	/**
	 * Text Area for decrypted ECM.
	 */
	private static TextArea ecmDecryptedTA;

	/**
	 * Text Area for EMM.
	 */
	private static TextArea emmTA;

	/**
	 * Text Area for encrypted EMM.
	 */
	private static TextArea emmEncryptedTA;

	/**
	 * Text Area for decrypted EMM.
	 */
	private static TextArea emmDecryptedTA;

	/**
	 * Button for Video Input Player.
	 */
	private static Button videoInputB;

	/**
	 * Button for Video Output Player.
	 */
	private static Button videoOutputB;

	/**
	 * Text Area for Error Messages from Video Output Player.
	 */
	private static TextArea errorOutputTA;

	/**
	 * Simulator View. Generate the View from Simulator.
	 */
	public SimulatorView() {

		// Layout
		grid = new GridPane();
		grid.setPadding(new Insets(0, 0, 0, 0));
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(5);
		grid.setVgap(5);

		// Menu Bar
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menuFile, menuOptions, menuHelp);
		GridPane.setColumnSpan(menuBar, 20);
		// menuBar.prefWidthProperty().bind(SimulatorModel.getPrimaryStage().widthProperty());
		grid.add(menuBar, 0, 0);

		menuFile.getItems().add(open);
		menuFile.getItems().add(exit);

		menuOptions.getItems().add(config);

		menuHelp.getItems().add(about);

		initGUI();

		// Erstellt eine neue Scene
		scene = new Scene(grid, width, height);
		scene.setRoot(grid);

	}

	/**
	 * View the Simulator View.
	 * 
	 * @param stage
	 *            current Stage.
	 */
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

	/**
	 * 
	 * @return the open menu
	 */
	public MenuItem getOpen() {
		return open;
	}

	/**
	 * 
	 * @return the exit menu
	 */
	public MenuItem getExit() {
		return exit;
	}

	/**
	 * 
	 * @return the config menu
	 */
	public MenuItem getConfig() {
		return config;
	}
	
	/**
	 * 
	 * @return the about menu
	 */
	public MenuItem getAbout() {
		return about;
	}

	/**
	 * Initializes the GUI elements.
	 */
	public void initGUI() {

		// TODO Debug
		// grid.setGridLinesVisible(true);

		// --------------------------------------------------------------------
		// Encryption Toggle Button
		Label encryptionL = new Label("Encryption:");
		encryptionTB = new ToggleButton("OFF");
		encryptionTB.setDisable(true);
		encryptionTB.setPrefWidth(80);
		grid.add(encryptionL, 1, 2);
		grid.add(encryptionTB, 2, 2);

		// --------------------------------------------------------------------
		// Video Player Input
		Label videoInputL = new Label("Input\nPlayer:");
		videoInputL.setPrefWidth(80);
		videoInputB = new Button("View");
		videoInputB.setDisable(true);
		videoInputB.setPrefWidth(80);
		grid.add(videoInputL, 1, 4);
		grid.add(videoInputB, 2, 4);

		// --------------------------------------------------------------------
		// EMM Button
		Label sendEMML = new Label("EMM:");
		sendEMMB = new Button("Send");
		sendEMMB.setDisable(true);
		sendEMMB.setPrefWidth(80);
		grid.add(sendEMML, 1, 6);
		grid.add(sendEMMB, 2, 6);

		// --------------------------------------------------------------------
		// Time interval for ControlWord (CW)
		Label cwTimeL = new Label("Time (sec):");
		cwTimeTF = new TextField("0");
		cwTimeTF.setMaxWidth(120);
		cwTimeTF.setTooltip(
				new Tooltip("Time in seconds. 0 if Constant Control Word or 10-60 if Intervall Control Word."));
		cwTimeTF.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					cwTimeTF.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		grid.add(cwTimeL, 1, 8);
		grid.add(cwTimeTF, 2, 8);

		// --------------------------------------------------------------------
		// ECM Input Keys
		Label cwInL = new Label("CW:");
		cwInTF = new TextField("0123456789ABCDEF");
		cwInTF.setStyle("-fx-background-color: transparent;");
		cwInTF.setEditable(false);
		cwInTF.setTooltip(new Tooltip("Control Word (64 bit)"));
		grid.add(cwInL, 1, 10);
		grid.add(cwInTF, 2, 10);

		groupRB = new ToggleGroup();
		RadioButton ak0InRB = new RadioButton();
		RadioButton ak1InRB = new RadioButton();
		ak0InRB.setToggleGroup(groupRB);
		ak1InRB.setToggleGroup(groupRB);
		ak0InRB.setUserData("00");
		ak1InRB.setUserData("01");
		ak0InRB.setSelected(true);
		grid.add(ak0InRB, 0, 12);
		grid.add(ak1InRB, 0, 13);

		Label ak0InL = new Label("AK 00:");
		ak0InTF = new TextField();
		ak0InTF.setEditable(true);
		ak0InTF.setTooltip(new Tooltip("Authorization Key 0 (128 bit)"));
		ak0InTF.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[0-9a-fA-F]{0,7}")) {
					ak0InTF.setText(newValue.replaceAll("[^0-9a-fA-F]", ""));
				}
				if (ak0InTF.getText().length() >= 32) {
					String s = ak0InTF.getText().substring(0, 32);
					ak0InTF.setText(s);
				}
			}
		});
		ak0InTF.setMinWidth(230);
		grid.add(ak0InL, 1, 12);
		grid.add(ak0InTF, 2, 12);

		Label ak1InL = new Label("AK 01:");
		ak1InTF = new TextField();
		ak1InTF.setEditable(true);
		ak1InTF.setTooltip(new Tooltip("Authorization Key 1 (128 bit)"));
		ak1InTF.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[0-9a-fA-F]{0,7}")) {
					ak1InTF.setText(newValue.replaceAll("[^0-9a-fA-F]", ""));
				}
				if (ak1InTF.getText().length() >= 32) {
					String s = ak1InTF.getText().substring(0, 32);
					ak1InTF.setText(s);
				}
			}
		});
		ak1InTF.setMinWidth(230);
		grid.add(ak1InL, 1, 13);
		grid.add(ak1InTF, 2, 13);

		Label mpkInL = new Label("MPK:");
		mpkInTA = new TextArea("00112233445566778899AABBCCDDEEFFFFEEDDCCBBAA99887766554433221100");
		mpkInTA.setWrapText(true);
		mpkInTA.setTooltip(new Tooltip("Master Private Key (256 bit)."));
		mpkInTA.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[0-9a-fA-F]{0,7}")) {
					mpkInTA.setText(newValue.replaceAll("[^0-9a-fA-F]", ""));
				}
				if (mpkInTA.getText().length() >= 64) {
					String s = mpkInTA.getText().substring(0, 64);
					mpkInTA.setText(s);
				}
			}
		});
		mpkInTA.setMaxSize(250, 45);
		grid.add(mpkInL, 1, 14);
		grid.add(mpkInTA, 2, 14);

		// --------------------------------------------------------------------
		// VLC
		Label parameterVLCstreamL = new Label("VLC\nParameter:");
		parameterVLCstreamTA = new TextArea();
		parameterVLCstreamTA.setEditable(true);
		parameterVLCstreamTA.setStyle("-fx-background-color: transparent;");
		parameterVLCstreamTA.setTooltip(new Tooltip("Only if Constant CW, Time (sec) = 0"));
		parameterVLCstreamTA.setMaxSize(250, 45);
		grid.add(parameterVLCstreamL, 1, 15);
		grid.add(parameterVLCstreamTA, 2, 15);

		// --------------------------------------------------------------------
		// Current ECM
		Label ecmL = new Label("Current\nECM:");
		ecmTA = new TextArea("");
		ecmTA.setWrapText(true);
		ecmTA.setEditable(false);
		ecmTA.setStyle("-fx-background-color: green;");
		ecmTA.setTooltip(new Tooltip("Current ECM."));
		ecmTA.setMaxSize(240, 60);
		grid.add(ecmL, 1, 17);
		grid.add(ecmTA, 2, 17);

		// --------------------------------------------------------------------
		// Current EMM
		Label emmL = new Label("Current\nEMM:");
		emmTA = new TextArea("");
		emmTA.setWrapText(true);
		emmTA.setEditable(false);
		emmTA.setStyle("-fx-background-color: yellow;");
		emmTA.setTooltip(new Tooltip("Current EMM."));
		emmTA.setMaxSize(240, 60);
		grid.add(emmL, 1, 19);
		grid.add(emmTA, 2, 19);

		// --------------------------------------------------------------------
		// Transport Stream Header
		Label scramblingControlL = new Label("Scrambling\nControl (2 bits):");
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
		Label ecmHeaderL = new Label("ECM Section\nHeader:");
		ecmHeaderL.setStyle("-fx-background-color: lightgray;");
		ecmHeaderTF = new TextField("8000000000000000");
		ecmHeaderTF.setEditable(false);
		ecmHeaderTF.setStyle("-fx-background-color: lightgray;");
		ecmHeaderTF.setTooltip(new Tooltip("ECM Section Header (64 bit)"));
		grid.add(ecmHeaderL, 8, 4);
		grid.add(ecmHeaderTF, 9, 4);

		Label ecmProtocolL = new Label("Protocol Nr.:");
		ecmProtocolL.setStyle("-fx-background-color: lightgray;");
		ecmProtocolTF = new TextField("AA");
		ecmProtocolTF.setEditable(false);
		ecmProtocolTF.setStyle("-fx-background-color: lightgray;");
		ecmProtocolTF.setTooltip(new Tooltip("Protocol number (8 bit)"));
		grid.add(ecmProtocolL, 8, 5);
		grid.add(ecmProtocolTF, 9, 5);

		Label ecmBroadcastIdL = new Label("Broadcast ID:");
		ecmBroadcastIdL.setStyle("-fx-background-color: lightgray;");
		ecmBroadcastIdTF = new TextField("FF");
		ecmBroadcastIdTF.setEditable(false);
		ecmBroadcastIdTF.setStyle("-fx-background-color: lightgray;");
		ecmBroadcastIdTF.setTooltip(new Tooltip("Broadcast group id (8 bit)"));
		grid.add(ecmBroadcastIdL, 8, 6);
		grid.add(ecmBroadcastIdTF, 9, 6);

		Label ecmWorkKeyIdL = new Label("AK id:");
		ecmWorkKeyIdL.setStyle("-fx-background-color: lightgray;");
		ecmWorkKeyIdTF = new TextField("00");
		ecmWorkKeyIdTF.setEditable(false);
		ecmWorkKeyIdTF.setStyle("-fx-background-color: lightgray;");
		ecmWorkKeyIdTF.setTooltip(new Tooltip("Work key id (8 bit)"));
		grid.add(ecmWorkKeyIdL, 8, 7);
		grid.add(ecmWorkKeyIdTF, 9, 7);

		Label ecmCwOddL = new Label("CW (odd):");
		ecmCwOddL.setStyle("-fx-background-color: lightgray;");
		ecmCwOddTF = new TextField("0123456789ABCDEF");
		ecmCwOddTF.setEditable(false);
		ecmCwOddTF.setStyle("-fx-background-color: lightgray;");
		ecmCwOddTF.setTooltip(new Tooltip("Scrambling key odd (64 bit)"));
		grid.add(ecmCwOddL, 8, 8);
		grid.add(ecmCwOddTF, 9, 8);

		Label ecmCwEvenL = new Label("CW (even)");
		ecmCwEvenL.setStyle("-fx-background-color: lightgray;");
		ecmCwEvenTF = new TextField("FEDCBA9876543210");
		ecmCwEvenTF.setEditable(false);
		ecmCwEvenTF.setStyle("-fx-background-color: lightgray;");
		ecmCwEvenTF.setTooltip(new Tooltip("Scrambling key even (64 bit)"));
		grid.add(ecmCwEvenL, 8, 9);
		grid.add(ecmCwEvenTF, 9, 9);

		Label ecmProgramTypeL = new Label("Program:");
		ecmProgramTypeL.setStyle("-fx-background-color: lightgray;");
		ecmProgramTypeTF = new TextField("C8");
		ecmProgramTypeTF.setEditable(false);
		ecmProgramTypeTF.setStyle("-fx-background-color: lightgray;");
		ecmProgramTypeTF.setTooltip(new Tooltip("Program type (8 bit)"));
		grid.add(ecmProgramTypeL, 8, 10);
		grid.add(ecmProgramTypeTF, 9, 10);

		Label ecmDateTimeL = new Label("Date/Time:");
		ecmDateTimeL.setStyle("-fx-background-color: lightgray;");
		ecmDateTimeTF = new TextField("0123456789");
		ecmDateTimeTF.setEditable(false);
		ecmDateTimeTF.setStyle("-fx-background-color: lightgray;");
		ecmDateTimeTF.setTooltip(new Tooltip("Date/Time (40 bit)"));
		grid.add(ecmDateTimeL, 8, 11);
		grid.add(ecmDateTimeTF, 9, 11);

		Label ecmRecordControlL = new Label("Recording:");
		ecmRecordControlL.setStyle("-fx-background-color: lightgray;");
		ecmRecordControlTF = new TextField("D5");
		ecmRecordControlTF.setEditable(false);
		ecmRecordControlTF.setStyle("-fx-background-color: lightgray;");
		ecmRecordControlTF.setTooltip(new Tooltip("Recording control (8 bit)"));
		grid.add(ecmRecordControlL, 8, 12);
		grid.add(ecmRecordControlTF, 9, 12);

		Label ecmVariablePartL = new Label("Variable part:");
		ecmVariablePartL.setStyle("-fx-background-color: lightgray;");
		ecmVariablePartTF = new TextField("00000000");
		ecmVariablePartTF.setEditable(false);
		ecmVariablePartTF.setStyle("-fx-background-color: lightgray;");
		ecmRecordControlTF
				.setTooltip(new Tooltip("Variable part \nCapable of accommodating various function information"));
		grid.add(ecmVariablePartL, 8, 13);
		grid.add(ecmVariablePartTF, 9, 13);

		Label ecmMacL = new Label("Payload MAC:");
		ecmMacL.setStyle("-fx-background-color: lightgray;");
		ecmMacTF = new TextField("01234567");
		ecmMacTF.setEditable(false);
		ecmMacTF.setStyle("-fx-background-color: lightgray;");
		ecmMacTF.setTooltip(new Tooltip("(Message Authentification Code (32 bit)"));
		grid.add(ecmMacL, 8, 14);
		grid.add(ecmMacTF, 9, 14);

		Label ecmCrcL = new Label("Section CRC:");
		ecmCrcL.setStyle("-fx-background-color: lightgray;");
		ecmCrcTF = new TextField("89ABCDEF");
		ecmCrcTF.setEditable(false);
		ecmCrcTF.setStyle("-fx-background-color: lightgray;");
		ecmMacTF.setTooltip(new Tooltip("Cyclic Redundancy Check (32 bit)"));
		grid.add(ecmCrcL, 8, 15);
		grid.add(ecmCrcTF, 9, 15);

		// --------------------------------------------------------------------
		// Encrypted ECM
		Label ecmEncryptedL = new Label("Encrypted\nECM:");
		ecmEncryptedTA = new TextArea("");
		ecmEncryptedTA.setWrapText(true);
		ecmEncryptedTA.setEditable(false);
		ecmEncryptedTA.setStyle("-fx-background-color: green;");
		ecmEncryptedTA.setTooltip(new Tooltip("Encrypted ECM."));
		ecmEncryptedTA.setMaxSize(240, 60);
		grid.add(ecmEncryptedL, 8, 17);
		grid.add(ecmEncryptedTA, 9, 17);

		// --------------------------------------------------------------------
		// Encrypted EMM
		Label emmEncryptedL = new Label("Encrypted\nEMM:");
		emmEncryptedTA = new TextArea("");
		emmEncryptedTA.setWrapText(true);
		emmEncryptedTA.setEditable(false);
		emmEncryptedTA.setStyle("-fx-background-color: yellow;");
		emmEncryptedTA.setTooltip(new Tooltip("Encrypted EMM."));
		emmEncryptedTA.setMaxSize(240, 60);
		grid.add(emmEncryptedL, 8, 19);
		grid.add(emmEncryptedTA, 9, 19);

		// --------------------------------------------------------------------
		// Encryption Toggle Button
		Label decryptionL = new Label("Decryption:");
		grid.add(decryptionL, 16, 2);
		decryptionTB = new ToggleButton("OFF");
		decryptionTB.setPrefWidth(80);
		grid.add(decryptionTB, 17, 2);

		// --------------------------------------------------------------------
		// Video Output
		Label videoOutputL = new Label("Output\nPlayer:");
		videoOutputL.setPrefWidth(80);
		videoOutputB = new Button("View");
		videoOutputB.setDisable(true);
		videoOutputB.setPrefWidth(80);
		grid.add(videoOutputL, 16, 4);
		grid.add(videoOutputB, 17, 4);

		// --------------------------------------------------------------------
		// ECM Output Keys
		Label cwOutL = new Label("CW:");
		cwOutTF = new TextField("-- WAIT FOR ECM/EMM --");
		cwOutTF.setStyle("-fx-background-color: transparent;");
		cwOutTF.setEditable(false);
		cwOutTF.setTooltip(new Tooltip("Control Word (64 bit)"));
		grid.add(cwOutL, 16, 10);
		grid.add(cwOutTF, 17, 10);

		Label ak0OutL = new Label("AK 00:");
		ak0OutTF = new TextField("");
		ak0OutTF.setEditable(true);
		ak0OutTF.setTooltip(new Tooltip("Authorization Key 0 (128 bit)"));
		ak0OutTF.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[0-9a-fA-F]{0,7}")) {
					ak0OutTF.setText(newValue.replaceAll("[^0-9a-fA-F]", ""));
				}
				if (ak0OutTF.getText().length() >= 32) {
					String s = ak0OutTF.getText().substring(0, 32);
					ak0OutTF.setText(s);
				}
			}
		});
		ak0OutTF.setMinWidth(230);
		grid.add(ak0OutL, 16, 12);
		grid.add(ak0OutTF, 17, 12);

		Label ak1OutL = new Label("AK 01:");
		ak1OutTF = new TextField("");
		ak1OutTF.setEditable(true);
		ak1OutTF.setTooltip(new Tooltip("Authorization Key 1 (128 bit)"));
		ak1OutTF.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[0-9a-fA-F]{0,7}")) {
					ak1OutTF.setText(newValue.replaceAll("[^0-9a-fA-F]", ""));
				}
				if (ak1OutTF.getText().length() >= 32) {
					String s = ak1OutTF.getText().substring(0, 32);
					ak1OutTF.setText(s);
				}
			}
		});
		ak1OutTF.setMinWidth(230);
		grid.add(ak1OutL, 16, 13);
		grid.add(ak1OutTF, 17, 13);

		Label mpkOutL = new Label("MPK:");
		mpkOutTA = new TextArea("00112233445566778899AABBCCDDEEFFFFEEDDCCBBAA99887766554433221100");
		mpkOutTA.setWrapText(true);
		mpkOutTA.setTooltip(new Tooltip("Master Private Key (256 bit)"));
		mpkOutTA.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[0-9a-fA-F]{0,7}")) {
					mpkOutTA.setText(newValue.replaceAll("[^0-9a-fA-F]", ""));
				}
				if (mpkOutTA.getText().length() >= 64) {
					String s = mpkOutTA.getText().substring(0, 64);
					mpkOutTA.setText(s);
				}
			}
		});
		mpkOutTA.setMaxSize(250, 45);
		grid.add(mpkOutL, 16, 14);
		grid.add(mpkOutTA, 17, 14);

		// --------------------------------------------------------------------
		// Error Output Player
		Label errorOutputL = new Label("ERROR:");
		errorOutputTA = new TextArea();
		errorOutputTA.setEditable(false);
		errorOutputTA.setStyle("-fx-background-color: transparent;");
		errorOutputTA.setTooltip(new Tooltip("View Error Messages."));
		errorOutputTA.setMaxSize(250, 45);
		grid.add(errorOutputL, 16, 15);
		grid.add(errorOutputTA, 17, 15);

		// --------------------------------------------------------------------
		// Decrypted ECM
		Label ecmDecryptedL = new Label("Decrypted\nECM:");
		ecmDecryptedTA = new TextArea();
		ecmDecryptedTA.setWrapText(true);
		ecmDecryptedTA.setEditable(false);
		ecmDecryptedTA.setStyle("-fx-background-color: green;");
		ecmDecryptedTA.setTooltip(new Tooltip("Decrypted ECM."));
		ecmDecryptedTA.setMaxSize(240, 60);
		grid.add(ecmDecryptedL, 16, 17);
		grid.add(ecmDecryptedTA, 17, 17);

		// --------------------------------------------------------------------
		// Decrypted EMM
		Label emmDecryptedL = new Label("Decrypted\nEMM:");
		emmDecryptedTA = new TextArea("");
		emmDecryptedTA.setWrapText(true);
		emmDecryptedTA.setEditable(false);
		emmDecryptedTA.setStyle("-fx-background-color: yellow;");
		emmDecryptedTA.setTooltip(new Tooltip("Decrypted EMM."));
		emmDecryptedTA.setMaxSize(240, 60);
		grid.add(emmDecryptedL, 16, 19);
		grid.add(emmDecryptedTA, 17, 19);

	}

	/**
	 * @return the menuFile
	 */
	public Menu getMenuFile() {
		return menuFile;
	}

	/**
	 * @return the menuOptions
	 */
	public Menu getMenuOptions() {
		return menuOptions;
	}

	/**
	 * @return the menuHelp
	 */
	public Menu getMenuHelp() {
		return menuHelp;
	}

	/**
	 * @return the test
	 */
	public MenuItem getTest() {
		return about;
	}

	/**
	 * @return the encryptionTB
	 */
	public ToggleButton getEncryption() {
		return encryptionTB;
	}

	/**
	 * @return the decryptionTB
	 */
	public ToggleButton getDecryption() {
		return decryptionTB;
	}

	/**
	 * @return the getSendEMMButton
	 */
	public Button getSendEMMButton() {
		return sendEMMB;
	}

	/**
	 * @return the cwTimeTF
	 */
	public TextField getCwTimeTF() {
		return cwTimeTF;
	}

	/**
	 * @return the cwInTF
	 */
	public TextField getCwInTF() {
		return cwInTF;
	}

	/**
	 * @return the ak0InTF
	 */
	public TextField getAk0InTF() {
		return ak0InTF;
	}

	/**
	 * @return the ak1InTF
	 */
	public TextField getAk1InTF() {
		return ak1InTF;
	}

	/**
	 * @return the groupRB
	 */
	public ToggleGroup getRadioButtonGroup() {
		return groupRB;
	}

	/**
	 * @return the mpkInTA
	 */
	public TextArea getMpkInTA() {
		return mpkInTA;
	}

	/**
	 * @return the parameterVLCstreamTA
	 */
	public TextArea getParameterVLCstream() {
		return parameterVLCstreamTA;
	}

	/**
	 * @return the scramblingControlTF
	 */
	public TextField getScramblingControl() {
		return scramblingControlTF;
	}

	/**
	 * @return the ecmHeaderTF
	 */
	public TextField getEcmHeaderTF() {
		return ecmHeaderTF;
	}

	/**
	 * @return the ecmProtocolTF
	 */
	public TextField getEcmProtocolTF() {
		return ecmProtocolTF;
	}

	/**
	 * @return the ecmBroadcastIdTF
	 */
	public TextField getEcmBroadcastIdTF() {
		return ecmBroadcastIdTF;
	}

	/**
	 * @return the ecmWorkKeyIdTF
	 */
	public TextField getEcmWorkKeyIdTF() {
		return ecmWorkKeyIdTF;
	}

	/**
	 * @return the ecmCwOddTF
	 */
	public TextField getEcmCwOddTF() {
		return ecmCwOddTF;
	}

	/**
	 * @return the ecmCwEvenTF
	 */
	public TextField getEcmCwEvenTF() {
		return ecmCwEvenTF;
	}

	/**
	 * @return the ecmProgramTypeTF
	 */
	public TextField getEcmProgramTypeTF() {
		return ecmProgramTypeTF;
	}

	/**
	 * @return the ecmDateTimeTF
	 */
	public TextField getEcmDateTimeTF() {
		return ecmDateTimeTF;
	}

	/**
	 * @return the ecmRecordControlTF
	 */
	public TextField getEcmRecordControlTF() {
		return ecmRecordControlTF;
	}

	/**
	 * @return the ecmVariablePartTF
	 */
	public TextField getEcmVariablePartTF() {
		return ecmVariablePartTF;
	}

	/**
	 * @return the ecmMacTF
	 */
	public TextField getEcmMacTF() {
		return ecmMacTF;
	}

	/**
	 * @return the ecmCrcTF
	 */
	public TextField getEcmCrcTF() {
		return ecmCrcTF;
	}

	/**
	 * @return the cwOutTF
	 */
	public TextField getCwOutTF() {
		return cwOutTF;
	}

	/**
	 * @return the ak0OutTF
	 */
	public TextField getAk0OutTF() {
		return ak0OutTF;
	}

	/**
	 * @return the ak1OutTF
	 */
	public TextField getAk1OutTF() {
		return ak1OutTF;
	}

	/**
	 * @return the mpkOutTA
	 */
	public TextArea getMpkOutTA() {
		return mpkOutTA;
	}

	/**
	 * @return the ecmTA
	 */
	public TextArea getEcmTA() {
		return ecmTA;
	}

	/**
	 * @return the ecmEncryptedTA
	 */
	public TextArea getEcmEncryptedTA() {
		return ecmEncryptedTA;
	}

	/**
	 * @return the ecmDecryptedTA
	 */
	public TextArea getEcmDecryptedTA() {
		return ecmDecryptedTA;
	}

	/**
	 * @return the emmTA
	 */
	public TextArea getEmmTA() {
		return emmTA;
	}

	/**
	 * @return the emmEncryptedTA
	 */
	public TextArea getEmmEncryptedTA() {
		return emmEncryptedTA;
	}

	/**
	 * @return the emmDecryptedTA
	 */
	public TextArea getEmmDecryptedTA() {
		return emmDecryptedTA;
	}

	/**
	 * @return the videoInputButton
	 */
	public Button getVideoInputButton() {
		return videoInputB;
	}

	/**
	 * @return the videoOutputButton
	 */
	public Button getVideoOutputButton() {
		return videoOutputB;
	}

	/**
	 * @return the errorOutputTA
	 */
	public TextArea getErrorOutputTA() {
		return errorOutputTA;
	}
	
}
