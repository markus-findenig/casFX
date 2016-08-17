package tests.vlc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.test.VlcjTest;

/* An example of how to stream a media file over HTTP.
* <p>
* The client specifies an MRL of <code>http://127.0.0.1:5555</code>
*/
public class StreamVideo extends VlcjTest {

	// Stream RTP

	static String media = "D:\\Users\\Videos\\BINGO.mp4 ";
	
	
	static String inFileOdd = "D:\\Users\\Videos\\odd.mp4";
	static String inFileEven = "D:\\Users\\Videos\\even.mp4";
	
	static MediaPlayerFactory mediaPlayerFactory;
	static HeadlessMediaPlayer mediaPlayer;
	
	private static boolean state;
	

	public static void main(String[] args) throws Exception {

		NativeLibrary.addSearchPath("libvlc", "C:/ProgLoc/VideoLAN/VLC");

		setState(true);
//
//	    Thread t1 = new Thread(generatePlayer());
//	    
//
//	    t1.start();
//	   
	    
		
		
		Runnable encryptionRunnableThird = new Runnable() {
			@Override
			public void run() {
				generatePlayer();
			}
		};
		
		
		ScheduledExecutorService vlcExecutor = Executors.newScheduledThreadPool(1);
		//vlcExecutor.scheduleWithFixedDelay(encryptionRunnableThird, 3, model.getCwTime(), TimeUnit.SECONDS);
		vlcExecutor.schedule(encryptionRunnableThird, 0, TimeUnit.SECONDS);
		
		
	
		
	}
	
	private static void generatePlayer() {
		String type = "rtp";
		String server = "127.0.0.1";
		int port = 8554;
		String csaCK = "0123456789ABCDEF";
		String csa2CK = "FEDABC9876543210";

		String id = "cas";
		
		String rtp = "rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}";

		//String options = formatHttpStream(type, server, port, id);

		List<String> vlcArgs = new ArrayList<String>();
		
		vlcArgs.clear();
		
//		vlcArgs.add("--intf=dummy");
//		vlcArgs.add("--dummy-quiet");
	

		//vlcArgs.add("--sout=#" + rtp);
		
		//vlcArgs.add("--sout=#"+ rtp);
		
		vlcArgs.add("--sout=#duplicate{dst=" + rtp + ",dst=display}");
//
//		vlcArgs.add("--sout-ts-crypt-video");
//		vlcArgs.add("--sout-ts-crypt-audio");
//		
//		vlcArgs.add("--sout-ts-csa-ck=" + csaCK);
//		vlcArgs.add("--sout-ts-csa2-ck=" + csa2CK);

		// 4000ms = 4sec
		//vlcArgs.add("--file-caching=4000");

		vlcArgs.add("--no-sout-rtp-sap");
		vlcArgs.add("--no-sout-standard-sap");
		vlcArgs.add("--sout-all");
		vlcArgs.add("--sout-keep");
		vlcArgs.add("--no-plugins-cache");
//		vlcArgs.add("vlc://quit");
		
		
		
		// -------------------------------------------------
		// if true = odd
		if (isState()) {

			vlcArgs.add("--sout-ts-csa-use=1");
			String[] standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);
			
			// switch
			setState(false);
			
			runPlayer(inFileOdd, standardVlcOptions);
			
		}
		// -------------------------------------------------
		// if else = even
		else {

			vlcArgs.add("--sout-ts-csa-use=2");
			String[] standardVlcOptions = vlcArgs.toArray(new String[vlcArgs.size()]);
			
			// switch
			setState(true);
		
			runPlayer(inFileEven, standardVlcOptions);

			
		} // end if else
		
		//return null;
		
		
		
	
		
	}
	
	
	private static void runPlayer(String file, String[] standardMediaOptions) {
		
		mediaPlayerFactory = new MediaPlayerFactory(standardMediaOptions);
		
		//String meta = null;
		
		MediaMeta mediaMeta = mediaPlayerFactory.getMediaMeta(media, true);
		mediaMeta.setDate("555555");
		mediaMeta.save();
		//mediaPlayerFactory.getMediaMeta(media, true);
		mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
		
		
		
		
		mediaPlayer.setVolume(0);
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				System.out.println("playing: " + file.toString());
				
				
			}
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				System.out.println("finished: " + file.toString());
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayerFactory.release();
				//Thread.currentThread().notify();
				
				System.out.println("generatePlayer: ");
				generatePlayer();
//				Thread.currentThread().notify();
//				generatePlayer();
				
			}
		});
		
		mediaPlayer.playMedia(file);
		
		

//		try {
//			Thread.currentThread().wait();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * vlc -vvv input_stream --sout
	 * '#rtp{dst=192.168.0.12,port=1234,sdp=rtsp://server.example.org:8080/test.
	 * sdp}'
	 * 
	 * @param type
	 * @param serverAddress
	 * @param serverPort
	 * @param id
	 * @return
	 */
	private static String formatHttpStream(String type, String serverAddress, int serverPort, String id) {
		StringBuilder sb = new StringBuilder(200);

		// sb.append("--sout=#duplicate{dst=display,dst=rtp{");
		// sb.append(":sout=#duplicate{dst=rtp{");

		// sb.append("--sout=#"+ type );
		// sb.append("{dst=" + serverAddress);
		// sb.append(",port=" + serverPort);
		// sb.append(",mux=ts,name=cas}");

		// sb.append("--sout=#rtp{dst=192.168.178.101,port=1234,mux=ts,sdp=rtsp://192.168.178.101:8080/cas.sdp}");

		// sb.append("--sout=#rtp{dst=127.0.0.1,port=1234,mux=ts,sdp=rtsp://127.0.0.1:8080/cas.sdp}");

		//sb.append("--sout=#rtp{dst=127.0.0.1,port=1234,mux=ts,sdp=rtsp://192.168.178.101:8080/cas.sdp}");

		// sb.append("{dst=" + serverAddress);
		// sb.append(",port=" + serverPort);
		// sb.append(",mux=ts}");

		return sb.toString();
	}

	public static boolean isState() {
		return state;
	}

	public static void setState(boolean state) {
		StreamVideo.state = state;
	}
}