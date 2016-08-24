package controller;

import java.io.IOException;

import model.ConfigModel;
import model.SimulatorModel;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

/**
 * FFmpeg Controller. Controls the parameters and configurations of FFmpeg
 * program.
 */
public class FFmpegController {

	/**
	 * Simulator Model
	 */
	private static SimulatorModel model;

	/**
	 * Config Model
	 */
	private static ConfigModel configModel;

	/**
	 * File path to FFmpeg library program.
	 */
	private static String ffmpegPath;

	/**
	 * File path of the current input video file.
	 */
	private static String infile;

	/**
	 * File path to the output file odd.mp4
	 */
	private static String fileOdd;

	/**
	 * File path to the output file even.mp4
	 */
	private static String fileEven;

	/**
	 * Process Builder for running FFmpeg.
	 */
	private static ProcessBuilder pb;

	/**
	 * Starting time from which the video input is cut.
	 */
	private static double startTime;

	/**
	 * Length of the input file in seconds.
	 */
	private static double maxTime;

	/**
	 * Initializes the parameters for FFmpeg. Sets odd.mp4 and even.mp4 the
	 * paths to output files and stores them in the input video file path.
	 */
	public static void initFFmpegController() {
		configModel = ConfigViewController.getConfigModel();
		model = SimulatorViewController.getModel();

		ffmpegPath = configModel.getFFmpegPath();

		infile = model.getInputFile().toString();
		fileOdd = model.getInputFile().getParent() + "\\odd.mp4";
		fileEven = model.getInputFile().getParent() + "\\even.mp4";

		// init Timer TODO
		// setStartTime(5);
		setStartTime(300);

		// get max Time
		MediaPlayerFactory factory = new MediaPlayerFactory();
		HeadlessMediaPlayer mp = factory.newHeadlessMediaPlayer();
		mp.prepareMedia(infile);

		// Parse die Media Datei
		mp.parseMedia();
		// setzt die maximale Länge der Input Video Datei
		setMaxTime(mp.getMediaMeta().getLength() / 1000);

	}

	/**
	 * Run FFmpeg. Cuts the input video file based on ECM message type in
	 * odd.mp4 or even.mp4 and stores them in the input video path.
	 */
	public static void runFFmpeg() {

		// überprüft ob die Input Video Datei zu ende ist
		if (getStartTime() > getMaxTime()) {
			EncryptionController.stopEncryption();
			return;
		}

		// --------------------------------------------------------
		// File Odd
		if (EncryptionController.isStateECMType()) {
			pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", "-y", "-ss", Double.toString(getStartTime()), "-i", infile,
					"-vcodec", "copy", "-acodec", "copy", "-t", Double.toString(model.getCwTime() - 0.9),
					"-avoid_negative_ts", "1", fileOdd);
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// --------------------------------------------------------
			// File Even
		} else {
			pb = new ProcessBuilder(ffmpegPath + "\\ffmpeg", "-y", "-ss", Double.toString(getStartTime()), "-i", infile,
					"-vcodec", "copy", "-acodec", "copy", "-t", Double.toString(model.getCwTime() - 0.9),
					"-avoid_negative_ts", "1", fileEven);
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} // end if else

		// Update nächste Start Zeit
		setStartTime(getStartTime() + model.getCwTime());

	}

	/**
	 * Gets the current start time.
	 * 
	 * @return The current start time.
	 */
	public static double getStartTime() {
		return startTime;
	}

	/**
	 * Set the next start time.
	 * 
	 * @param sTime
	 *            Time to set.
	 */
	public static void setStartTime(double sTime) {
		startTime = sTime;
	}

	/**
	 * Gets the maximum duration in seconds.
	 * 
	 * @return Maximum duration in seconds.
	 */
	public static double getMaxTime() {
		return maxTime;
	}

	/**
	 * Set the maximum duration in seconds.
	 * 
	 * @param mTime
	 *            The maximum duration in seconds.
	 */
	public static void setMaxTime(double mTime) {
		maxTime = mTime;
	}

}
