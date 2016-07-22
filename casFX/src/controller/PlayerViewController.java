package controller;

import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import model.ConfigModel;
import model.SimulatorModel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import view.PlayerView;

public class PlayerViewController {

	// Config Model
	private static ConfigModel configModel;

	// Simulator Model
	private static SimulatorModel model;

	// Player View
	private static PlayerView inputPlayerView;
	private static PlayerView outputPlayerView;

	// Player Threads
	public static Thread thInitPlayerInput;
	public static Thread thInitPlayerOutput;


	public PlayerViewController(SimulatorModel sModel, ConfigModel cModel) {
		model = sModel;
		configModel = cModel;
		initPlayerInput();
		initPlayerOutput();
		// playerView = new PlayerView();

	}

	public static void showInputPlayer() {
		inputPlayerView.showInputPlayer();

	}
	
	public static void showOutputPlayer() {
		outputPlayerView.showOutputPlayer();

	}

	public static void initPlayerInput() {

		// Video Player Input Initialisieren
		Task<Void> taskInitPlayerInput = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				List<String> vlcArgs = new ArrayList<String>();
				vlcArgs.add("--ts-csa-ck=" + model.getEcmCwOdd());
				vlcArgs.add("--ts-csa2-ck=" + model.getEcmCwEven());

				MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(
						vlcArgs.toArray(new String[vlcArgs.size()]));
				EmbeddedMediaPlayer embeddedMediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
				PlayerControlsPanel controlsPanel = new PlayerControlsPanel(embeddedMediaPlayer);
				EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
				mediaPlayerComponent.add(controlsPanel);

				// Set Player Volume 0
				controlsPanel.updateVolume(0);

				inputPlayerView = new PlayerView("Input", embeddedMediaPlayer, mediaPlayerFactory, mediaPlayerComponent,
						controlsPanel);

				embeddedMediaPlayer.playMedia(configModel.getServer());

				// Platform.runLater(new Runnable() {
				// @Override
				// public void run() {
				// embeddedMediaPlayer.playMedia(ConfigModel.getClient());
				// }
				// });
				
				return null;
			}
		};
		// start the task
		thInitPlayerInput = new Thread(taskInitPlayerInput);
		thInitPlayerInput.setDaemon(true);
		thInitPlayerInput.start();

	}

	public static void initPlayerOutput() {

		// Video Player Output Initialisieren
		Task<Void> taskInitPlayerOutput = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				List<String> vlcArgs = new ArrayList<String>();
				vlcArgs.add("--ts-csa-ck=" + model.getEcmCwOdd());
				vlcArgs.add("--ts-csa2-ck=" + model.getEcmCwEven());

				MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(
						vlcArgs.toArray(new String[vlcArgs.size()]));
				EmbeddedMediaPlayer embeddedMediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
				PlayerControlsPanel controlsPanel = new PlayerControlsPanel(embeddedMediaPlayer);
				EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
				mediaPlayerComponent.add(controlsPanel);

				// Set Player Volume 0
				controlsPanel.updateVolume(0);

				outputPlayerView = new PlayerView("Output", embeddedMediaPlayer, mediaPlayerFactory,
						mediaPlayerComponent, controlsPanel);

				embeddedMediaPlayer.playMedia(configModel.getClient());

				// Platform.runLater(new Runnable() {
				// @Override
				// public void run() {
				// embeddedMediaPlayer.playMedia(ConfigModel.getClient());
				// }
				// });
				return null;
			}
		};
		// start the task
		thInitPlayerOutput = new Thread(taskInitPlayerOutput);
		thInitPlayerOutput.setDaemon(true);
		thInitPlayerOutput.start();

	}

}
