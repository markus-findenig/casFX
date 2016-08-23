package controller;

import java.io.IOException;

import model.ConfigModel;
import model.SimulatorModel;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

/**
 * FFmpeg Controller. Steuert die Parameter und Ausführungen vom FFmpeg Programm.
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
	 * Dateipfad zur FFmpeg Programm Bibliothek
	 */
	private static String ffmpegPath;

	/**
	 * Dateipfad der aktuellen Input Video Datei
	 */
	private static String infile;

	/**
	 * Dateipfad zur Ausgabedatei odd.mp4
	 */
	private static String fileOdd;
	
	/**
	 * Dateipfad zur Ausgabedatei even.mp4
	 */
	private static String fileEven;

	/**
	 * Process Builder für die Ausführung von FFmpeg
	 */
	private static ProcessBuilder pb;
	
	/**
	 * Start Zeit von wo das Input Video geschnitten wird.
	 */
	private static double startTime;

	/**
	 * Länge der Input Datei in Sekunden
	 */
	private static double maxTime;

	/**
	 * Initialisiert die Parameter für FFmpeg. Setzt die Pfade zu den Ausgabe
	 * Dateien odd.mp4 und even.mp4 und speichert diese im Input Video Dateipfad
	 * ab.
	 */
	public static void initFFmpegController() {
		configModel = ConfigViewController.getConfigModel();
		model = SimulatorViewController.getModel();

		ffmpegPath = configModel.getFFmpegPath();

		infile = model.getInputFile().toString();
		fileOdd = model.getInputFile().getParent() + "\\odd.mp4";
		fileEven = model.getInputFile().getParent() + "\\even.mp4";

		// init Timer TODO
		//setStartTime(5);
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
	 * Startet FFmpeg. Schneidet die Input Video Datei anhand vom ECM
	 * Nachrichtentyp in odd.mp4 oder in even.mp4 und speichert diese im Input
	 * Video Pfad.
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
	 * Liefert die aktuelle Startzeit.
	 * 
	 * @return - Start Zeit
	 */
	public static double getStartTime() {
		return startTime;
	}

	/**
	 * Setzt die nächste Startzeit, von wo die Datei geschnitten wird.
	 * 
	 * @param sTime
	 *            Start Zeit
	 */
	public static void setStartTime(double sTime) {
		startTime = sTime;
	}

	/**
	 * Liefert die maximale Laufzeit der Input Video Datei in Sekunden.
	 * 
	 * @return Maximale Laufzeit in Sekunden.
	 */
	public static double getMaxTime() {
		return maxTime;
	}

	/**
	 * Setzt die Laufzeit von der Input Video Datei.
	 * 
	 * @param mTime
	 *            Maximale Laufzeit in Sekunden.
	 */
	public static void setMaxTime(double mTime) {
		maxTime = mTime;
	}
	
}
