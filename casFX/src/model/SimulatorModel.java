package model;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.media.Media;
import javafx.stage.Stage;

/**
 * Model für den Simulator
 */
public class SimulatorModel {

	public static Stage PRIMARY_STAGE = null;

	// Video Input File
	private File inputFile;
	private String mediaInputUrl;
	private Media mediaInput;
	
	// Video Input Informations
	private String videoInputType;
	private String videoInputCodec;
	private String videoInputResolution;
	
	// Video Output File
	private File outputFile;
	private String mediaOutputUrl;
	private Media mediaOutput;
	
	// actual CW
	private String controlWordInput;
	private String controlWordOutput;
	
	// Time for CW period of validity
	private int cwTime;
	
	// Transport Stream Header
	private String tsScramblingControl;
	
	// ECM
	private String ecmHeader;
	private String ecmProtocol;
	private String ecmBroadcastId;
	private String ecmWorkKeyId;
	private String ecmCwOdd;
	private String ecmCwEven;
	private String ecmProgramType;
	private String ecmDateTime;
	private String ecmRecordControl;
	private String ecmVariablePart;
	private String ecmMac;
	private String ecmCrc;
	
	// Authorization Keys for Input and Output Player
	private String authorizationInputKey0;
	private String authorizationInputKey1;
	private String authorizationOutputKey0;
	private String authorizationOutputKey1;
	

	// Dummy Array for BarChart
	public ObservableList<XYChart.Series<String, Number>> observableArrayList;
	
	
	public SimulatorModel(Stage primaryStage) {
		PRIMARY_STAGE  = primaryStage;
	}

	public Stage getPrimaryStage() {
		return PRIMARY_STAGE;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public String getMediaInputUrl() {
		return mediaInputUrl;
	}

	public void setMediaInputUrl(String mediaInputUrl) {
		this.mediaInputUrl = mediaInputUrl;
	}

	public Media getMediaInput() {
		return mediaInput;
	}

	public void setMediaInput(Media mediaInput) {
		this.mediaInput = mediaInput;
	}

	public String getVideoInputType() {
		return videoInputType;
	}

	public void setVideoInputType(String videoInputType) {
		this.videoInputType = videoInputType;
	}

	public String getVideoInputCodec() {
		return videoInputCodec;
	}

	public void setVideoInputCodec(String videoInputCodec) {
		this.videoInputCodec = videoInputCodec;
	}

	public String getVideoInputResolution() {
		return videoInputResolution;
	}

	public void setVideoInputResolution(String videoInputResolution) {
		this.videoInputResolution = videoInputResolution;
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

	public Media getMediaOutput() {
		return mediaOutput;
	}

	public void setMediaOutput(Media mediaOutput) {
		this.mediaOutput = mediaOutput;
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

	public String getTsScramblingControl() {
		return tsScramblingControl;
	}

	public void setTsScramblingControl(String tsScramblingControl) {
		this.tsScramblingControl = tsScramblingControl;
	}

	public String getEcmHeader() {
		return ecmHeader;
	}

	public void setEcmHeader(String ecmHeader) {
		this.ecmHeader = ecmHeader;
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
		this.ecmCwOdd = ecmCwOdd;
	}

	public String getEcmCwEven() {
		return ecmCwEven;
	}

	public void setEcmCwEven(String ecmCwEven) {
		this.ecmCwEven = ecmCwEven;
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
		return ecmMac;
	}

	public void setEcmMac(String ecmMac) {
		this.ecmMac = ecmMac;
	}

	public String getEcmCrc() {
		return ecmCrc;
	}

	public void setEcmCrc(String ecmCrc) {
		this.ecmCrc = ecmCrc;
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
		this.observableArrayList = observableArrayList;
	}

	
	
}
