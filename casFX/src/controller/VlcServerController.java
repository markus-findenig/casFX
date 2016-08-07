package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardCopyOption.*;

import javafx.application.Platform;
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
import uk.co.caprica.vlcj.player.media.Media;
import view.InputPlayerView;

public class VlcServerController {

	private static SimulatorModel model;

	// Config Modell
	private static ConfigModel configModel;
	
	// Encryption ECM Model
	private static EncryptionECM encryptionECM;

	public static Thread thStreamVLC;
	
	private static ProcessBuilder pb;
	public static Process p;
	
	
	public static EmbeddedMediaPlayer mediaPlayer;
	
	public static MediaPlayerFactory mediaPlayerFactory;
	
	public static HeadlessMediaPlayer headlessMediaPlayer;
	
	private static boolean state;

	private static String vlcPath;

	private static String inFileOdd;
	private static String inFileEven;
	
	private static Path streamFileOdd;
	private static Path streamFileEven;
	
	private static String[] standardVlcOptions;
	private static String[] standardMediaOptions;
	
	private static double startTime;
	private static double stopTime;

	public static void initVLC() {
		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();
		vlcPath = configModel.getVlcPath();
		
		encryptionECM = EncryptionController.getEncryptionECM();

		inFileOdd = model.getInputFile().getParent() + "\\odd.mp4";
		inFileEven = model.getInputFile().getParent() + "\\even.mp4";
		
		streamFileOdd = Paths.get(model.getInputFile().getParent() + "\\streamOdd.mp4");
		streamFileEven = Paths.get(model.getInputFile().getParent() + "\\streamEven.mp4");
		
		// first init state true for odd
		setState(true);
		
		thStreamVLC = null;

		
		// init Timer
		setStartTime(0);
		setStopTime(model.getCwTime());

//		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
//		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
//		mediaPlayer.setVolume(0);

	}

	
	public static void streamVLCmediaPlayer() {
			
		// rtp = "rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}"
		String rtp = formatRtpStream(configModel.getServer());

		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.clear();
		vlcArgs.add("--intf=dummy");
		vlcArgs.add("--dummy-quiet");
		
		vlcArgs.add("--sout=#"+ rtp);
//		vlcArgs.add("--sout=#duplicate{dst=" + rtp + ",dst=display}");
		
		vlcArgs.add("--sout-ts-crypt-video");
		vlcArgs.add("--sout-ts-crypt-audio");
		
//		vlcArgs.add("--no-repeat");
//		vlcArgs.add("--no-loop");
		vlcArgs.add("--ttl=1");
		vlcArgs.add("--no-sout-rtp-sap");
		vlcArgs.add("--no-sout-standard-sap");
		vlcArgs.add("--sout-all");
		vlcArgs.add("--sout-keep");
//		vlcArgs.add("--no-plugins-cache");
//		vlcArgs.add("vlc://quit");
		
	

		// -------------------------------------------------
		// if true = odd
		if (isState()) {
			vlcArgs.add("--sout-ts-csa-use=1");
			//vlcArgs.add("--sout-ts-csa-ck=0123456789ABCDEF");
			vlcArgs.add("--sout-ts-csa-ck=" + encryptionECM.getEcmCwOdd());
			String[] standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);
			
			// switch
			setState(false);
		
			runPlayer(inFileOdd, standardVlcOptions);
			
		}
		// -------------------------------------------------
		// if else = even
		else {
			vlcArgs.add("--sout-ts-csa-use=2");
			//vlcArgs.add("--sout-ts-csa2-ck=FEDABC9876543210");
			vlcArgs.add("--sout-ts-csa2-ck=" + encryptionECM.getEcmCwEven());
			String[] standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);
			
			// switch
			setState(true);
					
			runPlayer(inFileEven, standardVlcOptions);
			
		} // end if else
	}
	
	private static void runPlayer(String file, String[] standardMediaOptions) {
		
		mediaPlayerFactory = new MediaPlayerFactory(standardMediaOptions);
		headlessMediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
		headlessMediaPlayer.setVolume(0);
		headlessMediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				System.out.println("playing: " + file);
			}
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				System.out.println("finished: " + file);
				//mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayerFactory.release();
				streamVLCmediaPlayer();
			}
		});
		
		headlessMediaPlayer.playMedia(file);
	}
	


	/**
	 * Streamt die angegebene Datei mittels RTP
	 * 
	 * @param outfile
	 *            Datei zum streamen
	 */
	public static void streamVlcFile(String outfile) {

		String rtp = formatRtpStream(configModel.getServer());
	
		List<String> vlcArgs = new ArrayList<String>();
		
//		vlcArgs.add("--intf=dummy");
//		vlcArgs.add("--dummy-quiet");
		
		// "--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}";
		// rtp = "rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}"
		
		vlcArgs.add("--sout=#duplicate{dst=" + rtp + ",dst=display}");

		 vlcArgs.add("--sout-ts-crypt-video");
		 vlcArgs.add("--sout-ts-crypt-audio");
		 vlcArgs.add("--sout-ts-csa-use=1");
		 vlcArgs.add("--sout-ts-csa-ck=" + encryptionECM.getEcmCwOdd());
		 //vlcArgs.add("--sout-ts-csa2-ck=" + model.getEcmCwEven());
		
		vlcArgs.add("--no-repeat");
		vlcArgs.add("--no-loop");
		vlcArgs.add("--ttl=1");

		vlcArgs.add("--no-sout-rtp-sap");
		vlcArgs.add("--no-sout-standard-sap");
		vlcArgs.add("--sout-all");
		vlcArgs.add("--sout-keep");
		vlcArgs.add("--no-plugins-cache");
		vlcArgs.add("vlc://quit");
		
		String[] standardMediaOptions = vlcArgs.toArray(new String[vlcArgs.size()]);

		mediaPlayerFactory = new MediaPlayerFactory(standardMediaOptions);
		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
		
		PlayerControlsPanel controlsPanel = new PlayerControlsPanel(mediaPlayer);
		EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayerComponent.add(controlsPanel);
	
		controlsPanel.updateVolume(0);
		
		new InputPlayerView(mediaPlayer, mediaPlayerFactory, mediaPlayerComponent,
				controlsPanel);
		
		mediaPlayer.playMedia(outfile);

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
		sb.append("rtp{proto=udp,mux=ts{use-key-frames},dst=");
		sb.append(ip[0]);
		sb.append(",port=");
		sb.append(ip[1]);
		sb.append("}");
		return sb.toString();
	}
	

	public static boolean isState() {
		return state;
	}

	public static void setState(boolean s) {
		state = s;
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
