package model;

import java.io.File;

import javafx.stage.Stage;

/**
 * Simulator Model. The Model for the Simulator.
 */
public class SimulatorModel {

	/**
	 * Primary Stage.
	 */
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
	 * Video Input File.
	 */
	private static File inputFile;

	/**
	 * Video Output File.
	 */
	private File outputFile;

	/**
	 * Actual Control Word (CW) for Input Media Player.
	 */
	private String controlWordInput;
	
	/**
	 * Actual Control Word (CW) for Output Media Player.
	 */
	private String controlWordOutput;

	/**
	 * Current Control Word (CW) duration time.
	 */
	private int cwTime;

	/**
	 * Transport Stream Header - Scrambling Control
	 */
	private String scramblingControl;


	/**
	 * Authorization Key 00 for Input Media Player
	 */
	private String authorizationInputKey0;
	
	/**
	 * Authorization Key 01 for Input Media Player
	 */
	private String authorizationInputKey1;
	
	/**
	 * Authorization Key 00 for Output Media Player
	 */
	private String authorizationOutputKey0;
	
	/**
	 * Authorization Key 01 for Output Media Player
	 */
	private String authorizationOutputKey1;

	/**
	 * Simulator Model
	 * @param primaryStage Session Scope
	 */
	public SimulatorModel(Stage primaryStage) {
		PRIMARY_STAGE = primaryStage;
	}

	/**
	 * Get the primary Stage.
	 * @return The primary Stage.
	 */
	public static Stage getPrimaryStage() {
		return PRIMARY_STAGE;
	}

	/**
	 * @return the encryptionState
	 */
	public boolean isEncryptionState() {
		return encryptionState;
	}

	/**
	 * @param encryptionState the encryptionState to set
	 */
	public void setEncryptionState(boolean encryptionState) {
		this.encryptionState = encryptionState;
	}

	/**
	 * @return the decryptionState
	 */
	public boolean isDecryptionState() {
		return decryptionState;
	}

	/**
	 * @param decryptionState the decryptionState to set
	 */
	public void setDecryptionState(boolean decryptionState) {
		SimulatorModel.decryptionState = decryptionState;
	}

	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(File inputFile) {
		SimulatorModel.inputFile = inputFile;
	}

	/**
	 * @return the outputFile
	 */
	public File getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * @return the controlWordInput
	 */
	public String getControlWordInput() {
		return controlWordInput;
	}

	/**
	 * @param controlWordInput the controlWordInput to set
	 */
	public void setControlWordInput(String controlWordInput) {
		this.controlWordInput = controlWordInput;
	}

	/**
	 * @return the controlWordOutput
	 */
	public String getControlWordOutput() {
		return controlWordOutput;
	}

	/**
	 * @param controlWordOutput the controlWordOutput to set
	 */
	public void setControlWordOutput(String controlWordOutput) {
		this.controlWordOutput = controlWordOutput;
	}

	/**
	 * @return the cwTime
	 */
	public int getCwTime() {
		return cwTime;
	}

	/**
	 * @param cwTime the cwTime to set
	 */
	public void setCwTime(int cwTime) {
		this.cwTime = cwTime;
	}

	/**
	 * @return the scramblingControl
	 */
	public String getScramblingControl() {
		return scramblingControl;
	}

	/**
	 * @param scramblingControl the scramblingControl to set
	 */
	public void setScramblingControl(String scramblingControl) {
		this.scramblingControl = scramblingControl;
	}

	/**
	 * @return the authorizationInputKey0
	 */
	public String getAuthorizationInputKey0() {
		return authorizationInputKey0;
	}

	/**
	 * @param authorizationInputKey0 the authorizationInputKey0 to set
	 */
	public void setAuthorizationInputKey0(String authorizationInputKey0) {
		this.authorizationInputKey0 = authorizationInputKey0;
	}

	/**
	 * @return the authorizationInputKey1
	 */
	public String getAuthorizationInputKey1() {
		return authorizationInputKey1;
	}

	/**
	 * @param authorizationInputKey1 the authorizationInputKey1 to set
	 */
	public void setAuthorizationInputKey1(String authorizationInputKey1) {
		this.authorizationInputKey1 = authorizationInputKey1;
	}

	/**
	 * @return the authorizationOutputKey0
	 */
	public String getAuthorizationOutputKey0() {
		return authorizationOutputKey0;
	}

	/**
	 * @param authorizationOutputKey0 the authorizationOutputKey0 to set
	 */
	public void setAuthorizationOutputKey0(String authorizationOutputKey0) {
		this.authorizationOutputKey0 = authorizationOutputKey0;
	}

	/**
	 * @return the authorizationOutputKey1
	 */
	public String getAuthorizationOutputKey1() {
		return authorizationOutputKey1;
	}

	/**
	 * @param authorizationOutputKey1 the authorizationOutputKey1 to set
	 */
	public void setAuthorizationOutputKey1(String authorizationOutputKey1) {
		this.authorizationOutputKey1 = authorizationOutputKey1;
	}

}
