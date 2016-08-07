package controller;

import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import model.ConfigModel;
import model.SimulatorModel;

public class FFmpegController {

	private static SimulatorModel model;
	private static ConfigModel configModel;

	private static String ffmpegPath; // = "D:\\Securety\\Programms\\ffmpeg\\bin\\";

	private static String infile;
	private static String outFileOdd;
	private static String outFileEven;

	private static Thread thFFmpeg;
	
	private static ProcessBuilder pb;
	@SuppressWarnings("unused")
	private static Process pFFmpeg;

	/**
	 * @true Odd File
	 * @false Even File
	 */
	private static Boolean stateFileType;

	private static double startTime;
	private static double stopTime;

	public static void initFFmpegController() {
		configModel = ConfigViewController.getConfigModel();
		model = SimulatorViewController.getModel();

		ffmpegPath = configModel.getFfmpegPath();

		infile = model.getInputFile().toString();
		outFileOdd = model.getInputFile().getParent() + "\\odd.mp4";
		outFileEven = model.getInputFile().getParent() + "\\even.mp4";

		// init Thread
		thFFmpeg = null;
		
		// init Timer
		setStartTime(5);
		//setStopTime(model.getCwTime());
		setStateFileType(true);
		
	}

	public static void runFFmpeg() {
		
		// TODO
		// 1. init odd file
		// 2. switch
		// 3. even file
		// 4. switch
		// 5. odd file
		// 6. goto 2


		// --------------------------------------------------------
		// File Odd
		if (getStateFileType()) {
			pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", 
					"-y", 
					"-ss", Double.toString(getStartTime()), 
					"-i", infile, 
					"-t", Double.toString(model.getCwTime()), 
					"-vcodec", "copy", 
					"-acodec", "copy", 
					"-async", "1",
					"-avoid_negative_ts", "1",
					outFileOdd);
			
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// switch file type
			setStateFileType(false);

			// --------------------------------------------------------
			// File Even
		} else {
			pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", 
					"-y", 
					"-ss", Double.toString(getStartTime()), 
					"-i", infile, 
					"-t", Double.toString(model.getCwTime()), 
					"-vcodec", "copy", 
					"-acodec", "copy", 
					"-async", "1",
					"-avoid_negative_ts", "1",
					outFileEven);
			
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// switch file type
			setStateFileType(true);

		}

		// Update Start Time
		setStartTime(getStartTime() + model.getCwTime());
	}
	


	public static Boolean getStateFileType() {
		return stateFileType;
	}

	public static void setStateFileType(Boolean state) {
		stateFileType = state;
	}

	public static double getStartTime() {
		return startTime;
	}

	public static void setStartTime(double sTime) {
		startTime = sTime;
	}

	public static double getStopTime() {
		return stopTime;
	}

	public static void setStopTime(double sTime) {
		stopTime = sTime;
	}

}
