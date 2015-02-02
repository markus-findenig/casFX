package model;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.media.Media;
import javafx.stage.Stage;

public class SimulatorModel {

	public static Stage PRIMARY_STAGE = null;

	// Video Input File
	public File inputFile;
	public String mediaInputUrl;
	public Media mediaInput;
	
	// Video Output File
	public File outputFile;
	public String mediaOutputUrl;
	public Media mediaOutput;
	
	// actual CW
	public String controlWord;
	
	public int cwTime;
	
	// ECM CW
	public String ecmControlWordOdd;
	public String ecmControlWordEven;

	public ObservableList<XYChart.Series<String, Number>> observableArrayList;
	
	
	public SimulatorModel(Stage primaryStage) {
		PRIMARY_STAGE  = primaryStage;
	}

	public Stage getPrimaryStage() {
		return PRIMARY_STAGE;
	}

	
}