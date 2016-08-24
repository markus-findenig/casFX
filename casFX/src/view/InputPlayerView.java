package view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import controller.SimulatorViewController;

/**
 * Input Media Player View.
 */
public class InputPlayerView {

	/**
	 * Input Media Player View Frame.
	 */
	private static JFrame videoInputF;

	/**
	 * Input Media Player View Canvas.
	 */
	private static Canvas videoInputC;

	/**
	 * Input Media Player View JPanel.
	 */
	private static JPanel videoInputP;

	/**
	 * Input Media Player View width.
	 */
	private static int width = 500;

	/**
	 * Input Media Player View height.
	 */
	private static int height = 300;

	/**
	 * Generate a input Media Player View without controls Component.
	 * @param mediaPlayer Media Player
	 * @param mediaPlayerFactory Media Player Factory
	 */
	public InputPlayerView(EmbeddedMediaPlayer mediaPlayer, MediaPlayerFactory mediaPlayerFactory) {
		// mainFrame
		videoInputF = new JFrame("Video Input Player");
		videoInputF.setAlwaysOnTop(true);
		videoInputF.setLayout(new BorderLayout());
		videoInputF.setSize(width, height);
		videoInputF.setLocation(10, 10);

		videoInputF.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		videoInputF.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				videoInputF.setVisible(false);
			}
		});

		videoInputF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				SimulatorViewController.getPlayers().get(0).mediaPlayer().setVolume(0);
			}
		});

		// Video Anzeige
		videoInputC = new Canvas();
		videoInputC.setBackground(Color.black);

		// Video ins Panel
		videoInputP = new JPanel();
		videoInputP.setLayout(new BorderLayout());
		videoInputP.add(videoInputC, BorderLayout.CENTER);

		// Video Panel ins Frame
		videoInputF.add(videoInputP, BorderLayout.CENTER);
		videoInputF.setVisible(true);

		// Player Anzeige ins Canvas
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
	}

	/**
	 * Updates the current input Media Player View.
	 * 
	 * @param mediaPlayer
	 *            Media Player
	 * @param mediaPlayerFactory
	 *            Media Player Factory
	 */
	public void reInitInputPlayerView(EmbeddedMediaPlayer mediaPlayer, MediaPlayerFactory mediaPlayerFactory) {
		// Player Anzeige
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
	}

	/**
	 * @return The current frame {@link videoInputF} of the input Media Player View.
	 */
	public static JFrame getVideoInputF() {
		return videoInputF;
	}

}
