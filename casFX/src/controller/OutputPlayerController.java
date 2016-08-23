package controller;

import java.util.ArrayList;
import java.util.List;
import model.ConfigModel;
import model.DecryptionECM;
import model.SimulatorModel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.test.multi.PlayerInstance;
import view.OutputPlayerView;
import view.SimulatorView;

/**
 * Player View Controller für Input und Output Player
 */
public class OutputPlayerController {

	/**
	 * Config Model
	 */
	private static ConfigModel configModel;

	/**
	 * Simulator Model
	 */
	private static SimulatorModel model;

	/**
	 * Simulator View
	 */
	private static SimulatorView view;

	public static OutputPlayerView outputPlayerView;

	private static DecryptionECM decryptionECM;

	private static List<String> vlcArgs;
	
	private static String filePath;
	private static String streamFileOdd;
	private static String streamFileEven;

	public static EmbeddedMediaPlayer embeddedOutputMediaPlayer;

	public static MediaPlayerFactory mediaOutputPlayerFactory;

	public static PlayerControlsPanel controlsOutputPanel;

	public static EmbeddedMediaPlayerComponent mediaOutputPlayerComponent;
	
	private static int currentVolume;

	// private static ScheduledExecutorService playerOutputExecutor;
	private static Thread thInitOutputPlayer;

	/**
	 * Initialisiert den Output Media Player
	 */
	public static void initOutputPlayer() {
		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();
		view = SimulatorViewController.getView();
	}

