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
 * Controller for the Output Media Player View.
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

	/**
	 * Output Player View.
	 */
	public static OutputPlayerView outputPlayerView;

	/**
	 * Decryption ECM
	 */
	private static DecryptionECM decryptionECM;

	/**
	 * VLC parameters.
	 */
	private static List<String> vlcArgs;

	/**
	 * Output File Path.
	 */
	private static String filePath;

	/**
	 * Embedded Output Media Player. Stream the current odd_stream.ts or
	 * even_stream.ts file.
	 */
	public static EmbeddedMediaPlayer embeddedOutputMediaPlayer;

	/**
	 * Media Player Factory for the {@link embeddedOutputMediaPlayer}.
	 */
	public static MediaPlayerFactory mediaOutputPlayerFactory;

	/**
	 * Player Controls Panel for the {@link embeddedOutputMediaPlayer}.
	 */
	public static PlayerControlsPanel controlsOutputPanel;

	/**
	 * Embedded Media Player Component for the {@link embeddedOutputMediaPlayer}
	 * .
	 */
	public static EmbeddedMediaPlayerComponent mediaOutputPlayerComponent;

	/**
	 * Current Volume from Embedded Media Player.
	 */
	private static int currentVolume;

	/**
	 * Thread for the current Output Media Player.
	 */
	private static Thread thInitOutputPlayer;

	/**
	 * Initializes the Output Media Player.
	 */
	public static void initOutputPlayer() {
		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();
		view = SimulatorViewController.getView();
	}

	/**
	 * Generate an Output Media Player. For constant Control Word (CW) generated
	 * a constant Media Player and interval Control Words an interval Media
	 * Player.
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
	 * Stop the current Output Media Player.
	 */
	@SuppressWarnings("deprecation")
	public static void stopOutputPlayer() {
		// unlock Keys
		view.getAk0OutTF().setEditable(true);
		view.getAk1OutTF().setEditable(true);
		view.getMpkOutTA().setEditable(true);
		if (embeddedOutputMediaPlayer != null) {
			// media player stop
			embeddedOutputMediaPlayer.stop();
			embeddedOutputMediaPlayer.release();
			mediaOutputPlayerFactory.release();
			// thread stop
			thInitOutputPlayer.stop();
		}
	}

	/**
	 * Instantiates the interval Output Media Player.
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
		outputPlayerView = new OutputPlayerView(SimulatorViewController.getPlayers().get(0).mediaPlayer(),
				mediaOutputPlayerFactory, mediaOutputPlayerComponent, controlsOutputPanel);

		// VLC Argumente
		vlcArgs = new ArrayList<String>();

		// aktuelle Input File Dateipfad
		filePath = model.getInputFile().getParent();

		// startet den Player
		streamOutputPlayer();

	}

	/**
	 * Generate the interval Output Media Player. If the current ECM Message of
	 * type EVEN starts the media player with ODD. Is the Current ECM message
	 * type ODD is the Media Player with EVEN Started.
	 */
	public static void streamOutputPlayer() {
		// lösche alte VLC Argumente
		vlcArgs.clear();

		// if aktuelle msg ist even = 8100000000000000, folgt odd ist fertig,
		// starte odd file
		if (decryptionECM.getEcmHeader().equals("8000000000000000")) {
			// update GUI
			view.getCwOutTF().setText("odd:" + decryptionECM.getEcmCwOdd());
			// set VLC Parameter
			vlcArgs.add("--ts-csa-ck=" + decryptionECM.getEcmCwOdd());
			
			runIntervallOutputPlayer(filePath + "\\odd_stream.ts", vlcArgs.toArray(new String[vlcArgs.size()]));

		}
		// else msg is odd = 8000000000000000
		else {
			// update GUI
			view.getCwOutTF().setText("even:" + decryptionECM.getEcmCwEven());
			// set VLC Parameter
			vlcArgs.add("--ts-csa-ck=" + decryptionECM.getEcmCwEven());
			
			runIntervallOutputPlayer(filePath + "\\even_stream.ts", vlcArgs.toArray(new String[vlcArgs.size()]));

		} // end if else

	}

	/**
	 * Run the Intervall Output Media Player.
	 * 
	 * @param stream
	 *            File for streaming.
	 * @param standardVlcOptions
	 *            VLC parameters.
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
		outputPlayerView.reInitOutputPlayerView(SimulatorViewController.getPlayers().get(1).mediaPlayer(),
				mediaOutputPlayerFactory, mediaOutputPlayerComponent, controlsOutputPanel);

		// play output Player
		SimulatorViewController.getPlayers().get(1).mediaPlayer().play();

	}

	/**
	 * Media Player Output with constant Control Word (CW).
	 */
	private static void initConstantOutputPlayer() {
		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--ts-csa-ck=" + decryptionECM.getEcmCwOdd());

		// Update GUI Output CW
		view.getCwOutTF().setText("odd:" + decryptionECM.getEcmCwOdd());

		// stop Decryption Receive Message
		model.setDecryptionState(false);

		// lock Keys
		view.getAk0OutTF().setEditable(false);
		view.getAk1OutTF().setEditable(false);
		view.getMpkOutTA().setEditable(false);

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
	 * @return the current Volume
	 */
	public static int getCurrentVolume() {
		return currentVolume;
	}

	/**
	 * @param cVolume
	 *            the current Volume to set
	 */
	public static void setCurrentVolume(int cVolume) {
		currentVolume = cVolume;
	}

}
