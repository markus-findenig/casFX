package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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


	// Player Threads
	public static Thread thInitPlayerInput;
	public static Thread thInitPlayerOutput;

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
		
		//initIntervallOutputPlayer();

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
	public static void stopOutputPlayer() {

		// TODO
		if (embeddedOutputMediaPlayer != null) {
			embeddedOutputMediaPlayer.stop();
			embeddedOutputMediaPlayer.release();
			mediaOutputPlayerFactory.release();
		}

		// thInitOutputPlayer.stop();

	}

	/**
	 * Initialisiert den Intervall Output Media Player
	 */
	public static void initIntervallOutputPlayer() {

		// // Video Player Output Initialisieren
		// Task<Void> taskInitPlayerOutput = new Task<Void>() {
		// @Override
		// protected Void call() throws Exception {
//		
//		model = SimulatorViewController.getModel();
//		configModel = ConfigViewController.getConfigModel();
//		view = SimulatorViewController.getView();


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
		
		// hohle aktuelle ECM Type (odd/even)
		// if odd -> starte even file
		// else -> starte odd file
		
		
		// get msg Time
		 LocalDateTime dateTime;
		// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
		
		String mTime = decryptionECM.getEcmDateTime().substring(4, 10).trim();
		
		int msgTime = Integer.parseInt(decryptionECM.getEcmDateTime().substring(4, 10).trim());

		// Aktuelle Zeit
		dateTime = LocalDateTime.now();
		// currentTime: Stunden Minuten Sekunden
		int currentTime = Integer.parseInt(dateTime.format(formatter).trim());
		
		int delay = msgTime - currentTime;
		
		if (delay < 0) {
			
		}
		
		
		System.out.println("mTime : " + mTime);
		System.out.println("currentTime : " + currentTime);
		System.out.println("msgTime : " + msgTime);
		System.out.println("delay : " + delay);
		
//		ScheduledExecutorService playerOutputExecutor = Executors.newScheduledThreadPool(1);
//		playerOutputExecutor.scheduleWithFixedDelay(streamOutputPlayer(), 0, delay, TimeUnit.SECONDS);
		
		streamOutputPlayer();
		
//		try {
//			Thread.currentThread();
//			Thread.sleep(delay * 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		
		
		
		
		// LocalDateTime dateTime;
		// // Datum Formatieren: Monat Tag Stunden Minuten Sekunden
		// DateTimeFormatter formatter =
		// DateTimeFormatter.ofPattern("MMddHHmmss");
		//
		// // ECM Gültigkeit von
		// int msgDateTime =
		// Integer.parseInt(decryptionECM.getEcmDateTime().trim());
		//
		// // ECM Intervall Zeit
		// int cwIntervall =
		// Integer.parseInt(decryptionECM.getEcmVariablePart().trim());
		//
		// // Aktuelle Zeit
		// dateTime = LocalDateTime.now();
		// // 0808105420
		// int currentTime =
		// Integer.parseInt(dateTime.format(formatter).trim());
		//
		// // aktuelle zeit gleich ecm zeit
		// int delayRunTime = 0;
		// // aktuelle zeit keiner als ecm zeit
		// if (currentTime < msgDateTime) {
		// delayRunTime = msgDateTime - currentTime;
		// } else
		// // aktuelle zeit größer als ecm zeit
		// if (currentTime > msgDateTime) {
		// delayRunTime = msgDateTime + cwIntervall - currentTime;
		// }
		//
		//
		// System.out.println("delayRunTime : " + delayRunTime);
		// System.out.println("cwIntervall : " + cwIntervall);

		// runIntervallPlayerOutput(configModel.getClient().toString(),
		// standardVlcOptions);

		// Video Player Output Initialisieren
		// Task<Void> taskInitPlayerOutput = new Task<Void>() {
		// @Override
		// protected Void call() throws Exception {

		// embeddedOutputMediaPlayer.stop();
		// embeddedOutputMediaPlayer.release();
		// mediaOutputPlayerFactory.release();
		//
		// // if cw is odd = 8000000000000000
		// if (decryptionECM.getEcmHeader().equals("8000000000000000")) {
		// view.getCwOutTF().setText(decryptionECM.getEcmCwOdd());
		// //vlcArgs.add("--ts-csa-ck=" + decryptionECM.getEcmCwOdd());
		// vlcArgs.add("--sout-ts-csa-ck=0123456789ABCDEF");
		// }
		// // if cw is even = 8100000000000000
		// else {
		// view.getCwOutTF().setText(decryptionECM.getEcmCwEven());
		// //vlcArgs.add("--ts-csa2-ck=" + decryptionECM.getEcmCwEven());
		// vlcArgs.add("--sout-ts-csa2-ck=FEDABC9876543210");
		// }

		// String[] standardVlcOptions = vlcArgs.toArray(new
		// String[vlcArgs.size()]);

		// runIntervallPlayerOutput_TEST(configModel.getClient().toString(),
		// standardVlcOptions);

		//getIntervallPlayerOutput().run();

		//
		// Platform.runLater(new Runnable() {
		// @Override
		// public void run() {
		// runIntervallPlayerOutput(configModel.getClient().toString(),
		// standardVlcOptions);
		// }
		// });

		// return null;
		// }
		// };

		//ScheduledExecutorService playerOutputExecutor = Executors.newScheduledThreadPool(1);
		// playerOutputExecutor.scheduleWithFixedDelay(taskInitPlayerOutput, 0,
		// model.getCwTime(), TimeUnit.SECONDS);

		// playerOutputExecutor.scheduleAtFixedRate(taskInitPlayerOutput,
		// delayRunTime, cwIntervall, TimeUnit.SECONDS);

		// playerOutputExecutor.scheduleWithFixedDelay(taskInitPlayerOutput,
		// delayRunTime, cwIntervall, TimeUnit.SECONDS);

		// playerOutputExecutor.scheduleAtFixedRate(taskInitPlayerOutput, 0, 10,
		// TimeUnit.SECONDS);

		// playerOutputExecutor.schedule(taskInitPlayerOutput, 0,
		// TimeUnit.SECONDS);

		// // start the task
		// thInitPlayerOutput = new Thread(taskInitPlayerOutput);
		// thInitPlayerOutput.setDaemon(true);
		// thInitPlayerOutput.start();

	}

