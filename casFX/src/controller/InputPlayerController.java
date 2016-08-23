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
	 *  Encryption ECM Model. Speichert die aktuelle ECM Nachricht.
	 */
	private static EncryptionECM encryptionECM;

	/**
	 * Stream Media Player. Verschlüsselt die aktuelle odd.mp4 oder even.mp4 und speichert 
	 * dieses als odd_stream.ts oder even_stream.ts ab.
	 */
	public static HeadlessMediaPlayer streamMediaPlayer;
	
	/**
	 * Media Player Factory für den {@link streamMediaPlayer}
	 */
	public static MediaPlayerFactory mediaStreamPlayerFactory;
	
	/**
	 * Embedded Input Media Player. Spielt die aktuelle odd.mp4 oder even.mp4 ab.
	 */
	public static EmbeddedMediaPlayer embeddedInputMediaPlayer;

	/**
	 * Media Player Factory für den {@link embeddedInputMediaPlayer}
	 */
	public static MediaPlayerFactory mediaInputPlayerFactory;

	/**
	 * Player Controls Panel für den {@link embeddedInputMediaPlayer}
	 */
	public static PlayerControlsPanel controlsInputPanel;

	/**
	 * Embedded Media Player Component für den {@link embeddedInputMediaPlayer}
	 */
	public static EmbeddedMediaPlayerComponent mediaInputPlayerComponent;

	/**
	 * Input Player View. Anzeige für den Player.
	 */
	public static InputPlayerView inputPlayerView;

	/**
	 * Parameter für die Media Player Factory {@link mediaInputPlayerFactory}
	 */
	private static List<String> vlcArgs;

	
	private static String filePath;

	/**
	 * Dateipfad zur Eingabedatei odd.mp4
	 */
	private static String fileOdd;

	/**
	 * Dateipfad zur Eingabedatei even.mp4
	 */
	private static String fileEven;

	/**
	 * Dateipfad zur Ausgabedatei odd_stream.ts
	 */
	private static String streamFileOdd;
	
	/**
	 * Dateipfad zur Ausgabedatei even_stream.ts
	 */
	private static String streamFileEven;
	
	
	//private static List<PlayerInstance> players;
	

	/**
	 * Initialisiert den Input Media Player
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
	 * Initialisiert den Intervall Input Media Player.
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
	 * Zeigt den Input Media Player an.
	 */
	public static void getInputPlayer() {
		InputPlayerView.getVideoInputF().setVisible(true);
	}
	
	/**
	 * Startet den Input Media Player anhand des aktuellen ECM Typs.
	 */
	public static void runInputPlayer() {
		// if odd
		if (EncryptionController.isStateECMType()) {
			streamInputPlayer().run();
			startInputPlayerView(fileOdd);
			// switch ECM Type
			//EncryptionController.setStateECMType(false);
		} 
		// else even
		else {
			streamInputPlayer().run();
			startInputPlayerView(fileEven);
			// switch ECM Type
			//EncryptionController.setStateECMType(true);
		}
	}

	public static void stopInputPlayer() {

		// TODO
		if (embeddedInputMediaPlayer != null) {
			embeddedInputMediaPlayer.stop();
			embeddedInputMediaPlayer.release();
			mediaInputPlayerFactory.release();
		}

	}

	/**
	 * Erzeugt den Stream Input Media Player.
	 * 
	 * Setzt die Verschlüsselungsparameter für die ODD/EVEN Datei sowie die
	 * Verschlüsselte Ausgabedatei. Startet den Stream Input Media Player anhand
	 * des aktuellen ECM Typs mit den erzeugten Parametern.
	 * 
	 * @return taskStreamInputPlayer - Gibt den erzeugten Task für den Start
	 *         zurück.
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

					// vlcArgs.add("--sout=#duplicate{dst=file{mux=ts,dst=" +
					// streamFileOdd + "}, dst=display}");
					vlcArgs.add("--sout=#std{access=file,mux=ts,dst=" + streamFileOdd + "}");
					vlcArgs.add("--sout-ts-crypt-video");
					vlcArgs.add("--sout-ts-crypt-audio");
					vlcArgs.add("--sout-ts-csa-use=1");
					//vlcArgs.add("--sout-ts-csa-ck=0123456789ABCDEF");
					vlcArgs.add("--sout-ts-csa-ck=" + encryptionECM.getEcmCwOdd());
					vlcArgs.add("--sout-ts-csa2-ck=0000000000000000");
					vlcArgs.add("vlc://quit");

					runStreamInputPlayer(fileOdd, vlcArgs.toArray(new String[vlcArgs.size()]));

				}
				// -------------------------------------------------
				// else false = even
				else {

					// vlcArgs.add("--sout=#duplicate{dst=file{mux=ts,dst=" +
					// streamFileEven + "}, dst=display}");
					vlcArgs.add("--sout=#std{access=file,mux=ts,dst=" + streamFileEven + "}");
					vlcArgs.add("--sout-ts-crypt-video");
					vlcArgs.add("--sout-ts-crypt-audio");
					vlcArgs.add("--sout-ts-csa-use=2");
					vlcArgs.add("--sout-ts-csa-ck=0000000000000000");
					//vlcArgs.add("--sout-ts-csa2-ck=FEDABC9876543210");
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
	 * Startet den Stream Input Media Player (keine View).
	 * 
	 * @param file
	 *            - Datei zum Abspielen.
	 * @param standardVlcOptions
	 *            - Media Player Parameter
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
	 * Startet die Wiedergabe vom Input Media Player.
	 * 
	 * @param file
	 *            - Datei für die Wiedergabe.
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

//		controlsInputPanel = null;
//		mediaInputPlayerComponent = null;
//
		//controlsInputPanel = new PlayerControlsPanel(embeddedInputMediaPlayer);
//		mediaInputPlayerComponent = new EmbeddedMediaPlayerComponent();
		//mediaInputPlayerComponent.add(controlsInputPanel);
//		controlsInputPanel.updateVolume(0);
		
		//controlsInputPanel.reInitPlayerControlsPanel(embeddedInputMediaPlayer);
		
		// TODO
		//mediaInputPlayerComponent.release();
		//mediaInputPlayerComponent.remove(controlsInputPanel);
		
		
		embeddedInputMediaPlayer.prepareMedia(file);
		
		
		//controlsInputPanel.reInitPlayerControlsPanel(embeddedInputMediaPlayer);
		
		// entferne alten Player
		SimulatorViewController.getPlayers().remove(0);
//		
		// erstellen neuen Player
		PlayerInstance playerInstance = new PlayerInstance(embeddedInputMediaPlayer);
		SimulatorViewController.getPlayers().add(0, playerInstance);
		
		//mediaInputPlayerComponent.add(controlsInputPanel);

		// View the Player
//		inputPlayerView.reInitInputPlayerView(SimulatorViewController.getPlayers().get(0).mediaPlayer(), mediaInputPlayerFactory,
//				mediaInputPlayerComponent, controlsInputPanel);
		
		inputPlayerView.reInitInputPlayerView(embeddedInputMediaPlayer, mediaInputPlayerFactory);
		
		
		// Play the file
		//embeddedInputMediaPlayer.play();
		SimulatorViewController.getPlayers().get(0).mediaPlayer().play();
		//embeddedInputMediaPlayer.playMedia(file);

	}

	/**
	 * Streamt die angegebene Datei mittels RTP Protokoll.
	 * 
	 * @param outfile
	 *            - Datei zum streamen.
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
		
		//embeddedInputMediaPlayer.prepareMedia(outfile);

		// erzeugt die Steuerelemente für den media player
//		controlsInputPanel = new PlayerControlsPanel(embeddedInputMediaPlayer);
//		mediaInputPlayerComponent = new EmbeddedMediaPlayerComponent();
//		mediaInputPlayerComponent.add(controlsInputPanel);
//
//		// setze die Lautstärke auf null
//		controlsInputPanel.updateVolume(0);
//		mediaInputPlayerComponent.disable();
//		controlsInputPanel.disable();
		
		embeddedInputMediaPlayer.prepareMedia(outfile);
		
		PlayerInstance playerInstance = new PlayerInstance(embeddedInputMediaPlayer);
		SimulatorViewController.getPlayers().add(0, playerInstance);
		
		// erzeugt die GUI für den media player
//		inputPlayerView = new InputPlayerView(embeddedInputMediaPlayer, mediaInputPlayerFactory,
//				mediaInputPlayerComponent, controlsInputPanel);
		
		inputPlayerView = new InputPlayerView(embeddedInputMediaPlayer, mediaInputPlayerFactory);

		// streamt die Datei
		SimulatorViewController.getPlayers().get(0).mediaPlayer().play();
		//embeddedInputMediaPlayer.playMedia(outfile);

	}

	/**
	 * Formatiert den server String für die VLC input Parameter
	 * 
	 * @param server
	 *            Protokoll, Adresse und IP des Servers
	 * @return String für VLC Parameter
	 */
	private static String formatRtpStream(String server) {
		// default server = rtp://239.0.0.1:5004
		String[] rtpSplit = server.split("://");
		// rtp = rtpSplit[0]
		String ipPort = rtpSplit[1];
		String[] ip = ipPort.split(":");

		StringBuilder sb = new StringBuilder(200);
		// sb.append("rtp{proto=udp,mux=ts{use-key-frames},dst=");
		sb.append("rtp{proto=udp,mux=ts,dst=");
		sb.append(ip[0]);
		sb.append(",port=");
		sb.append(ip[1]);
		sb.append("}");
		return sb.toString();
	}

	public static String getInFileOdd() {
		return fileOdd;
	}

	public static String getInFileEven() {
		return fileEven;
	}

}
