package controller;

import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import model.ConfigModel;
import model.EncryptionECM;
import model.SimulatorModel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.test.multi.PlayerInstance;
import view.InputPlayerView;

/**
 * Input Player Controller.
 */
public class InputPlayerController {

	/**
	 * Simulator Model
	 */
	private static SimulatorModel model;

	/**
	 * Config Model
	 */
	private static ConfigModel configModel;

	/**
	 * Encryption ECM Model. Saves the current ECM message.
	 */
	private static EncryptionECM encryptionECM;

	/**
	 * Stream Media Player. Encrypts the current odd.mp4 or even.mp4 and stores
	 * from as odd_stream.ts or even_stream.ts.
	 */
	public static HeadlessMediaPlayer streamMediaPlayer;

	/**
	 * Media Player Factory for the {@link streamMediaPlayer}.
	 */
	public static MediaPlayerFactory mediaStreamPlayerFactory;

	/**
	 * Embedded Input Media Player. Plays the current odd.mp4 or even.mp4 file.
	 */
	public static EmbeddedMediaPlayer embeddedInputMediaPlayer;

	/**
	 * Media Player Factory for the {@link embeddedInputMediaPlayer}.
	 */
	public static MediaPlayerFactory mediaInputPlayerFactory;

	/**
	 * Player Controls Panel for the {@link embeddedInputMediaPlayer}.
	 */
	public static PlayerControlsPanel controlsInputPanel;

	/**
	 * Embedded Media Player Component for the {@link embeddedInputMediaPlayer}.
	 */
	public static EmbeddedMediaPlayerComponent mediaInputPlayerComponent;

	/**
	 * Input Player View. View for the Input Player.
	 */
	public static InputPlayerView inputPlayerView;

	/**
	 * Parameters for the Media Player Factory {@link mediaInputPlayerFactory}.
	 */
	private static List<String> vlcArgs;

	/**
	 * Input File Path.
	 */
	private static String filePath;

	/**
	 * File path to the input file odd.mp4.
	 */
	private static String fileOdd;

	/**
	 * File path to the input file even.mp4.
	 */
	private static String fileEven;

	/**
	 * File path to the input file odd_stream.ts.
	 */
	private static String streamFileOdd;

	/**
	 * File path to the input file even_stream.ts.
	 */
	private static String streamFileEven;

	/**
	 * Initializes the Input Media Player
	 */
	public static void initInputPlayer() {
		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();

		encryptionECM = EncryptionController.getEncryptionECM();

		filePath = model.getInputFile().getParent();

		fileOdd = filePath + "\\odd.mp4";
		fileEven = filePath + "\\even.mp4";

		streamFileOdd = filePath + "\\odd_stream.ts";
		streamFileEven = filePath + "\\even_stream.ts";

		vlcArgs = new ArrayList<String>();
	}

	/**
	 * Initializes the Intervall Input Media Player.
	 */
	public static void initIntervallInputPlayer() {
		// init player
		mediaInputPlayerFactory = new MediaPlayerFactory();
		embeddedInputMediaPlayer = mediaInputPlayerFactory.newEmbeddedMediaPlayer();
		// Registriert Player
		PlayerInstance playerInstance = new PlayerInstance(embeddedInputMediaPlayer);
		SimulatorViewController.getPlayers().add(0, playerInstance);
		// erzeuge View für den Input Player
		inputPlayerView = new InputPlayerView(embeddedInputMediaPlayer, mediaInputPlayerFactory);

	}

	/**
	 * View the Input Media Player.
	 */
	public static void getInputPlayer() {
		InputPlayerView.getVideoInputF().setVisible(true);
	}

	/**
	 * Run the Input Media Player based on the current ECM type.
	 */
	public static void runInputPlayer() {
		// if odd
		if (EncryptionController.isStateECMType()) {
			streamInputPlayer().run();
			startInputPlayerView(fileOdd);
		}
		// else even
		else {
			streamInputPlayer().run();
			startInputPlayerView(fileEven);
		}
	}

