package tests.vlc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.jna.NativeLibrary;

import javafx.application.Platform;
import javafx.concurrent.Task;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.test.VlcjTest;

/* An example of how to stream a media file over HTTP.
* <p>
* The client specifies an MRL of <code>http://127.0.0.1:5555</code>
*/
public class StreamHttp extends VlcjTest {

	// Stream RTP

	static String media = "D:\\Users\\Videos\\2014-11-22_ORF_BINGO_Edith-Oma.mp4 ";

	protected static boolean run = true;
	
	static Thread thStream;
	
	
   	static double start = 0;

	static double stop = 10;
	
	
	public static double getStart() {
		return start;
	}



	public static void setStart(double start) {
		StreamHttp.start = start;
	}



	public static double getStop() {
		return stop;
	}



	public static void setStop(double stop) {
		StreamHttp.stop = stop;
	}


	
	
	public static void main(String[] args) throws Exception {
				 

	
		

		Runnable myRunnable = new Runnable(){
		     @Override
			public void run(){
		    	 
		    	NativeLibrary.addSearchPath("libvlc", "C:/ProgLoc/VideoLAN/VLC");
		        System.out.println("Runnable running");
		        
		        double _start = getStart();
		        double _stop = getStop();
		        
		        activateStream2(_start, _stop);
		        
		        setStart(_stop + 0.01);
		        
		        
		        setStop(_stop + 100000);
		     }
		   };

		   
		   ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		   //executor.scheduleAtFixedRate(myRunnable, 0, 10, TimeUnit.SECONDS);
		   executor.scheduleWithFixedDelay(myRunnable, 0, 10000, TimeUnit.MILLISECONDS);
		   
		
//		
//		while (run) {
//			Thread thread = new Thread(myRunnable);
//			
//			
//			if (thread.isAlive()) {
//				System.out.println("isAlive");
//				
//			} else {
//				thread.start();
//				System.out.println("notAlive");
//			}
//			
//			Thread.sleep(5000);
//			
//			//thread.stop();
//		}
//		
//		// Don't exit
//		Thread.currentThread().join();
	}
	
	
	

	
	
