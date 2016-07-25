package model;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.media.Media;
import javafx.stage.Stage;

/**
 * Model f�r den Simulator
 */
public class SimulatorModel {

	private static Stage PRIMARY_STAGE = null;

	// Encryption State ON (true) or OFF (false)
	private boolean encryptionState = false;

	// Video Input File
	private static File inputFile;
	private String mediaInputUrl;
	private static Media mediaInput;

	// Video Output File
	private File outputFile;
	private String mediaOutputUrl;
	private static Media mediaOutput;

	// odd/even Files
	private File oddFile;
	private File evenFile;

	// actual CW
	private String controlWordInput;
	private String controlWordOutput;

	// Time for CW period of validity
	private int cwTime;

	// Transport Stream Header
	private String scramblingControl;

	// ECM
	private String ecmHeader;
	private String ecmProtocol;
	private String ecmBroadcastId;
	private String ecmWorkKeyId;
	private static String ecmCwOdd;
	private static String ecmCwEven;
	private String ecmProgramType;
	private String ecmDateTime;
	private String ecmRecordControl;
	private String ecmVariablePart;
	private String ecmMAC;
	private String ecmCRC;

	// Authorization Keys for Input and Output Player
	private String authorizationInputKey0;
	private String authorizationInputKey1;
	private String authorizationOutputKey0;
	private String authorizationOutputKey1;

	// Dummy Array for BarChart
	public static ObservableList<XYChart.Series<String, Number>> observableArrayList;

	public SimulatorModel(Stage primaryStage) {
		PRIMARY_STAGE = primaryStage;
	}

	public Stage getPrimaryStage() {
		return PRIMARY_STAGE;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		SimulatorModel.inputFile = inputFile;
	}

	public String getMediaInputUrl() {
		return mediaInputUrl;
	}

	public void setMediaInputUrl(String mediaInputUrl) {
		this.mediaInputUrl = mediaInputUrl;
	}

	public static Media getMediaInput() {
		return mediaInput;
	}

	public void setMediaInput(Media mediaInput) {
		SimulatorModel.mediaInput = mediaInput;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public String getMediaOutputUrl() {
		return mediaOutputUrl;
	}

	public void setMediaOutputUrl(String mediaOutputUrl) {
		this.mediaOutputUrl = mediaOutputUrl;
	}

	public static Media getMediaOutput() {
		return mediaOutput;
	}

	public void setMediaOutput(Media mediaOutput) {
		SimulatorModel.mediaOutput = mediaOutput;
	}

	public File getOddFile() {
		return oddFile;
	}

	public void setOddFile(File oddFile) {
		this.oddFile = oddFile;
	}

	public File getEvenFile() {
		return evenFile;
	}

	public void setEvenFile(File evenFile) {
		this.evenFile = evenFile;
	}

	public String getEcmMAC() {
		return ecmMAC;
	}

	public void setEcmMAC(String ecmMAC) {
		this.ecmMAC = ecmMAC;
	}

	public String getEcmCRC() {
		return ecmCRC;
	}

	public void setEcmCRC(String ecmCRC) {
		this.ecmCRC = ecmCRC;
	}

	public String getControlWordInput() {
		return controlWordInput;
	}

	public void setControlWordInput(String controlWordInput) {
		this.controlWordInput = controlWordInput;
	}

	public String getControlWordOutput() {
		return controlWordOutput;
	}

	public void setControlWordOutput(String controlWordOutput) {
		this.controlWordOutput = controlWordOutput;
	}

	public int getCwTime() {
		return cwTime;
	}

	public void setCwTime(int cwTime) {
		this.cwTime = cwTime;
	}

	public String getScramblingControl() {
		return scramblingControl;
	}

	public void setScramblingControl(String sControl) {
		scramblingControl = sControl;
	}

	public String getEcmHeader() {
		return ecmHeader;
	}

	public void setEcmHeader(String eHeader) {
		ecmHeader = eHeader;
	}

	public String getEcmProtocol() {
		return ecmProtocol;
	}

	public void setEcmProtocol(String ecmProtocol) {
		this.ecmProtocol = ecmProtocol;
	}

	public String getEcmBroadcastId() {
		return ecmBroadcastId;
	}

	public void setEcmBroadcastId(String ecmBroadcastId) {
		this.ecmBroadcastId = ecmBroadcastId;
	}

	public String getEcmWorkKeyId() {
		return ecmWorkKeyId;
	}

	public void setEcmWorkKeyId(String ecmWorkKeyId) {
		this.ecmWorkKeyId = ecmWorkKeyId;
	}

	public String getEcmCwOdd() {
		return ecmCwOdd;
	}

	public void setEcmCwOdd(String ecmCwOdd) {
		SimulatorModel.ecmCwOdd = ecmCwOdd;
	}

	public String getEcmCwEven() {
		return ecmCwEven;
	}

	public void setEcmCwEven(String ecmCwEven) {
		SimulatorModel.ecmCwEven = ecmCwEven;
	}

	public String getEcmProgramType() {
		return ecmProgramType;
	}

	public void setEcmProgramType(String ecmProgramType) {
		this.ecmProgramType = ecmProgramType;
	}

	public String getEcmDateTime() {
		return ecmDateTime;
	}

	public void setEcmDateTime(String ecmDateTime) {
		this.ecmDateTime = ecmDateTime;
	}

	public String getEcmRecordControl() {
		return ecmRecordControl;
	}

	public void setEcmRecordControl(String ecmRecordControl) {
		this.ecmRecordControl = ecmRecordControl;
	}

	public String getEcmVariablePart() {
		return ecmVariablePart;
	}

	public void setEcmVariablePart(String ecmVariablePart) {
		this.ecmVariablePart = ecmVariablePart;
	}

	public String getEcmMac() {
		return ecmMAC;
	}

	public void setEcmMac(String ecmMac) {
		this.ecmMAC = ecmMac;
	}

	public String getEcmCrc() {
		return ecmCRC;
	}

	public void setEcmCrc(String ecmCrc) {
		this.ecmCRC = ecmCrc;
	}

	public String getAuthorizationInputKey0() {
		return authorizationInputKey0;
	}

	public void setAuthorizationInputKey0(String authorizationInputKey0) {
		this.authorizationInputKey0 = authorizationInputKey0;
	}

	public String getAuthorizationInputKey1() {
		return authorizationInputKey1;
	}

	public void setAuthorizationInputKey1(String authorizationInputKey1) {
		this.authorizationInputKey1 = authorizationInputKey1;
	}

	public String getAuthorizationOutputKey0() {
		return authorizationOutputKey0;
	}

	public void setAuthorizationOutputKey0(String authorizationOutputKey0) {
		this.authorizationOutputKey0 = authorizationOutputKey0;
	}

	public String getAuthorizationOutputKey1() {
		return authorizationOutputKey1;
	}

	public void setAuthorizationOutputKey1(String authorizationOutputKey1) {
		this.authorizationOutputKey1 = authorizationOutputKey1;
	}

	public ObservableList<XYChart.Series<String, Number>> getObservableArrayList() {
		return observableArrayList;
	}

	public void setObservableArrayList(ObservableList<XYChart.Series<String, Number>> observableArrayList) {
		SimulatorModel.observableArrayList = observableArrayList;
	}

	public boolean getEncryptionState() {
		return encryptionState;
	}

	public void setEncryptionState(boolean encryptionState) {
		this.encryptionState = encryptionState;
	}

}