//	private static Runnable getIntervallPlayerOutput() {
//
//		Task<Void> taskIntervallPlayerOutput = new Task<Void>() {
//			@Override
//			protected Void call() throws Exception {
//
//				Timer timer = new Timer();
//
//				DateFormat dateFormatter = new SimpleDateFormat("MMddHHmmss");
//				Date runTime = null;
//
//				Date startTime;
//
//				startTime = dateFormatter.parse("0000000000");
//
//				while (model.getDecryptionState()) {
//
//					runTime = dateFormatter.parse(decryptionECM.getEcmDateTime());
//					System.out.println("currentTime : " + startTime.getTime());
//					System.out.println("runTime : " + runTime.getTime());
//
//					if (startTime.getTime() < runTime.getTime()) {
//						embeddedOutputMediaPlayer.release();
//						mediaOutputPlayerFactory.release();
//
//						// timer.cancel();
//						// Use this if you want to execute it once
//						timer.schedule(runIntervallPlayerOutput(), runTime);
//						startTime = runTime;
//					}
//
//					Thread.currentThread().wait(Integer.parseInt(decryptionECM.getEcmVariablePart().trim()) * 900);
//
//				} // end while
//
//				return null;
//			} // end call
//		};
//
//		return taskIntervallPlayerOutput;
//
//	}
	
	
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
		
		PlayerInstance playerInstance = new PlayerInstance(embeddedOutputMediaPlayer);
		SimulatorViewController.getPlayers().add(1, playerInstance);

		outputPlayerView = new OutputPlayerView(embeddedOutputMediaPlayer, mediaOutputPlayerFactory,
				mediaOutputPlayerComponent, controlsOutputPanel);

		// play Player
		//embeddedOutputMediaPlayer.playMedia(configModel.getClient().toString());
		SimulatorViewController.getPlayers().get(1).mediaPlayer().play();

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
