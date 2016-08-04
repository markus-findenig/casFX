package tests.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import model.ConfigModel;
import model.SimulatorModel;

class TestFFMPEG {
	
	
	static Thread thread;
	static Thread threadOdd;
	static Thread threadEven;

	static Thread thFFmpeg;

	static SimulatorModel model;
	static ConfigModel configModel;

	static String ffmpegPath  = "D:\\Securety\\Programms\\ffmpeg\\bin\\";

	static String infile;
	static String outfileOdd;
	static String outfileEven;

	/**
	 * @true Odd File
	 * @false Even File
	 */
	static Boolean stateFileType;

	static double startTime;
	static double stopTime;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		infile = "D:\\Users\\Videos\\BINGO.mp4";
		outfileOdd = "D:\\Users\\Videos" + "\\odd.mp4";
		outfileEven = "D:\\Users\\Videos" + "\\even.mp4";

		// init Timer
		setStartTime(0);
		setStopTime(10);
		setStateFileType(true);
	
		
		Runnable myRunnable = new Runnable() {
			
			ProcessBuilder pb;
			Process p = null;
			
			@Override
			public void run() {
				
				

				if (!Thread.interrupted()) {
					
					System.out.println("test");

					if (getStateFileType()) {
						
						File cutOddFile = new File(outfileOdd);
						pb = new ProcessBuilder(ffmpegPath + "ffmpeg", "-i", infile, "-y", "-ss",
						Double.toString(getStartTime()), "-to", Double.toString(getStopTime()), "-y", "-async", "1", outfileOdd);

						
						
						try {
							cutOddFile.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						pb.redirectErrorStream(true);
						pb.redirectInput(ProcessBuilder.Redirect.PIPE); //optional,
						pb.redirectOutput(cutOddFile);
						try {
							p = pb.start();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {
							p.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						
						
						setStateFileType(false);
						

					} else {
						
						System.out.println("else");

						File cutEvenFile = new File(outfileEven);
						pb = new ProcessBuilder(ffmpegPath + "ffmpeg", "-i", infile, "-y", "-ss",
						Double.toString(getStartTime()), "-to", Double.toString(getStopTime()), "-y", "-async", "1", outfileEven);

						try {
							cutEvenFile.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						pb.redirectErrorStream(true);
						pb.redirectInput(ProcessBuilder.Redirect.PIPE); //optional,
						pb.redirectOutput(cutEvenFile);
						try {
							p = pb.start();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {
							p.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						setStateFileType(true);

					}

					// Update Start/Stop
					setStartTime(getStopTime() + 0.001);
					setStopTime(getStopTime() + 10);

				} else {
					Thread.currentThread().stop();
				}

			}
		};

		
		//myRunnable.run();
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleWithFixedDelay(myRunnable, 0, 10000, TimeUnit.MILLISECONDS);

		
		
		
//		thread = new Thread(){
//			String ffmpegPath = "D:\\Securety\\Programms\\ffmpeg\\bin\\";
//			String[] ffmpeg = new String[] {ffmpegPath + "ffmpeg", "-i", "D:\\Users\\Videos\\BINGO.mp4", "-ss", "00:00:20.1",
//					"-to", "00:00:30", "-y", "-async", "1", "D:\\Users\\Videos\\BINGO_20-30.mp4"};
//			Runtime rt = Runtime.getRuntime();
//			Process p = rt.exec(ffmpeg);

		// if you want to wait for the process to finish
		//p.waitFor();
		
//		};
//		thread.start();
		
	
		
		
//		FFmpeg ffmpeg = new FFmpeg("/path/to/ffmpeg");
//		FFprobe ffprobe = new FFprobe("/path/to/ffprobe");
//
//		FFmpegBuilder builder = new FFmpegBuilder()
//		    .setInput(in)
//		    .overrideOutputFiles(true)
//		    .addOutput("output.mp4")
//		        .setFormat("mp4")
//		        .setTargetSize(250000)
//
//		        .disableSubtitle()
//
//		        .setAudioChannels(1)
//		        .setAudioCodec("libfdk_aac")
//		        .setAudioRate(48000)
//		        .setAudioBitrate(32768)
//
//		        .setVideoCodec("libx264")
//		        .setVideoFramerate(Fraction.getFraction(24, 1))
//		        .setVideoResolution(640, 480)
//
//		        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
//		        .done();
//
//		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
//		executor.createTwoPassJob(builder).run();
		


	}
	
	public static void generateOddFile(double start, double stop) throws IOException, InterruptedException {

		File cutOddFile = new File(outfileOdd);
		ProcessBuilder pbOdd = new ProcessBuilder(ffmpegPath + "ffmpeg", "-i", infile, "-y", "-ss",
				Double.toString(start), "-to", Double.toString(stop), "-y", "-async", "1", outfileOdd);

		cutOddFile.createNewFile();
		pbOdd.redirectErrorStream(false);
		pbOdd.redirectInput(new File(infile)); //optional,
//		// default behavior
		pbOdd.redirectOutput(cutOddFile);
		Process pOdd = pbOdd.start();
		
		pOdd.waitFor();
		
		pOdd.destroy();
	}
	
	public static void generateEvenFile(double start, double stop) throws IOException, InterruptedException {

		File cutEvenFile = new File(outfileOdd);
		ProcessBuilder pbEven = new ProcessBuilder(ffmpegPath + "ffmpeg", "-i", infile, "-y", "-ss",
				Double.toString(start), "-to", Double.toString(stop), "-y", "-async", "1", outfileEven);

		// cutEvenFile.createNewFile();
		pbEven.redirectErrorStream(false);
//		pbEven.redirectInput(); //optional,
//		// default behavior
//		pbEven.redirectOutput(cutEvenFile);
		Process pEven = pbEven.start();

		// if you want to wait for the process to finish
		pEven.waitFor();
		pEven.destroy();
		
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