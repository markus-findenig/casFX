package model;

import java.io.File;

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
	
	
	public SimulatorModel(Stage primaryStage) {
		PRIMARY_STAGE  = primaryStage;
	}

	public Stage getPrimaryStage() {
		return PRIMARY_STAGE;
	}

	
}
