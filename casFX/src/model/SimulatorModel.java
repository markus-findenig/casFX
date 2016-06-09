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
	public File inputFile;
	public String mediaInputUrl;
	public Media mediaInput;
	
	// Video Input Informations
	public String videoInputType;
	public String videoInputCodec;
	public String videoInputResolution;
	
	// Video Output File
	public File outputFile;
	public String mediaOutputUrl;
	public Media mediaOutput;
	
	// actual CW
	public String controlWordInput;
	public String controlWordOutput;
	
	// Time for CW period of validity
	public int cwTime;
	
	// Transport Stream Header
	public String tsScramblingControl;
	
	// ECM
	public String ecmHeader;
	public String ecmProtocol;
	public String ecmBroadcastId;
	public String ecmWorkKeyId;
	public String ecmCwOdd;
	public String ecmCwEven;
	public String ecmProgramType;
	public String ecmDateTime;
	public String ecmRecordControl;
	public String ecmVariablePart;
	public String ecmMac;
	public String ecmCrc;
	
	// Authorization Keys for Input and Output Player
	public String authorizationInputKey0;
	public String authorizationInputKey1;
	public String authorizationOutputKey0;
	public String authorizationOutputKey1;
	

	// Dummy Array for BarChart
	public ObservableList<XYChart.Series<String, Number>> observableArrayList;
	
	
	public SimulatorModel(Stage primaryStage) {
		PRIMARY_STAGE  = primaryStage;
	}

	public Stage getPrimaryStage() {
		return PRIMARY_STAGE;
	}

	
}
