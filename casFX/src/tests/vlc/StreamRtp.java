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
import javafx.scene.media.MediaPlayer.Status;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.player.manager.MediaManager;
import uk.co.caprica.vlcj.test.VlcjTest;

/* An example of how to stream a media file over HTTP.
* <p>
* The client specifies an MRL of <code>http://127.0.0.1:5555</code>
*/
public class StreamRtp extends VlcjTest {

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
		StreamRtp.start = start;
	}



	public static double getStop() {
		return stop;
	}



	public static void setStop(double stop) {
		StreamRtp.stop = stop;
	}


	
	
	public static void main(String[] args) throws Exception {
				 

	
		

		Runnable myRunnable = new Runnable(){
		     public void run(){
		    	 
		    	NativeLibrary.addSearchPath("libvlc", "C:/ProgLoc/VideoLAN/VLC");
		        System.out.println("Runnable running");
		        
		        double _start = getStart();
		        double _stop = getStop();
		        
		        activateStream2(_start, _stop);
		        
		        setStart(_stop + 0.01);
		        
		        
		        setStop(_stop + 10);
		     }
		   };

		   
		   ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		   //executor.scheduleAtFixedRate(myRunnable, 0, 10, TimeUnit.SECONDS);
		   executor.scheduleWithFixedDelay(myRunnable, 0, 9999, TimeUnit.MILLISECONDS);

//		// Don't exit
//		Thread.currentThread().join();
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

		vlcArgs.add("--sout-ts-crypt-video");
		vlcArgs.add("--sout-ts-crypt-audio");
		vlcArgs.add("--sout-ts-csa-use=2");
		vlcArgs.add("--sout-ts-csa-ck=" + csaCK);
		vlcArgs.add("--sout-ts-csa2-ck=" + csa2CK);
		
		vlcArgs.add("--start-time=" + start);
		vlcArgs.add("--stop-time=" + stop);

		// 4000ms = 4sec
		
		
//		vlcArgs.add("--sout-ts-dts-delay=300");
//		
//		vlcArgs.add("--rtsp-session-timeout=10");
		//vlcArgs.add("--file-caching=300");
		//vlcArgs.add("--network-caching=2000");
		
		//vlcArgs.add("--live-caching=0");
		
		
		//vlcArgs.add("--loop");
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
			
			sb.append("--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}");

			return sb.toString();
		}



}