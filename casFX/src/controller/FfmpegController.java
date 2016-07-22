package controller;

import java.io.File;
import java.io.IOException;

import javafx.concurrent.Task;
import model.ConfigModel;
import model.SimulatorModel;

public class FfmpegController {

	static Thread threadOdd;
	static Thread threadEven;
	
	static SimulatorModel model;
	static ConfigModel configModel;
	
	static String ffmpegPath; // = "D:\\Securety\\Programms\\ffmpeg\\bin\\";
	
	static String infile;
	static String outfileOdd;
	static String outfileEven;
	
	
	static double startTime;
	static double stopTime;

	

	public static void runFfmpeg(){
		
		configModel = ConfigViewController.getConfigModel();
		model = InputViewController.getModel();
		
		ffmpegPath = configModel.getFfmpegPath();
		
		//System.out.println("infile: " + model.getInputFile().getParent());
		
				
		infile = model.getInputFile().toString();
		outfileOdd = model.getInputFile().getParent() + "\\odd.mp4";
		outfileEven = model.getInputFile().getParent() + "\\even.mp4";
		
		// init Timer
		startTime = 0;
		stopTime = model.getCwTime();
		
		System.out.println("runFfmpeg: " + model.getCwTime());
		
		
	
	
		

	}
	
	private static void generateOddFile() {
		Task<Integer> taskOdd = new Task<Integer>() {
			@Override
			protected final Integer call() throws Exception {
				
				File cutFile = new File(outfileOdd);
				ProcessBuilder pb = new ProcessBuilder(ffmpegPath + "ffmpeg", "-i", infile, "-y", "-ss",
						Double.toString(getStartTime()), "-to", Double.toString(getStopTime()), "-y", "-async", "1", outfileOdd);
				
				cutFile.createNewFile();
				pb.redirectErrorStream(true);
				pb.redirectInput(ProcessBuilder.Redirect.PIPE); //optional, default behavior
				pb.redirectOutput(cutFile);
				Process p = pb.start();
				
				if (p.exitValue() == 0) {
					succeeded();
				} else {
					failed();
				}
				
				// if you want to wait for the process to finish
				p.waitFor();
				
				return p.exitValue();
			}
		};
		

		threadOdd = new Thread(taskOdd);
		threadOdd.setDaemon(true);
		threadOdd.start();
	}
	
	private static void generateEvenFile() {
		Task<Integer> taskEven = new Task<Integer>() {
			@Override
			protected final Integer call() throws Exception {
				
				File cutFile = new File(outfileOdd);
				ProcessBuilder pb = new ProcessBuilder(ffmpegPath + "ffmpeg", "-i", infile, "-y", "-ss",
						Double.toString(getStartTime()), "-to", Double.toString(getStopTime()), "-y", "-async", "1", outfileOdd);
				
				cutFile.createNewFile();
				pb.redirectErrorStream(true);
				pb.redirectInput(ProcessBuilder.Redirect.PIPE); //optional, default behavior
				pb.redirectOutput(cutFile);
				Process p = pb.start();
				
				if (p.exitValue() == 0) {
					succeeded();
				} else {
					failed();
				}
				
				// if you want to wait for the process to finish
				p.waitFor();
				
				return p.exitValue();
			}
		};
		
		threadEven = new Thread(taskEven);
		threadEven.setDaemon(true);
		threadEven.start();
		
	}

	public static double getStartTime() {
		return startTime;
	}

	public static void setStartTime(double startTime) {
		FfmpegController.startTime = startTime;
	}

	public static double getStopTime() {
		return stopTime;
	}

	public static void setStopTime(double stopTime) {
		FfmpegController.stopTime = stopTime;
	}

}
