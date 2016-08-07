package controller;

import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import model.ConfigModel;
import model.DecryptionECM;
import model.EncryptionECM;
import model.SimulatorModel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;
import view.InputPlayerView;
import view.OutputPlayerView;
import view.SimulatorView;

/**
 * Player View Controller für Input und Output Player
 */
public class PlayerViewController {

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

	private static EncryptionECM encryptionECM;

	public static DecryptionECM decryptionECM;

	// Player View
	// private static PlayerView inputPlayerView;
	// private static PlayerView outputPlayerView;

	// Player Threads
	public static Thread thInitPlayerInput;
	public static Thread thInitPlayerOutput;

	public static EmbeddedMediaPlayer embeddedOutputMediaPlayer;

	public static MediaPlayerFactory mediaOutputPlayerFactory;

	public static PlayerControlsPanel controlsOutputPanel;

	public static EmbeddedMediaPlayerComponent mediaOutputPlayerComponent;

	// private static ScheduledExecutorService playerOutputExecutor;
	private static Thread thInitOutputPlayer;

	// public PlayerViewController(SimulatorModel sModel, ConfigModel cModel) {
	// model = sModel;
	// configModel = cModel;
	// runPlayerInput();
	// runPlayerOutput();
	//
	// }

	// public static void showInputPlayer() {
	// inputPlayerView.showInputPlayer();
	//
	// }
	//
	// public static void showOutputPlayer() {
	// outputPlayerView.showOutputPlayer();
	//
	// }

	public static void runPlayerInput() {

		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();

		// Constant CW
		if (model.getCwTime() == 0) {
			InputPlayerView.videoInputF.setVisible(true);

		} else {

			// Video Player Input Initialisieren
			Task<Void> taskInitPlayerInput = new Task<Void>() {
				@Override
				protected Void call() throws Exception {

					MediaPlayerFactory mediaInputPlayerFactory = new MediaPlayerFactory();

					EmbeddedMediaPlayer embeddedInputMediaPlayer = mediaInputPlayerFactory.newEmbeddedMediaPlayer();
					PlayerControlsPanel controlsInputPanel = new PlayerControlsPanel(embeddedInputMediaPlayer);
					EmbeddedMediaPlayerComponent mediaInputPlayerComponent = new EmbeddedMediaPlayerComponent();
					mediaInputPlayerComponent.add(controlsInputPanel);

					MediaListPlayer mediaListPlayer = mediaInputPlayerFactory.newMediaListPlayer();
					MediaList mediaList = mediaInputPlayerFactory.newMediaList();

					mediaListPlayer.setMediaPlayer(embeddedInputMediaPlayer);

					// Set Player Volume 0
					controlsInputPanel.updateVolume(0);

					new InputPlayerView(embeddedInputMediaPlayer, mediaInputPlayerFactory, mediaInputPlayerComponent,
							controlsInputPanel);

					mediaList.addMedia(model.getInputFile().getParent() + "\\odd.mp4");
					mediaList.addMedia(model.getInputFile().getParent() + "\\even.mp4");

					mediaListPlayer.setMediaList(mediaList);
					mediaListPlayer.setMode(MediaListPlayerMode.LOOP);

					mediaListPlayer.play();

					return null;
				}
			};
			// start the task
			thInitPlayerInput = new Thread(taskInitPlayerInput);
			thInitPlayerInput.setDaemon(true);
			thInitPlayerInput.start();

		}

	}

