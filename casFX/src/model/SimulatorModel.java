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
	 * Master Private Key (256 bit) for Input Media Player
	 */
	private String masterPrivateKeyInput;

	/**
	 * Master Private Key (256 bit) for Output Media Player
	 */
	private String masterPrivateKeyOutput;

	/**
	 * Simulator Model
	 * 
	 * @param primaryStage
	 *            Session Scope
	 */
	public SimulatorModel(Stage primaryStage) {
		PRIMARY_STAGE = primaryStage;
	}

	/**
	 * Get the primary Stage.
	 * 
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
	 * @param encryptionState
	 *            the encryptionState to set
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
	 * @param decState
	 *            the decryptionState to set
	 */
	public void setDecryptionState(boolean decState) {
		decryptionState = decState;
	}

	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}

	/**
	 * @param inFile
	 *            the inputFile to set
	 */
	public void setInputFile(File inFile) {
		inputFile = inFile;
	}

	/**
	 * @return the outputFile
	 */
	public File getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outFile
	 *            the outputFile to set
	 */
	public void setOutputFile(File outFile) {
		outputFile = outFile;
	}

	/**
	 * @return the controlWordInput
	 */
	public String getControlWordInput() {
		return controlWordInput;
	}

	/**
	 * @param cWordInput
	 *            the controlWordInput to set
	 */
	public void setControlWordInput(String cWordInput) {
		controlWordInput = cWordInput;
	}

	/**
	 * @return the controlWordOutput
	 */
	public String getControlWordOutput() {
		return controlWordOutput;
	}

	/**
	 * @param cWordOutput
	 *            the controlWordOutput to set
	 */
	public void setControlWordOutput(String cWordOutput) {
		controlWordOutput = cWordOutput;
	}

	/**
	 * @return the cwTime
	 */
	public int getCwTime() {
		return cwTime;
	}

	/**
	 * @param cwT
	 *            the cwTime to set
	 */
	public void setCwTime(int cwT) {
		cwTime = cwT;
	}

	/**
	 * @return the scramblingControl
	 */
	public String getScramblingControl() {
		return scramblingControl;
	}

	/**
	 * @param sControl
	 *            the scramblingControl to set
	 */
	public void setScramblingControl(String sControl) {
		scramblingControl = sControl;
	}

	/**
	 * @return the authorizationInputKey0
	 */
	public String getAuthorizationInputKey0() {
		return authorizationInputKey0;
	}

	/**
	 * @param authInputKey0
	 *            the authorizationInputKey0 to set
	 */
	public void setAuthorizationInputKey0(String authInputKey0) {
		authorizationInputKey0 = authInputKey0;
	}

	/**
	 * @return the authorizationInputKey1
	 */
	public String getAuthorizationInputKey1() {
		return authorizationInputKey1;
	}

	/**
	 * @param authInputKey1
	 *            the authorizationInputKey1 to set
	 */
	public void setAuthorizationInputKey1(String authInputKey1) {
		authorizationInputKey1 = authInputKey1;
	}

	/**
	 * @return the authorizationOutputKey0
	 */
	public String getAuthorizationOutputKey0() {
		return authorizationOutputKey0;
	}

	/**
	 * @param authOutputKey0
	 *            the authorizationOutputKey0 to set
	 */
	public void setAuthorizationOutputKey0(String authOutputKey0) {
		authorizationOutputKey0 = authOutputKey0;
	}

	/**
	 * @return the authorizationOutputKey1
	 */
	public String getAuthorizationOutputKey1() {
		return authorizationOutputKey1;
	}

	/**
	 * @param authOutputKey1
	 *            the authorizationOutputKey1 to set
	 */
	public void setAuthorizationOutputKey1(String authOutputKey1) {
		authorizationOutputKey1 = authOutputKey1;
	}

	/**
	 * @return the inputMasterPrivateKey
	 */
	public String getMasterPrivateKeyInput() {
		return masterPrivateKeyInput;
	}

	/**
	 * @param inMasterPrivateKey
	 *            the inputMasterPrivateKey to set
	 */
	public void setMasterPrivateKeyInput(String inMasterPrivateKey) {
		masterPrivateKeyInput = inMasterPrivateKey;
	}

	/**
	 * @return the masterPrivateKeyOutput
	 */
	public String getMasterPrivateKeyOutput() {
		return masterPrivateKeyOutput;
	}

	/**
	 * @param outMasterPrivateKey
	 *            the masterPrivateKeyOutput to set
	 */
	public void setMasterPrivateKeyOutput(String outMasterPrivateKey) {
		masterPrivateKeyOutput = outMasterPrivateKey;
	}

}
