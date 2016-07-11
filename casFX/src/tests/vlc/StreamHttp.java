package tests.vlc;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.test.VlcjTest;

/* An example of how to stream a media file over HTTP.
* <p>
* The client specifies an MRL of <code>http://127.0.0.1:5555</code>
*/
public class StreamHttp extends VlcjTest {

	static String media = "D:\\Users\\Videos\\Test\\TheSimpsonsMovie1080pTrailer.mp4";

	public static void main(String[] args) throws Exception {
		// if(args.length != 1) {
		// System.out.println("Specify a single MRL to stream");
		// System.exit(1);
		// }

		NativeLibrary.addSearchPath("libvlc", "C:/ProgLoc/VideoLAN/VLC");

		// String media = args[0];
		String server = "127.0.0.1";
		int port = 5555;
		String casCK = "0123456789ABCDEF";
		
		String options = formatHttpStream(server, port, casCK);

		System.out.println("Streaming '" + media + "' to '" + options + "'");

		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(media);
		HeadlessMediaPlayer mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
		
		
		
		mediaPlayer.playMedia(media, options);

		// Don't exit
		Thread.currentThread().join();
	}

	private static String formatHttpStream(String serverAddress, int serverPort, String casCK) {
		StringBuilder sb = new StringBuilder(200);
//		 sb.append("--sout=#duplicate{dst=std{access=http,mux=ts,");
//		 sb.append("dst=");
//		 sb.append(serverAddress);
//		 sb.append(':');
//		 sb.append(serverPort);
//		 sb.append("}} ");
	
		sb.append("--sout=#duplicate{dst=display,dst=rtp{");
		sb.append("dst=");
		sb.append(serverAddress);
		sb.append(",port=");
		sb.append(serverPort);
		sb.append(",mux=ts,sap,name=cas}} ");
		sb.append("--sout-ts-crypt-video --sout-ts-crypt-audio ");
		sb.append("--sout-ts-csa-ck=" + casCK);
		
		
		
		return sb.toString();
	}
}