	public static void getPlayerOutput() {

		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();
		view = SimulatorViewController.getView();

		// Runnable receiveMessage = new Runnable() {
		// @Override
		// public void run() {
		// // init received Messages
		// try {
		// DecryptionController.receiveMessage();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// };
		//
		// thReceiveMessage = new Thread(receiveMessage);
		// thReceiveMessage.setDaemon(true);
		// thReceiveMessage.start();

		Runnable initPlayer = new Runnable() {
			@Override
			public void run() {
				try {
					decryptionECM = DecryptionController.getDecryptionECM();

					// Constant CW Protocol Type = BB
					if (decryptionECM.getEcmProtocol().equals("BB")) {
						initConstantPlayerOutput();
					}
					// Intervall CW Protocol Type = AA
					else {
						initIntervallPlayerOutput();
					} // end if else

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thInitOutputPlayer = new Thread(initPlayer);
		thInitOutputPlayer.setDaemon(true);
		thInitOutputPlayer.start();

		// playerOutputExecutor = Executors.newScheduledThreadPool(1);
		// playerOutputExecutor.schedule(initPlayer, 5, TimeUnit.SECONDS);

	}

	private static void initIntervallPlayerOutput() {
		System.err.println("decryptionECM.getEcmCwOdd() : " + decryptionECM.getEcmCwOdd());

		// // Video Player Output Initialisieren
		// Task<Void> taskInitPlayerOutput = new Task<Void>() {
		// @Override
		// protected Void call() throws Exception {

		List<String> vlcArgs = new ArrayList<String>();

		// if cw is odd = 8000000000000000
		if (encryptionECM.getEcmHeader().equals("8000000000000000")) {
			view.getCwOutTF().setText(encryptionECM.getEcmCwOdd());
			vlcArgs.add("--ts-csa-ck=" + encryptionECM.getEcmCwOdd());
		}
		// if cw is even = 8100000000000000
		else {
			view.getCwOutTF().setText(encryptionECM.getEcmCwEven());
			vlcArgs.add("--ts-csa2-ck=" + encryptionECM.getEcmCwEven());
		}

		// // update decrypted ECM
		// String ecm = encryptionECM.getEcmHeader() +
		// encryptionECM.getEcmProtocol() +
		// encryptionECM.getEcmBroadcastId() +
		// encryptionECM.getEcmWorkKeyId() +
		// encryptionECM.getEcmCwOdd() +
		// encryptionECM.getEcmCwEven() +
		// encryptionECM.getEcmProgramType() +
		// encryptionECM.getEcmDateTime() +
		// encryptionECM.getEcmRecordControl() +
		// encryptionECM.getEcmVariablePart() +
		// encryptionECM.getEcmMAC() +
		// encryptionECM.getEcmCRC();
		//
		// view.getEcmDecryptedTA().setText(ecm);

		String[] standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);

		runConstantPlayerOutput(configModel.getClient().toString(), standardVlcOptions);

		// Platform.runLater(new Runnable() {
		// @Override
		// public void run() {
		// embeddedMediaPlayer.playMedia(ConfigModel.getClient());
		// }
		// });
		// return null;
		// }
		// };
		// // start the task
		// thInitPlayerOutput = new Thread(taskInitPlayerOutput);
		// thInitPlayerOutput.setDaemon(true);
		// thInitPlayerOutput.start();

	}

	private static void runConstantPlayerOutput(String stream, String[] standardVlcOptions) {

		mediaOutputPlayerFactory = new MediaPlayerFactory(standardVlcOptions);
		embeddedOutputMediaPlayer = mediaOutputPlayerFactory.newEmbeddedMediaPlayer();
		// embeddedMediaPlayer.setVolume(0);

		controlsOutputPanel = new PlayerControlsPanel(embeddedOutputMediaPlayer);
		mediaOutputPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaOutputPlayerComponent.add(controlsOutputPanel);

		// Set Player Volume 0
		controlsOutputPanel.updateVolume(0);

		embeddedOutputMediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				System.out.println("Constant playing: " + stream);
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				System.out.println("Constant finished: " + stream);
				// mediaPlayer.stop();
				mediaPlayer.release();
				mediaOutputPlayerFactory.release();
				// streamVLCmediaPlayer();
				// Thread.currentThread().notify();
				// generatePlayer();

			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
				System.out.println("Constant error: ");

				System.exit(0);

			}

		});

		outputPlayerView = new OutputPlayerView(embeddedOutputMediaPlayer, mediaOutputPlayerFactory,
				mediaOutputPlayerComponent, controlsOutputPanel);

		embeddedOutputMediaPlayer.playMedia(stream);

		// try {
		// Thread.currentThread().wait();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

	}

	/**
	 * Media Player Output mit Constant CW
	 */
	private static void initConstantPlayerOutput() {
		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--ts-csa-ck=" + decryptionECM.getEcmCwOdd());

		// Update GUI Output CW
		view.getCwOutTF().setText(decryptionECM.getEcmCwOdd());

		// generate media player
		mediaOutputPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		embeddedOutputMediaPlayer = mediaOutputPlayerFactory.newEmbeddedMediaPlayer();
		controlsOutputPanel = new PlayerControlsPanel(embeddedOutputMediaPlayer);
		mediaOutputPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaOutputPlayerComponent.add(controlsOutputPanel);
		// Set Player Volume 0
		controlsOutputPanel.updateVolume(0);

		outputPlayerView = new OutputPlayerView(embeddedOutputMediaPlayer, mediaOutputPlayerFactory,
				mediaOutputPlayerComponent, controlsOutputPanel);
		embeddedOutputMediaPlayer.playMedia(configModel.getClient().toString());

	}

	public static void exitOutputPlayerView() {

		thInitOutputPlayer.stop();

	}

	// public static void reInitPlayerOutput() {
	//
	// embeddedMediaPlayer.stop();
	// embeddedMediaPlayer.release();
	// mediaPlayerFactory.release();
	//
	// List<String> vlcArgs = new ArrayList<String>();
	// vlcArgs.add("--ts-csa-ck=" + encryptionECM.getEcmCwOdd());
	// vlcArgs.add("--ts-csa2-ck=" + encryptionECM.getEcmCwEven());
	//
	// mediaPlayerFactory = new MediaPlayerFactory(
	// vlcArgs.toArray(new String[vlcArgs.size()]));
	// embeddedMediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
	// PlayerControlsPanel controlsPanel = new
	// PlayerControlsPanel(embeddedMediaPlayer);
	// EmbeddedMediaPlayerComponent mediaPlayerComponent = new
	// EmbeddedMediaPlayerComponent();
	// mediaPlayerComponent.add(controlsPanel);
	//
	// outputPlayerView.reInitOutputPlayerView(embeddedMediaPlayer,
	// mediaPlayerFactory, mediaPlayerComponent, controlsPanel);
	// }

}
