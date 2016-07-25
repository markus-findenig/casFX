package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.media.bean.playerbean.MediaPlayer;

import com.sun.jna.NativeLibrary;

import javafx.concurrent.Task;
import model.ConfigModel;
import model.SimulatorModel;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

public class VlcServerController_backup {

	private static SimulatorModel model;

	// Config Modell
	private static ConfigModel configModel;
	
	public static Thread thStreamVLC;
	
	public static HeadlessMediaPlayer mediaPlayer = null;
	
	static boolean state;
	
	static String file;
	
	public static void streamVLC() {
		
		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();
		
		
		Runnable myRunnable = new Runnable() {
			public void run() {
				if (isState()) {
					setFile(model.getInputFile().getParent() + "\\odd.mp4");
					setState(false);
				} else {
					setFile(model.getInputFile().getParent() + "\\even.mp4");
					setState(true);
				}

				streamVlcFile(getFile());
			}
		};
		   
		   ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		   //executor.scheduleAtFixedRate(myRunnable, 0, 10, TimeUnit.SECONDS);
		   executor.scheduleWithFixedDelay(myRunnable, 0, model.getCwTime(), TimeUnit.SECONDS);
		   
		
//		Task<Void> taskStreamVLC = new Task<Void>() {
//			@Override
//			protected Void call() throws Exception {
//				
//				// first init state true for odd
//				//setState(true);
//				setState(true);;
//				
//
//				while (!isCancelled() && model.getEncryptionState()) {
//
//					if (isState()) {
//						setFile(model.getInputFile().getParent() + "\\odd.mp4");
//					} else {
//						setFile(model.getInputFile().getParent() + "\\even.mp4");
//					}
//					 
//					streamVlcFile(getFile());
//					
//					// GUI updaten
////					Platform.runLater(new Runnable() {
////						public void run() {
////								VlcServerController.streamVlcFile(getFile());
////
////						} // end run
////					});
//
//					// Thread wait
//					try {
//						// time in seconds
//						//Thread.currentThread().join();
//						Thread.sleep(model.getCwTime() * 1000);
//
//						// switch odd/even
//						if (isState()) {
//							setState(false);
//						} else {
//							setState(true);
//						}
//					} catch (InterruptedException interrupted) {
//						cancel();
//					}
//
//				} // end while
//				return null;
//			} // end call
//		};
//
//		// start the task
//		thStreamVLC = new Thread(taskStreamVLC);
//		thStreamVLC.setDaemon(true);
//		thStreamVLC.start();

		
	}
	

	/**
	 * Streamt die angegebene Datei mittels RTP
	 * @param outfile Datei zum streamen
	 */
	public static void streamVlcFile(String outfile) {
		
		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();
		
		
//		if (mediaPlayer.isPlaying()) {
//			mediaPlayer.stop();
//		}

		String options = formatRtpStream(configModel.getServer());
		String workKeyId;

		// get work key
		// --sout-ts-csa-use=<string> CSA Key in use CSA encryption key
		// used. It can be the odd/first/1 (default)
		// or the even/second/2 one.
		if (model.getEcmWorkKeyId() == "00") {
			workKeyId = "1";
		} else {
			workKeyId = "2";
		}

		List<String> vlcArgs = new ArrayList<String>();
		// "--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}";
		vlcArgs.add(options);

		vlcArgs.add("--sout-ts-crypt-video");
		vlcArgs.add("--sout-ts-crypt-audio");
		vlcArgs.add("--sout-ts-csa-use=" + workKeyId);
		vlcArgs.add("--sout-ts-csa-ck=" + model.getEcmCwOdd());
		vlcArgs.add("--sout-ts-csa2-ck=" + model.getEcmCwEven());
		vlcArgs.add("--ttl=1");

		vlcArgs.add("--no-sout-rtp-sap");
		vlcArgs.add("--no-sout-standard-sap");
		vlcArgs.add("--sout-all");
		vlcArgs.add("--sout-keep");

		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
		mediaPlayer.setVolume(0);
		mediaPlayer.playMedia(outfile);
		
		
	}

	/**
	 * Formatiert den server String für die VLC input Parameter
	 * @param server Protokoll, Adresse und IP des Servers
	 * @return String für VLC Parameter
	 */
	private static String formatRtpStream(String server) {
		// default server = rtp://239.0.0.1:5004
		String[] rtpSplit = server.split("://");
		// rtp = rtpSplit[0]
		String ipPort = rtpSplit[1];
		String[] ip = ipPort.split(":");

		StringBuilder sb = new StringBuilder(200);
		sb.append("--sout=#rtp{proto=udp,mux=ts,dst=");
		sb.append(ip[0]);
		sb.append(",port=");
		sb.append(ip[1]);
		sb.append("}");
		return sb.toString();
	}

	public static String getFile() {
		return file;
	}

	public static void setFile(String f) {
		file = f;
	}

	public static boolean isState() {
		return state;
	}

	public static void setState(boolean s) {
		state = s;
	}
	
}
