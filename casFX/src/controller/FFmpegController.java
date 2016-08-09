package controller;

import java.io.IOException;
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
		if (EncryptionController.isStateECMType()) {
			pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", 
					"-y", 
					"-ss", Double.toString(getStartTime() - 0.01), 
					"-i", infile, 
					"-vcodec", "copy", 
					"-acodec", "copy", 
					"-t", Double.toString(model.getCwTime()), 
					"-avoid_negative_ts", "1",
					outFileOdd);
			
			// "-t", Double.toString(model.getCwTime()), 
			// "-c", "copy",
			// "-async", "1",
			// "-vcodec", "copy", 
			// "-acodec", "copy", 
			// "-vframes", Double.toString(25 * model.getCwTime()),
			// "-avoid_negative_ts", "1",
			
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// --------------------------------------------------------
			// File Even
		} else {
			pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", 
					"-y", 
					"-ss", Double.toString(getStartTime()), 
					"-i", infile, 
					"-vcodec", "copy", 
					"-acodec", "copy", 
					"-t", Double.toString(model.getCwTime() - 0.01), 
					"-avoid_negative_ts", "1",
					outFileEven);
			
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// switch file type
			//setStateFileType(true);

		}

		// Update Start Time
		setStartTime(getStartTime() + model.getCwTime());
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
