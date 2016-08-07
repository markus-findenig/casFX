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

public class VlcServerController_backup {

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
	
	static HeadlessMediaPlayer headlessMediaPlayer;
	
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
		
//		p = null;
//		
//		thStreamVLC = null;
//		
//		mediaPlayerFactory = new MediaPlayerFactory();
//		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
		
		// init Timer
		setStartTime(0);
		setStopTime(model.getCwTime());

//		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
//		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
//		mediaPlayer.setVolume(0);

	}

	public static void streamVLC() {

		File streamOddFile;
		File streamEvenFile;
		
		String rtp = formatRtpStream(configModel.getServer());
			
		// "--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}";
		// rtp = "rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}"
		
		
		
		
		// -------------------------------------------------
		// if true = odd
		if (isState()) {

			System.out.println("State odd");

			// System.out.println("vlcPath:" + vlcPath);
			// System.out.println("inFileOdd:" + inFileOdd);
			//
			// System.out.println("--sout-ts-csa-ck=" + model.getEcmCwOdd());
			// System.out.println("--sout-ts-csa2-ck=" + model.getEcmCwEven());

			streamOddFile = new File(inFileOdd);

//			pb = new ProcessBuilder(vlcPath + "\\vlc", "--intf=dummy", "--dummy-quiet", inFileOdd,
//					formatRtpStream(configModel.getServer()), "--no-sout-ts-crypt-video", "--no-sout-ts-crypt-audio",
//					"--sout-ts-csa-use=1", "--sout-ts-csa-ck=" + model.getEcmCwOdd(),
//					"--sout-ts-csa2-ck=" + model.getEcmCwEven(), "--no-repeat", "--no-loop", "--ttl=1",
//					"--no-sout-rtp-sap", "--no-sout-standard-sap", "--sout-all", "--sout-keep", "vlc://quit");

			pb = new ProcessBuilder(vlcPath + "\\vlc", "--intf=dummy", "--dummy-quiet", inFileOdd,
					"--sout=#" + rtp, "--sout-ts-crypt-video", "--sout-ts-crypt-audio",
					"--sout-ts-csa-use=1", "--sout-ts-csa-ck=0123456789ABCDEF",
					"--sout-ts-csa2-ck=FEDABC9876543210", "--no-repeat", "--no-loop", "--no-plugins-cache", "--ttl=1",
					"--no-sout-rtp-sap", "--no-sout-standard-sap", "--sout-all", "--sout-keep", "vlc://quit");
			
			pb.redirectErrorStream(true);
			pb.redirectInput(streamOddFile);
			pb.redirectOutput(ProcessBuilder.Redirect.PIPE);

			try {
				p = pb.start();
				// p.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// p.destroy();

			// switch
			setState(false);

		}
		// -------------------------------------------------
		// if else = even
		else {

			System.out.println("State even");
			streamEvenFile = new File(inFileEven);

//			pb = new ProcessBuilder(vlcPath + "\\vlc", "--intf=dummy", "--dummy-quiet", inFileEven,
//					formatRtpStream(configModel.getServer()), "--no-sout-ts-crypt-video", "--no-sout-ts-crypt-audio",
//					"--sout-ts-csa-use=2", "--sout-ts-csa-ck=" + model.getEcmCwOdd(),
//					"--sout-ts-csa2-ck=" + model.getEcmCwEven(), "--no-repeat", "--no-loop", "--ttl=1",
//					"--no-sout-rtp-sap", "--no-sout-standard-sap", "--sout-all", "--sout-keep", "vlc://quit");

			pb = new ProcessBuilder(vlcPath + "\\vlc", "--intf=dummy", "--dummy-quiet", inFileOdd,
					"--sout=#" + rtp, "--sout-ts-crypt-video", "--sout-ts-crypt-audio",
					"--sout-ts-csa-use=2", "--sout-ts-csa-ck=0123456789ABCDEF",
					"--sout-ts-csa2-ck=FEDABC9876543210", "--no-repeat", "--no-loop", "--no-plugins-cache", "--ttl=1",
					"--no-sout-rtp-sap", "--no-sout-standard-sap", "--sout-all", "--sout-keep", "vlc://quit");
			
			pb.redirectErrorStream(true);
			pb.redirectInput(streamEvenFile);
			pb.redirectOutput(ProcessBuilder.Redirect.PIPE);

			try {
				p = pb.start();
				// p.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// p.destroy();

			// switch
			setState(true);

		} // end if else

	}
	
	public static void streamVLCProcessBuilder_test() {
		
		ProcessBuilder pb;
		Process p;
		
//		File streamOddFile;
//		File streamEvenFile;
		
		// rtp = "rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}"
		String rtp = formatRtpStream(configModel.getServer());
		
		while (!EncryptionController.vlcExecutor.isShutdown()) {
			
			System.out.println("----odd-----" );
			//streamOddFile = new File(inFileOdd);
			
			pb = new ProcessBuilder(vlcPath + "\\vlc", 
					"--intf=dummy", "--dummy-quiet", 
					inFileOdd,
					"--sout=#" + rtp, 
					"--sout-ts-crypt-video", 
					"--sout-ts-crypt-audio",
					"--sout-ts-csa-use=1", 
					"--sout-ts-csa-ck=0123456789ABCDEF", 
					"--sout-ts-csa2-ck=FEDABC9876543210",
					"--no-repeat", 
					"--no-loop", 
					"--ttl=1", 
					"--no-sout-rtp-sap", 
					"--no-sout-standard-sap", 
					"--sout-all",
					"--sout-keep", 
					"vlc://quit");
			
			try {
//				pb.redirectErrorStream(true);
//				pb.redirectInput(streamOddFile);
//				pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
				p = pb.start();
				//p.waitFor();
				p.waitFor(model.getCwTime() + 3, TimeUnit.SECONDS);
				//p.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("----even-----" );
			
//			streamEvenFile = new File(inFileEven);
			
			pb = new ProcessBuilder(vlcPath + "\\vlc", 
					"--intf=dummy", "--dummy-quiet", 
					inFileEven,
					"--sout=#" + rtp, 
					"--sout-ts-crypt-video", 
					"--sout-ts-crypt-audio",
					"--sout-ts-csa-use=2", 
					"--sout-ts-csa-ck=0123456789ABCDEF", 
					"--sout-ts-csa2-ck=FEDABC9876543210",
					"--no-repeat", 
					"--no-loop", 
					"--ttl=1", 
					"--no-sout-rtp-sap", 
					"--no-sout-standard-sap", 
					"--sout-all",
					"--sout-keep", 
					"vlc://quit");
			
			try {
//				pb.redirectErrorStream(true);
//				pb.redirectInput(streamEvenFile);
//				pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
				p = pb.start();
				//p.waitFor();
				p.waitFor(model.getCwTime() + 3, TimeUnit.SECONDS);
				//p.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
		}
		
		
	}

	public static void streamVLCProcessBuilder() {
		
		
		String rtp = formatRtpStream(configModel.getServer());
		
		// "--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}";
		// rtp = "rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}"

		// -------------------------------------------------
		// if true = odd
		if (isState()) {

			System.out.println("State odd");

			// System.out.println("vlcPath:" + vlcPath);
			// System.out.println("inFileOdd:" + inFileOdd);
			//
			// System.out.println("--sout-ts-csa-ck=" +
			// model.getEcmCwOdd());
			// System.out.println("--sout-ts-csa2-ck=" +
			// model.getEcmCwEven());
			
			ProcessBuilder pb = new ProcessBuilder(vlcPath + "\\vlc", 
					"--intf=dummy", "--dummy-quiet", 
					inFileOdd,
					"--sout=#" + rtp, 
					"--sout-ts-crypt-video", 
					"--sout-ts-crypt-audio",
					"--sout-ts-csa-use=1", 
					"--sout-ts-csa-ck=0123456789ABCDEF", 
					"--sout-ts-csa2-ck=FEDABC9876543210",
					"--no-repeat", 
					"--no-loop", 
					"--ttl=1", 
					"--no-sout-rtp-sap", 
					"--no-sout-standard-sap", 
					"--sout-all",
					"--sout-keep", "vlc://quit");

			// "--sout-ts-csa-ck=" + model.getEcmCwOdd(),
			// "--sout-ts-csa2-ck=" + model.getEcmCwEven(),

			try {
				p = pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

				
			// switch
			setState(false);

		}
		// -------------------------------------------------
		// if else = even
		else {

			System.out.println("State even");

			ProcessBuilder pb = new ProcessBuilder(vlcPath + "\\vlc", "--intf=dummy", "--dummy-quiet", 
					inFileEven,
					"--sout=#" + rtp, 
					"--sout-ts-crypt-video", 
					"--sout-ts-crypt-audio",
					"--sout-ts-csa-use=2", 
					"--sout-ts-csa-ck=0123456789ABCDEF", 
					"--sout-ts-csa2-ck=FEDABC9876543210",
					"--no-repeat", 
					"--no-loop", 
					"--ttl=1", 
					"--no-sout-rtp-sap", 
					"--no-sout-standard-sap", 
					"--sout-all",
					"--sout-keep", 
					"vlc://quit");

			// "--sout-ts-csa-ck=" + model.getEcmCwOdd(),
			// "--sout-ts-csa2-ck=" + model.getEcmCwEven(),

				try {
					p = pb.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
		
			// switch
			setState(true);

		} // end if else
		
		
	}

	public static void streamVLCtask() {


		Task<Void> taskStreamVLC = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// GUI updaten
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						// "--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}";
						// rtp = "rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}"

						String rtp = formatRtpStream(configModel.getServer());

						List<String> vlcArgs = new ArrayList<String>();
						vlcArgs.add("--intf=dummy");
						vlcArgs.add("--dummy-quiet");

						vlcArgs.add("--sout=#" + rtp);
						 vlcArgs.add("--sout-ts-crypt-video");
						 vlcArgs.add("--sout-ts-crypt-audio");

						vlcArgs.add("--sout-ts-csa-ck=0123456789ABCDEF");
						vlcArgs.add("--sout-ts-csa2-ck=FEDABC9876543210");
						
//						vlcArgs.add("--sout-ts-csa-ck=" + model.getEcmCwOdd());
//						vlcArgs.add("--sout-ts-csa2-ck=" + model.getEcmCwEven());

						vlcArgs.add("--no-repeat");
						vlcArgs.add("--no-loop");
						vlcArgs.add("--ttl=1");

						vlcArgs.add("--no-sout-rtp-sap");
						vlcArgs.add("--no-sout-standard-sap");
						vlcArgs.add("--sout-all");
						vlcArgs.add("--sout-keep");
						vlcArgs.add("vlc://quit");

						// -------------------------------------------------
						// if true = odd
						if (isState()) {

							vlcArgs.add("--sout-ts-csa-use=1");

							String[] standardMediaOptions = vlcArgs.toArray(new String[vlcArgs.size()]);

							mediaPlayerFactory = new MediaPlayerFactory(standardMediaOptions);
							mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
							
							mediaPlayer.playMedia(inFileOdd);

							// switch
							setState(false);
						}
						// -------------------------------------------------
						// if else = even
						else {

							vlcArgs.add("--sout-ts-csa-use=2");

							String[] standardMediaOptions = vlcArgs.toArray(new String[vlcArgs.size()]);

							mediaPlayerFactory = new MediaPlayerFactory(standardMediaOptions);
							mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
							
							mediaPlayer.playMedia(inFileEven);

							// switch
							setState(true);
						} // end if else

					} // end run
				});

				return null;
			} // end call
		};

		// start the task
		thStreamVLC = new Thread(taskStreamVLC);
		thStreamVLC.setDaemon(true);
		thStreamVLC.start();
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
		
//		vlcArgs.add("--sout-ts-csa-ck=" + model.getEcmCwOdd());
//		vlcArgs.add("--sout-ts-csa2-ck=" + model.getEcmCwEven());
		
		
		
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
//				Thread.currentThread().notify();
//				generatePlayer();
				
			}
		});
		
		headlessMediaPlayer.playMedia(file);
	

