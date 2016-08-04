package tests.vlc;

import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import model.ConfigModel;
import model.SimulatorModel;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

public class VLC_ProcessBuilder {

	private static SimulatorModel model;

	// Config Modell
	private static ConfigModel configModel;

	public static Thread thStreamVLC;

	public static HeadlessMediaPlayer mediaPlayer = null;

	static boolean state;

	static String vlcPath;

	static String inFileOdd;
	static String inFileEven;

	public static void main(String[] args) throws Exception {

		vlcPath = "C:\\ProgLoc\\VideoLAN\\VLC\\";

//		ProcessBuilder pb;
//		Process p = null;
//
//		String testFile = "D:\\Users\\Videos\\odd.mp4";
//		//String testFile = "D:\\Users\\Videos\\2014-11-22_ORF_BINGO_Edith-Oma.mp4";
//		//
//		pb = new ProcessBuilder(vlcPath + "vlc", "--intf=dummy", "--dummy-quiet", testFile, "--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}", "--sout-ts-crypt-video", "--sout-ts-crypt-audio",
//				"--sout-ts-csa-use=1", "--sout-ts-csa-ck=0123456789ABCDEF", "--sout-ts-csa2-ck=0123456789ABCDEF",
//				"--no-repeat", "--no-loop", "--ttl=1", "--no-sout-rtp-sap", "--no-sout-standard-sap", "--sout-all", "--sout-keep", "vlc://quit");
//
//		try {
//			p = pb.start();
//			p.waitFor();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		//Thread.sleep(20000);
//		p.destroy();

		initVLC();
		streamVLC();

	}

	public static void initVLC() {

		// model = SimulatorViewController.getModel();
		// configModel = ConfigViewController.getConfigModel();

		// vlcPath = configModel.getVlcPath();
		vlcPath = "C:\\ProgLoc\\VideoLAN\\VLC";

		// inFileOdd = model.getInputFile().getParent() + "\\odd.mp4";
		// inFileEven = model.getInputFile().getParent() + "\\even.mp4";

		inFileOdd = "D:\\Users\\Videos\\odd.mp4";
		inFileEven = "D:\\Users\\Videos\\even.mp4";

		// first init state true for odd
		setState(true);

	}

	public static void streamVLC() {

		// TODO
		// 1. init odd file
		// 2. switch
		// 3. even file
		// 4. switch
		// 5. odd file
		// 6. goto 2

		Task<Void> taskStreamVLC = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// GUI updaten
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						ProcessBuilder pb;
						Process p = null;

						// -------------------------------------------------
						// if true = odd
						if (isState()) {

							System.out.println("State odd");

							pb = new ProcessBuilder(vlcPath + "vlc", "--intf=dummy", "--dummy-quiet", inFileOdd, "--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}", "--sout-ts-crypt-video", "--sout-ts-crypt-audio",
									"--sout-ts-csa-use=1", "--sout-ts-csa-ck=0123456789ABCDEF", "--sout-ts-csa2-ck=0123456789ABCDEF",
									"--no-repeat", "--no-loop", "--ttl=1", "--no-sout-rtp-sap", "--no-sout-standard-sap", "--sout-all", "--sout-keep", "vlc://quit");

							try {
								p = pb.start();
								p.waitFor();
							} catch (IOException | InterruptedException e) {
								e.printStackTrace();
							}
							
							p.destroy();
							
							// switch
							setState(false);

						}
						// -------------------------------------------------
						// if else = even
						else {
							
							pb = new ProcessBuilder(vlcPath + "vlc", "--intf=dummy", "--dummy-quiet", inFileEven, "--sout=#rtp{proto=udp,mux=ts,dst=239.0.0.1,port=5004}", "--sout-ts-crypt-video", "--sout-ts-crypt-audio",
									"--sout-ts-csa-use=1", "--sout-ts-csa-ck=0123456789ABCDEF", "--sout-ts-csa2-ck=0123456789ABCDEF",
									"--no-repeat", "--no-loop", "--ttl=1", "--no-sout-rtp-sap", "--no-sout-standard-sap", "--sout-all", "--sout-keep", "vlc://quit");

							try {
								p = pb.start();
								p.waitFor();
							} catch (IOException | InterruptedException e) {
								e.printStackTrace();
							}
							
							p.destroy();
							
							// switch
							setState(true);

						}

					}
				});

				// Scrambling Control switch

				// } // end while
				return null;
			} // end call
		};

		// start the task
		thStreamVLC = new Thread(taskStreamVLC);
		thStreamVLC.setDaemon(true);
		thStreamVLC.start();

	}

	public static boolean isState() {
		return state;
	}

	public static void setState(boolean s) {
		state = s;
	}

}