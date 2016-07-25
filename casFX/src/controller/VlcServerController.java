package controller;

import java.util.ArrayList;
import java.util.List;

import model.ConfigModel;
import model.SimulatorModel;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

public class VlcServerController {

	private static SimulatorModel model;

	// Config Modell
	private static ConfigModel configModel;

	/**
	 * Streamt die angegebene Datei mittels RTP
	 * @param outfile Datei zum streamen
	 */
	public static void streamVlcFile(String outfile) {

		model = SimulatorViewController.getModel();
		configModel = ConfigViewController.getConfigModel();

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
		HeadlessMediaPlayer mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
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

}