	public static void activateStream() {
		
		System.out.println("activateStream");
		
		Task<Void> taskStream = new Task<Void>() {
		    
			protected Integer call3() throws Exception {
		        int iterations = 0;
		        for (iterations = 0; iterations < 100000; iterations++) {
		            if (isCancelled()) {
		                break;
		            }
		            System.out.println("Iteration " + iterations);
		        }
		        return iterations;
		    }
			
			@Override protected void succeeded() {
		        super.succeeded();
		        updateMessage("Done!");
		    }

		    @Override protected void cancelled() {
		        super.cancelled();
		        updateMessage("Cancelled!");
		    }
			@Override protected void failed() {
			    super.failed();
			    updateMessage("Failed!");
			    }
			
			@Override
			public Void call() throws Exception {
				// erster Status
				LocalDateTime dateTime;
				// Datum Formatieren: Monat Tag Stunden Minuten Sekunden
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
				
				
				
				String type = "rtp";
				String server = "127.0.0.1";
				int port = 8554;
				String id = "cas";
				String csaCK = "0123456789ABCDEF";
				String csa2CK = "0123456789ABCDE0";
				
				
				
				String options = formatHttpStream(type, server, port, id);

				List<String> vlcArgs = new ArrayList<String>();

				vlcArgs.add(options);
				
				vlcArgs.add("--sout-ts-crypt-video");
				vlcArgs.add("--sout-ts-crypt-audio");
				vlcArgs.add("--sout-ts-csa-use=2");
				vlcArgs.add("--sout-ts-csa-ck=" + csaCK);
				vlcArgs.add("--sout-ts-csa2-ck=" + csa2CK);
				
				// 4000ms = 4sec
				//vlcArgs.add("--file-caching=4000");
				
				vlcArgs.add("--no-sout-rtp-sap");
				vlcArgs.add("--no-sout-standard-sap");
				vlcArgs.add("--sout-all");
				vlcArgs.add("--sout-keep");

				
				System.out.println("Streaming '" + media + "' to '" + vlcArgs.toArray(new String[vlcArgs.size()]) + "'");

				MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
				HeadlessMediaPlayer mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
				
				
							
				while (!isCancelled()) {
					if (run) {
						
						
						
						// GUI updaten
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								mediaPlayer.setVolume(0);
								mediaPlayer.playMedia(media);
							}
						});
						
						

						// Thread wait
						try {
							// time in seconds
							Thread.sleep(20 * 1000);
							
							
							
						} catch (InterruptedException interrupted) {
							 break;
						}
					} else {
						// no scrambling
						// Thread beenden
						isCancelled();
						
					}
					// Status jedes mal überprüfen
					
				}
				return null;
			} // end call
			
			
			
		};
		
		// start the task
		thStream = new Thread(taskStream);
		thStream.setDaemon(true);
		thStream.start();
	}
	
	
	public static void activateStream2(double start, double stop) {
		String type = "rtp";
		String server = "127.0.0.1";
		int port = 8554;
		String csaCK = "0123456789ABCDEF";
		String csa2CK = "0123456789ABCDE0";

		String id = "cas";

		String options = formatHttpStream(type, server, port, id);

		List<String> vlcArgs = new ArrayList<String>();

		vlcArgs.add(options);

//		vlcArgs.add("--sout-ts-crypt-video");
//		vlcArgs.add("--sout-ts-crypt-audio");
//		vlcArgs.add("--sout-ts-csa-use=2");
//		vlcArgs.add("--sout-ts-csa-ck=" + csaCK);
//		vlcArgs.add("--sout-ts-csa2-ck=" + csa2CK);
		
		vlcArgs.add("--start-time=" + start);
		vlcArgs.add("--stop-time=" + stop);

		// 4000ms = 4sec
		
		
		//vlcArgs.add("--sout-ts-dts-delay=100");
		
		vlcArgs.add("--packetizer-mpegvideo-sync-iframe");
//		
		//vlcArgs.add("--rtsp-session-timeout=10000");
		//vlcArgs.add("--file-caching=300");
		//vlcArgs.add("--network-caching=500");
//		
//		vlcArgs.add("--live-caching=500");
	
		
		//vlcArgs.add("--loop");
		//vlcArgs.add("--sout-ts-pcr=100");
		
		// --sout-mux-caching=0 --sout-udp-caching=0 --sout-udp-group=10 --clock-synchro=1 --sout-ts-shaping=2000 --sout-ts-use-key-frames
//		vlcArgs.add("--sout-mux-caching=0");
//		vlcArgs.add("--sout-udp-caching=0");
		//vlcArgs.add("--sout-udp-group=10");
		//vlcArgs.add("--clock-synchro=1");
//		vlcArgs.add("--sout-ts-shaping=100");
//		vlcArgs.add("--sout-ts-use-key-frames");
				
		
//		vlcArgs.add("--video-filter=scene");
//		vlcArgs.add("--scene-ratio=200");
		
//		
//		vlcArgs.add("--sout-x264-keyint=9999");
//		
//		vlcArgs.add("--sout-x264-min-keyint=9000");
//		vlcArgs.add("--sout-x264-scenecut=1000");
//		
//		vlcArgs.add("--no-sout-x264-opengop");
		
		
//		vlcArgs.add("--sout-rtp-caching=0");
//		vlcArgs.add("--sout-udp-caching=0");
		
		//vlcArgs.add("--clock-jitter=100");
		
		// PCR interval (ms) (Ganzzahl)
		vlcArgs.add("--sout-ts-pcr=100");
		
		vlcArgs.add("--ttl=1");
	
		vlcArgs.add("--no-sout-rtp-sap");
		vlcArgs.add("--no-sout-standard-sap");
		vlcArgs.add("--sout-all");
		vlcArgs.add("--sout-keep");

		System.out.println("Streaming '" + media + "' to '" + vlcArgs.toArray(new String[vlcArgs.size()]) + "'");

		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));

		HeadlessMediaPlayer mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
		System.out.println("run: ");
		
		

		mediaPlayer.setVolume(0);
		mediaPlayer.playMedia(media);
	}
	
	/**
	 * vlc -vvv input_stream --sout '#rtp{dst=192.168.0.12,port=1234,sdp=rtsp://server.example.org:8080/test.sdp}' 
	 * @param type
	 * @param serverAddress
	 * @param serverPort
	 * @param id
	 * @return
	 */
		private static String formatHttpStream(String type, String serverAddress, int serverPort, String id) {
			StringBuilder sb = new StringBuilder(200);

			//sb.append("--sout=#duplicate{dst=display,dst=rtp{");
			//sb.append(":sout=#duplicate{dst=rtp{");
			
//			sb.append("--sout=#"+ type );
//			sb.append("{dst=" + serverAddress);
//			sb.append(",port=" + serverPort);
//			sb.append(",mux=ts,name=cas}");
			
			//sb.append("--sout=#rtp{dst=192.168.178.101,port=1234,mux=ts,sdp=rtsp://192.168.178.101:8080/cas.sdp}");
			
			//sb.append("--sout=#rtp{dst=127.0.0.1,port=1234,mux=ts,sdp=rtsp://127.0.0.1:8080/cas.sdp}");
			
			//sb.append("--sout=#rtp{dst=127.0.0.1,port=1234,mux=ts,sdp=rtsp://192.168.178.101:8080/cas.sdp}");
			
			
			//sb.append("--sout=#std{access=udp,mux=ts,dst=192.168.178.101:1234}");
			
			//sb.append("--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}");
			
			//sb.append("--sout=#rtp{dst=127.0.0.1,port=1234,mux=ts,sdp=rtsp://127.0.0.1:8080/cas.sdp}");
			
			sb.append("--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}");
			
						
			//sb.append("--sout=#rtp{dst=127.0.0.1,port=1234,mux=ts}");
			
			
			//sb.append("--sout=#transcode{vcodec=h264,venc=x264{scenecut=10,bframes=0}}:rtp{dst=127.0.0.1,port=1234,mux=ts,sdp=rtsp://127.0.0.1:8080/cas.sdp}");
			
			
//			sb.append("{dst=" + serverAddress);
//			sb.append(",port=" + serverPort);
//			sb.append(",mux=ts}");

			return sb.toString();
		}



}