package controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.concurrent.Task;
import model.ConfigModel;
import model.SimulatorModel;

public class FFmpegController {

	static SimulatorModel model;
	static ConfigModel configModel;

	static String ffmpegPath; // = "D:\\Securety\\Programms\\ffmpeg\\bin\\";

	static String infile;
	static String outFileOdd;
	static String outFileEven;

	static Thread thFFmpeg;

	/**
	 * @true Odd File
	 * @false Even File
	 */
	static Boolean stateFileType;

	static double startTime;
	static double stopTime;

	public static void initFFmpegController() {
		configModel = ConfigViewController.getConfigModel();
		model = SimulatorViewController.getModel();

		ffmpegPath = configModel.getFfmpegPath();

		infile = model.getInputFile().toString();
		outFileOdd = model.getInputFile().getParent() + "\\odd.mp4";
		outFileEven = model.getInputFile().getParent() + "\\even.mp4";

		// init Timer
		setStartTime(0);
		setStopTime(model.getCwTime());
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

		Task<Void> taskFFmpeg = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// GUI updaten
				Platform.runLater(new Runnable() {
					public void run() {

						ProcessBuilder pb;
						Process p = null;

						File cutOddFile;
						File cutEvenFile;

						if (model.getEncryptionState()) {

							// --------------------------------------------------------
							// File Odd
							if (getStateFileType()) {

								cutOddFile = new File(outFileOdd);
								pb = new ProcessBuilder(ffmpegPath + "ffmpeg", "-i", infile, "-y", "-ss",
										Double.toString(getStartTime()), "-to", Double.toString(getStopTime()), "-y",
										"-async", "1", outFileOdd);

								try {
									cutOddFile.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}

								pb.redirectErrorStream(true);
								pb.redirectInput(ProcessBuilder.Redirect.PIPE); // optional,
								pb.redirectOutput(cutOddFile);
								try {
									p = pb.start();
								} catch (IOException e) {
									e.printStackTrace();
								}

								try {
									p.waitFor();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								// switch file type
								setStateFileType(false);

								// --------------------------------------------------------
								// File Even
							} else {
								cutEvenFile = new File(outFileEven);
								pb = new ProcessBuilder(ffmpegPath + "ffmpeg", "-i", infile, "-y", "-ss",
										Double.toString(getStartTime()), "-to", Double.toString(getStopTime()), "-y",
										"-async", "1", outFileEven);

								try {
									cutEvenFile.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}

								pb.redirectErrorStream(true);
								pb.redirectInput(ProcessBuilder.Redirect.PIPE); // optional,
								pb.redirectOutput(cutEvenFile);
								try {
									p = pb.start();
								} catch (IOException e) {
									e.printStackTrace();
								}

								try {
									p.waitFor();
									// VlcServerController.streamVlcFile(outFileEven);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								// switch file type
								setStateFileType(true);

							}

							// Update Start/Stop
							setStartTime(getStopTime() + 0.01);
							setStopTime(getStopTime() + model.getCwTime());

						} else {

							// del cut files
							// cutEvenFile.delete();
							// cutOddFile.delete();
							cancel();
							// Thread.currentThread().stop();
						}
					}
				});

				// Scrambling Control switch
				if (model.getScramblingControl() == "10") {
					model.setScramblingControl("11");
				} else {
					model.setScramblingControl("10");
				}

				// } // end while
				return null;
			} // end call
		};

		// start the task
		thFFmpeg = new Thread(taskFFmpeg);
		thFFmpeg.setDaemon(true);
		thFFmpeg.start();

		// Runnable ffmpegRunnable = new Runnable() {
		//
		// ProcessBuilder pb;
		// Process p = null;
		//
		// File cutOddFile;
		// File cutEvenFile;
		//
		// public void run() {
		//
		//
		//
		// }
		// };

		// myRunnable.run();
		// ScheduledExecutorService executor =
		// Executors.newScheduledThreadPool(1);
		// executor.scheduleWithFixedDelay(ffmpegRunnable, 0, model.getCwTime(),
		// TimeUnit.SECONDS);

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
