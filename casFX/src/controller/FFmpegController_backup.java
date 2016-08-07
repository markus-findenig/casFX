package controller;

import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import model.ConfigModel;
import model.SimulatorModel;

public class FFmpegController_backup {

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
	
	public static void runFFmpegProcessBuilder() {
		
		// TODO
		// 1. init odd file
		// 2. switch
		// 3. even file
		// 4. switch
		// 5. odd file
		// 6. goto 2

		

		File cutOddFile;
		File cutEvenFile;

		
		// --------------------------------------------------------
		// File Odd
		if (getStateFileType()) {

			cutOddFile = new File(outFileOdd);
			pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", 
					"-y", 
					"-ss", Double.toString(getStartTime()), 
					"-i", infile, 
					"-t", Double.toString(model.getCwTime()), 
					"-vcodec", "copy", 
					"-acodec", "copy", 
					"-async", "1",
					"-c", "copy",
					"-avoid_negative_ts", "1",
					outFileOdd);
			
	
			try {
				cutOddFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			pb.redirectErrorStream(true);
			pb.redirectInput(ProcessBuilder.Redirect.PIPE);
			pb.redirectOutput(cutOddFile);
			try {
				pFFmpeg = pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// switch file type
			setStateFileType(false);
			
			try {
				pFFmpeg.waitFor();
				pFFmpeg.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			// --------------------------------------------------------
			// File Even
		} else {
			cutEvenFile = new File(outFileEven);
			pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", 
					"-y", 
					"-ss", Double.toString(getStartTime()), 
					"-i", infile, 
					"-t", Double.toString(model.getCwTime()), 
					"-vcodec", "copy", 
					"-acodec", "copy", 
					"-async", "1",
					"-c", "copy",
					"-avoid_negative_ts", "1",
					outFileEven);
			
			try {
				cutEvenFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			pb.redirectErrorStream(true);
			pb.redirectInput(ProcessBuilder.Redirect.PIPE);
			pb.redirectOutput(cutEvenFile);
			try {
				pFFmpeg = pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// switch file type
			setStateFileType(true);
			
			try {
				pFFmpeg.waitFor();
				pFFmpeg.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// Update Start/Stop
		setStartTime(getStartTime() + model.getCwTime());
		//setStopTime(getStopTime() + model.getCwTime());
		

		// TODO del
//		// Scrambling Control switch
//		if (model.getScramblingControl() == "10") {
//			model.setScramblingControl("11");
//		} else {
//			model.setScramblingControl("10");
//		}

	}

	public static Runnable runFFmpegTask() {
		
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
					@Override
					public void run() {

						ProcessBuilder pb;
						File cutOddFile;
						File cutEvenFile;

						if (model.getEncryptionState()) {

							// --------------------------------------------------------
							// File Odd
							if (getStateFileType()) {

								cutOddFile = new File(outFileOdd);
								pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", 
										"-y", 
										"-ss", Double.toString(getStartTime()), 
										"-i", infile, 
										"-t", Double.toString(model.getCwTime()), 
										"-vcodec", "copy", 
										"-acodec", "copy", 
										"-async", "1",
										"-c", "copy",
										"-avoid_negative_ts", "1",
										outFileOdd);

								try {
									cutOddFile.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}

								pb.redirectErrorStream(true);
								pb.redirectInput(ProcessBuilder.Redirect.PIPE); // optional,
								pb.redirectOutput(cutOddFile);
								try {
									pb.start();
								} catch (IOException e) {
									e.printStackTrace();
								}

								// try {
								// p.waitFor();
								// } catch (InterruptedException e) {
								// e.printStackTrace();
								// }

								// switch file type
								setStateFileType(false);

								// --------------------------------------------------------
								// File Even
							} else {
								cutEvenFile = new File(outFileEven);
								pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", 
										"-y", 
										"-ss", Double.toString(getStartTime()), 
										"-i", infile, 
										"-t", Double.toString(model.getCwTime()), 
										"-vcodec", "copy", 
										"-acodec", "copy", 
										"-async", "1",
										"-c", "copy",
										"-avoid_negative_ts", "1",
										outFileEven);

								try {
									cutEvenFile.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}

								pb.redirectErrorStream(true);
								pb.redirectInput(ProcessBuilder.Redirect.PIPE); // optional,
								pb.redirectOutput(cutEvenFile);
								try {
									pb.start();
								} catch (IOException e) {
									e.printStackTrace();
								}

								// try {
								// p.waitFor();
								// } catch (InterruptedException e) {
								// e.printStackTrace();
								// }

								// switch file type
								setStateFileType(true);

							}

							
							
							// Update Start Time
							setStartTime(getStartTime() + model.getCwTime());
							
							// Update Start/Stop
//							setStartTime(getStopTime() + 0.001);
//							setStopTime(getStopTime() + model.getCwTime());

						} else {

							// del cut files
							// cutEvenFile.delete();
							// cutOddFile.delete();
							cancel();
							// Thread.currentThread().stop();
						}
					}
				});
								
	
				// } // end while
				return null;
			} // end call
		};

//		// start the task
//		thFFmpeg = new Thread(taskFFmpeg);
//		thFFmpeg.setDaemon(true);
//		thFFmpeg.start();
		return taskFFmpeg;

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