//		try {
//			Thread.currentThread().wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		
		
	}
	
	public static void streamVLCmediaPlayerInputFile() {
		
		System.out.println("mediaPlayer.isPlaying():" + mediaPlayer.isPlaying());
		
		while (!EncryptionController.vlcExecutor.isShutdown() && !mediaPlayer.isPlaying()) {

		String server = formatRtpStream(configModel.getServer());

		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--intf=dummy");
		vlcArgs.add("--dummy-quiet");

		vlcArgs.add(server);
		vlcArgs.add("--sout-ts-crypt-video");
		vlcArgs.add("--sout-ts-crypt-audio");

//		vlcArgs.add("--sout-ts-csa-ck=" + model.getEcmCwOdd());
//		vlcArgs.add("--sout-ts-csa2-ck=" + model.getEcmCwEven());
		
		vlcArgs.add("--sout-ts-csa-ck=0123456789ABCDEF");
		vlcArgs.add("--sout-ts-csa2-ck=FEDABC9876543210");

		vlcArgs.add("--no-repeat");
		vlcArgs.add("--no-loop");
		vlcArgs.add("--ttl=1");

		vlcArgs.add("--no-plugins-cache");
		
		// TODO del
//		vlcArgs.add("--reset-config");
//		vlcArgs.add("--reset-plugins-cache");
		
		vlcArgs.add("--no-sout-rtp-sap");
		vlcArgs.add("--no-sout-standard-sap");
		vlcArgs.add("--sout-all");
		vlcArgs.add("--sout-keep");
		vlcArgs.add("vlc://quit");

		List<String> mediaArgs = new ArrayList<String>();
//
//		mediaArgs.add(":start-time=" + getStartTime());
//		mediaArgs.add(":stop-time=" + getStopTime());
		
		// -------------------------------------------------
		// if true = odd
		if (isState()) {
		
					vlcArgs.add("--sout-ts-csa-use=1");
					standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);
					standardMediaOptions = mediaArgs.toArray(new String[mediaArgs.size()]);
				
					mediaPlayerFactory = new MediaPlayerFactory(standardVlcOptions);
					mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
					
					mediaPlayer.setVolume(0);
					
					//mediaPlayer.playMedia(model.getInputFile().toString(), standardMediaOptions);
					mediaPlayer.playMedia(inFileOdd);
					
//					
//					try {
//						Thread.sleep(model.getCwTime() * 1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					mediaPlayer.stop();
//					mediaPlayer.release();
//					mediaPlayerFactory.release();
					
					
	
			
			// switch
			setState(false);
			
				
		}
		// -------------------------------------------------
		// if else = even
		else {

					vlcArgs.add("--sout-ts-csa-use=2");
					standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);
					standardMediaOptions = mediaArgs.toArray(new String[mediaArgs.size()]);
				
					mediaPlayerFactory = new MediaPlayerFactory(standardVlcOptions);
					mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
					
					mediaPlayer.setVolume(0);
					
					//mediaPlayer.playMedia(model.getInputFile().toString(), standardMediaOptions);
					mediaPlayer.playMedia(inFileEven);
					
//					try {
//						Thread.sleep(model.getCwTime() * 1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					mediaPlayer.stop();
//					mediaPlayer.release();
//					mediaPlayerFactory.release();
					
		
			// switch
			setState(true);
		
			
			
		} // end if else
		
		// Update Start/Stop
		setStartTime(getStopTime() + 0.001);
		setStopTime(getStopTime() + model.getCwTime());
		
		} //end while

	}
	
	public static void streamVLCmediaPlayerInputFileOhneTask() {
		
		while (!EncryptionController.vlcExecutor.isShutdown()) {

		String server = formatRtpStream(configModel.getServer());

		List<String> vlcArgs = new ArrayList<String>();
		vlcArgs.add("--intf=dummy");
		vlcArgs.add("--dummy-quiet");

		vlcArgs.add(server);
		vlcArgs.add("--sout-ts-crypt-video");
		vlcArgs.add("--sout-ts-crypt-audio");

//		vlcArgs.add("--sout-ts-csa-ck=" + model.getEcmCwOdd());
//		vlcArgs.add("--sout-ts-csa2-ck=" + model.getEcmCwEven());
		
		vlcArgs.add("--sout-ts-csa-ck=0123456789ABCDEF");
		vlcArgs.add("--sout-ts-csa2-ck=FEDABC9876543210");

		vlcArgs.add("--no-repeat");
		vlcArgs.add("--no-loop");
		vlcArgs.add("--ttl=1");

		vlcArgs.add("--no-plugins-cache");
		
		vlcArgs.add("--no-sout-rtp-sap");
		vlcArgs.add("--no-sout-standard-sap");
		vlcArgs.add("--sout-all");
		vlcArgs.add("--sout-keep");
		vlcArgs.add("vlc://quit");

		List<String> mediaArgs = new ArrayList<String>();
//
//		mediaArgs.add(":start-time=" + getStartTime());
//		mediaArgs.add(":stop-time=" + getStopTime());
		
		// -------------------------------------------------
		// if true = odd
		if (isState()) {
		
					vlcArgs.add("--sout-ts-csa-use=1");
					String[] standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);
					String[] standardMediaOptions = mediaArgs.toArray(new String[mediaArgs.size()]);
				
					mediaPlayerFactory = new MediaPlayerFactory(standardVlcOptions);
					mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
					
					mediaPlayer.setVolume(0);
					
					//mediaPlayer.playMedia(model.getInputFile().toString(), standardMediaOptions);
					mediaPlayer.playMedia(inFileOdd);
					
					
					try {
						Thread.sleep(10000);
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayerFactory.release();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
			// switch
			setState(false);
			
					
			
		}
		// -------------------------------------------------
		// if else = even
		else {

					vlcArgs.add("--sout-ts-csa-use=2");
					String[] standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);
					String[] standardMediaOptions = mediaArgs.toArray(new String[mediaArgs.size()]);
				
					mediaPlayerFactory = new MediaPlayerFactory(standardVlcOptions);
					mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
					
					mediaPlayer.setVolume(0);
					
					//mediaPlayer.playMedia(model.getInputFile().toString(), standardMediaOptions);
					mediaPlayer.playMedia(inFileEven);
					
					try {
						Thread.sleep(10000);
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayerFactory.release();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
			// switch
			setState(true);
			
		
			
		} // end if else
		
		// Update Start/Stop
		setStartTime(getStopTime() + 0.001);
		setStopTime(getStopTime() + model.getCwTime());
		
		} // end while

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

		// MediaPlayerFactory mediaPlayerFactory = new
		// MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
		// mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
		
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(standardMediaOptions);
		EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
		
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