	/**
	 * Stop the Input Media Player.
	 */
	public static void stopInputPlayer() {

		// TODO
		if (embeddedInputMediaPlayer != null) {
			embeddedInputMediaPlayer.stop();
			embeddedInputMediaPlayer.release();
			mediaInputPlayerFactory.release();
		}

	}

	/**
	 * Generates the Stream Input Media Player.
	 * 
	 * Sets the encryption parameters for the ODD/EVEN file and Encrypted output
	 * file. Starts the Stream Input Media Player from the current ECM type with
	 * the generated parameters.
	 * 
	 * @return The task Stream Input Player.
	 */
	public static Runnable streamInputPlayer() {
		Task<Void> taskStreamInputPlayer = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// löschte die alten VLC Argumente
				vlcArgs.clear();

				// -------------------------------------------------
				// if true = odd
				if (EncryptionController.isStateECMType()) {
					vlcArgs.add("--sout=#std{access=file,mux=ts,dst=" + streamFileOdd + "}");
					vlcArgs.add("--sout-ts-crypt-video");
					vlcArgs.add("--sout-ts-crypt-audio");
					vlcArgs.add("--sout-ts-csa-use=1");
					vlcArgs.add("--sout-ts-csa-ck=" + encryptionECM.getEcmCwOdd());
					vlcArgs.add("--sout-ts-csa2-ck=0000000000000000");
					vlcArgs.add("vlc://quit");

					runStreamInputPlayer(fileOdd, vlcArgs.toArray(new String[vlcArgs.size()]));

				}
				// -------------------------------------------------
				// else false = even
				else {
					vlcArgs.add("--sout=#std{access=file,mux=ts,dst=" + streamFileEven + "}");
					vlcArgs.add("--sout-ts-crypt-video");
					vlcArgs.add("--sout-ts-crypt-audio");
					vlcArgs.add("--sout-ts-csa-use=2");
					vlcArgs.add("--sout-ts-csa-ck=0000000000000000");
					vlcArgs.add("--sout-ts-csa2-ck=" + encryptionECM.getEcmCwEven());
					vlcArgs.add("vlc://quit");

					runStreamInputPlayer(fileEven, vlcArgs.toArray(new String[vlcArgs.size()]));

				} // end if else
				return null;

			} // end call
		};
		return taskStreamInputPlayer;
	}

	/**
	 * Run the Stream Input Media Player without view.
	 * 
	 * @param file
	 *            File to play.
	 * @param standardVlcOptions
	 *            Media Player Options.
	 */
	private static void runStreamInputPlayer(String file, String[] standardVlcOptions) {
		mediaStreamPlayerFactory = new MediaPlayerFactory(standardVlcOptions);
		streamMediaPlayer = mediaStreamPlayerFactory.newHeadlessMediaPlayer();
		streamMediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				mediaPlayer.release();
				streamMediaPlayer.release();
				mediaStreamPlayerFactory.release();
			}
		});
		streamMediaPlayer.playMedia(file);
	}

	/**
	 * Run the Input Media Player with view.
	 * 
	 * @param file
	 *            File to play.
	 */
	private static void startInputPlayerView(String file) {
		mediaInputPlayerFactory = new MediaPlayerFactory();
		embeddedInputMediaPlayer = mediaInputPlayerFactory.newEmbeddedMediaPlayer();
		// TODO
		embeddedInputMediaPlayer.setVolume(0);
		embeddedInputMediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				// switch
				if (EncryptionController.isStateECMType()) {
					EncryptionController.setStateECMType(false);
				} else {
					EncryptionController.setStateECMType(true);
				}
				// bereite nächste Datei vor
				FFmpegController.runFFmpeg();
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				// ECM für vorbereitete Datei
				EncryptionController.generateECM();
				EncryptionController.sendECM();
				// release Player
				mediaPlayer.release();
				embeddedInputMediaPlayer.release();
				mediaInputPlayerFactory.release();
				// starte vorbereitete Datei
				runInputPlayer();
			}
		});

		embeddedInputMediaPlayer.prepareMedia(file);

		// entferne alten Player
		SimulatorViewController.getPlayers().remove(0);
		// erstellen neuen Player
		PlayerInstance playerInstance = new PlayerInstance(embeddedInputMediaPlayer);
		SimulatorViewController.getPlayers().add(0, playerInstance);

		inputPlayerView.reInitInputPlayerView(embeddedInputMediaPlayer, mediaInputPlayerFactory);

		// Play the file
		// embeddedInputMediaPlayer.play();
		SimulatorViewController.getPlayers().get(0).mediaPlayer().setVolume(0);
		SimulatorViewController.getPlayers().get(0).mediaPlayer().play();

	}

	/**
	 * Streams the file specified by RDP protocol.
	 * 
	 * @param outfile
	 *            File to stream.
	 */
	public static void streamInputPlayerRTP(String outfile) {
		// rtp = "rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}"
		String rtp = formatRtpStream(configModel.getServer());

		vlcArgs.clear();
		vlcArgs.add("--sout=#duplicate{dst=" + rtp + ",dst=display}");
		vlcArgs.add("--sout-ts-crypt-video");
		vlcArgs.add("--sout-ts-crypt-audio");
		vlcArgs.add("--sout-ts-csa-use=1");
		vlcArgs.add("--sout-ts-csa-ck=" + encryptionECM.getEcmCwOdd());
		// vlcArgs.add("--sout-ts-csa2-ck=" + model.getEcmCwEven());
		vlcArgs.add("--no-repeat");
		vlcArgs.add("--no-loop");
		vlcArgs.add("--ttl=1");
		vlcArgs.add("--no-sout-rtp-sap");
		vlcArgs.add("--no-sout-standard-sap");
		vlcArgs.add("--sout-all");
		vlcArgs.add("--sout-keep");
		vlcArgs.add("--no-plugins-cache");
		vlcArgs.add("vlc://quit");

		// erzeugt eine media player
		mediaInputPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		embeddedInputMediaPlayer = mediaInputPlayerFactory.newEmbeddedMediaPlayer();
		embeddedInputMediaPlayer.setVolume(0);
		embeddedInputMediaPlayer.prepareMedia(outfile);

		PlayerInstance playerInstance = new PlayerInstance(embeddedInputMediaPlayer);
		SimulatorViewController.getPlayers().add(0, playerInstance);

		inputPlayerView = new InputPlayerView(embeddedInputMediaPlayer, mediaInputPlayerFactory);

		// streamt die Datei
		// embeddedInputMediaPlayer.playMedia(outfile);
		SimulatorViewController.getPlayers().get(0).mediaPlayer().setVolume(0);
		SimulatorViewController.getPlayers().get(0).mediaPlayer().play();

	}

	/**
	 * Formats the server string for the VLC input parameters.
	 * 
	 * @param server
	 *            Protocol, address and IP of the server.
	 * @return String for VLC parameters.
	 */
	private static String formatRtpStream(String server) {
		// default server = rtp://239.0.0.1:5004
		String[] rtpSplit = server.split("://");
		// rtp = rtpSplit[0]
		String ipPort = rtpSplit[1];
		String[] ip = ipPort.split(":");

		StringBuilder sb = new StringBuilder(200);
		sb.append("rtp{proto=udp,mux=ts,dst=");
		sb.append(ip[0]);
		sb.append(",port=");
		sb.append(ip[1]);
		sb.append("}");
		
		return sb.toString();
	}

	/**
	 * @return The File path to odd.mp4
	 */
	public static String getInFileOdd() {
		return fileOdd;
	}

	/**
	 * @return The File path to even.mp4
	 */
	public static String getInFileEven() {
		return fileEven;
	}

}
