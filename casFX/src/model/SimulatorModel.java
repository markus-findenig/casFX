package model;

import java.io.File;

import javafx.stage.Stage;

/**
 * Model für den Simulator
 */
public class SimulatorModel {

	private static Stage PRIMARY_STAGE = null;

	/**
	 * Encryption State ON (true) or OFF (false)
	 */
	private boolean encryptionState = false;
	
	/**
	 * Decryption State ON (true) or OFF (false)
	 */
	private static boolean decryptionState = false;

	/**
	 * Video Input File
	 */
	private static File inputFile;

	/**
	 * Video Output File
	 */
	private File outputFile;

	/**
	 * Actual Control Word (CW) for Input Player
	 */
	private String controlWordInput;
	
	/**
	 * Actual Control Word (CW) for Output Player
	 */
	private String controlWordOutput;

	/**
	 * Aktuelle Control Word (CW) Gültigkeitsdauer
	 */
	private int cwTime;

	/**
	 * Transport Stream Header - Scrambling Control
	 */
	private String scramblingControl;


	/**
	 * Authorization Key 00 for Input Player
	 */
	private String authorizationInputKey0;
	
	/**
	 * Authorization Key 01 for Input Player
	 */
	private String authorizationInputKey1;
	
	/**
	 * Authorization Key 00 for Output Player
	 */
	private String authorizationOutputKey0;
	
	/**
	 * Authorization Key 01 for Output Player
	 */
	private String authorizationOutputKey1;

	/**
	 * Simulator Model
	 * @param primaryStage - Session Scope
	 */
	public SimulatorModel(Stage primaryStage) {
		PRIMARY_STAGE = primaryStage;
	}

	public static Stage getPrimaryStage() {
		return PRIMARY_STAGE;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		SimulatorModel.inputFile = inputFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
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

	public boolean getEncryptionState() {
		return encryptionState;
	}

	public void setEncryptionState(boolean encryptionState) {
		this.encryptionState = encryptionState;
	}

	public boolean getDecryptionState() {
		return decryptionState;
	}

	public void setDecryptionState(boolean decState) {
		decryptionState = decState;
	}

}