	/**
	 * Erzeugt eine Output Player. Für Konstante Kontrollwort (Control Word, CW)
	 * wird ein Konstanter Media Player und für Intervall Kontrollwörter
	 * ein Intervall Media Player erzeugt.
	 */
	public static void getOutputPlayer() {
		Runnable initPlayer = new Runnable() {
			@Override
			public void run() {
				try {
					decryptionECM = DecryptionController.getDecryptionECM();

					// Constant CW Protocol Type = BB
					if (decryptionECM.getEcmProtocol().equals("BB")) {
						initConstantOutputPlayer();
					}
					// Intervall CW Protocol Type = AA
					else {
						initIntervallOutputPlayer();
					} // end if else

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		// Starte einen neuen Thread für den Media Player
		thInitOutputPlayer = new Thread(initPlayer);
		thInitOutputPlayer.setDaemon(true);
		thInitOutputPlayer.start();

	}

	/**
	 * Stoppt den aktuellen Output Media Player
	 */
	@SuppressWarnings("deprecation")
	public static void stopOutputPlayer() {

		// TODO
		if (embeddedOutputMediaPlayer != null) {
			embeddedOutputMediaPlayer.stop();
			embeddedOutputMediaPlayer.release();
			mediaOutputPlayerFactory.release();
			
			thInitOutputPlayer.stop();
		}
	}

	/**
	 * Instanziiert den Intervall Output Media Player.
	 */
	public static void initIntervallOutputPlayer() {
		// init Media Player
		mediaOutputPlayerFactory = new MediaPlayerFactory();
		embeddedOutputMediaPlayer = mediaOutputPlayerFactory.newEmbeddedMediaPlayer();

		// init Media Player Instance
		PlayerInstance playerInstance = new PlayerInstance(embeddedOutputMediaPlayer);
		SimulatorViewController.getPlayers().add(1, playerInstance);
		
		// add Media Player Control Components
		controlsOutputPanel = new PlayerControlsPanel(SimulatorViewController.getPlayers().get(1).mediaPlayer());
		mediaOutputPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaOutputPlayerComponent.add(controlsOutputPanel, 1);
		
		setCurrentVolume(0);

		// init Media Player View
		outputPlayerView = new OutputPlayerView(SimulatorViewController.getPlayers().get(0).mediaPlayer(), mediaOutputPlayerFactory,
				mediaOutputPlayerComponent, controlsOutputPanel);

		// VLC Argumente
		vlcArgs = new ArrayList<String>();
		
		// aktuelle Input File Dateipfad
		filePath = model.getInputFile().getParent();

		// output Dateien
		streamFileOdd = filePath + "\\odd_stream.ts";
		streamFileEven = filePath + "\\even_stream.ts";
		
		// startet den Player
		streamOutputPlayer();

	}
	
	/**
	 * Initialisiert den Intervall Output Media Player. Ist die aktuelle ECM Nachricht
	 * von Typ EVEN wird der Media Player mit ODD gestartet. Ist die aktuelle
	 * ECM Nachricht von Typ ODD wird der Media Player mit EVEN gestartet.
	 */
	public static void streamOutputPlayer() {
		// lösche alte VLC Argumente
		vlcArgs.clear();
		
		// if aktuelle msg ist even = 8100000000000000, folgt odd ist fertig, starte odd file
		if (decryptionECM.getEcmHeader().equals("8000000000000000")) {
			// update GUI
			view.getCwOutTF().setText("odd:" + decryptionECM.getEcmCwOdd());
			// set VLC Parameter
			vlcArgs.add("--ts-csa-ck=" + decryptionECM.getEcmCwOdd());
			vlcArgs.add("--ts-csa2-ck=0000000000000000");
		
			runIntervallOutputPlayer(streamFileOdd, vlcArgs.toArray(new String[vlcArgs.size()]));
			
		}
		// else msg is odd = 8000000000000000
		else {
			// update GUI
			view.getCwOutTF().setText("even:" + decryptionECM.getEcmCwEven());
			// set VLC Parameter
			vlcArgs.add("--ts-csa-ck=0000000000000000");
			vlcArgs.add("--ts-csa2-ck=" + decryptionECM.getEcmCwEven());
			
			runIntervallOutputPlayer(streamFileEven, vlcArgs.toArray(new String[vlcArgs.size()]));
			
		} // end if else
		
	}

	/**
	 * Startet den Intervall Output Media Player.
	 * @param stream - Datei zum Streamen.
	 * @param standardVlcOptions - VLC Parameter.
	 */
	private static void runIntervallOutputPlayer(String stream, String[] standardVlcOptions) {
		// erzeuge neuen Player
		mediaOutputPlayerFactory = new MediaPlayerFactory(standardVlcOptions);
		embeddedOutputMediaPlayer = mediaOutputPlayerFactory.newEmbeddedMediaPlayer();
		embeddedOutputMediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void playing(MediaPlayer mediaPlayer) {
				System.out.println("Intervall playing: " + stream);
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				System.out.println("Intervall finished: " + stream);
				// lösche alte Parameter
				mediaPlayer.release();
				embeddedOutputMediaPlayer.release();
				mediaOutputPlayerFactory.release();
				// schleife, initialisiere Player neu
				streamOutputPlayer();
			}
		});
		// Bereite Datei für den Player vor
		embeddedOutputMediaPlayer.prepareMedia(stream);
		
		// Entferne alten Player
		SimulatorViewController.getPlayers().remove(1);
		
		// Erzeugen eine neue Player Instance
		PlayerInstance playerInstance = new PlayerInstance(embeddedOutputMediaPlayer);
		SimulatorViewController.getPlayers().add(1, playerInstance);

		// update Control Panel
		controlsOutputPanel.reInitPlayerControlsPanel(SimulatorViewController.getPlayers().get(1).mediaPlayer());
			
		// GUI update output Player
		outputPlayerView.reInitOutputPlayerView(SimulatorViewController.getPlayers().get(1).mediaPlayer(), mediaOutputPlayerFactory,
				mediaOutputPlayerComponent, controlsOutputPanel);
		
		// play output Player
		SimulatorViewController.getPlayers().get(1).mediaPlayer().play();

	}


	/**
	 * Media Player Output mit Constant Control Word (CW).
	 */
	private static void initConstantOutputPlayer() {
		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--ts-csa-ck=" + decryptionECM.getEcmCwOdd());

		// Update GUI Output CW
		view.getCwOutTF().setText(decryptionECM.getEcmCwOdd());

		// stop Decryption Receive Message
		model.setDecryptionState(false);

		// generate media player
		mediaOutputPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		embeddedOutputMediaPlayer = mediaOutputPlayerFactory.newEmbeddedMediaPlayer();
		controlsOutputPanel = new PlayerControlsPanel(embeddedOutputMediaPlayer);
		mediaOutputPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaOutputPlayerComponent.add(controlsOutputPanel);
		// Set Player Volume 0
		controlsOutputPanel.updateVolume(0);
		embeddedOutputMediaPlayer.prepareMedia(configModel.getClient().toString());

		outputPlayerView = new OutputPlayerView(embeddedOutputMediaPlayer, mediaOutputPlayerFactory,
				mediaOutputPlayerComponent, controlsOutputPanel);

		// play Player
		embeddedOutputMediaPlayer.playMedia(configModel.getClient().toString());
	}

	/**
	 * @return the currentVolume
	 */
	public static int getCurrentVolume() {
		return currentVolume;
	}

	/**
	 * @param currentVolume the currentVolume to set
	 */
	public static void setCurrentVolume(int currentVolume) {
		OutputPlayerController.currentVolume = currentVolume;
	}


}
