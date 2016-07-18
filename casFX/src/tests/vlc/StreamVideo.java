package tests.vlc;

import java.util.ArrayList;
import java.util.List;

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
public class StreamVideo extends VlcjTest {

	// Stream RTP

	static String media = "D:\\Users\\Videos\\2014-11-22_ORF_BINGO_Edith-Oma.mp4 ";

	public static void main(String[] args) throws Exception {

		NativeLibrary.addSearchPath("libvlc", "C:/ProgLoc/VideoLAN/VLC");

		// String media = args[0];
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

		// 4000ms = 4sec
		vlcArgs.add("--file-caching=4000");

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

		// Don't exit
		Thread.currentThread().join();
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

		sb.append("--sout=#rtp{dst=127.0.0.1,port=1234,mux=ts,sdp=rtsp://192.168.178.101:8080/cas.sdp}");

		// sb.append("{dst=" + serverAddress);
		// sb.append(",port=" + serverPort);
		// sb.append(",mux=ts}");

		return sb.toString();
	}
}