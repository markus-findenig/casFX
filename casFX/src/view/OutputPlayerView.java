package view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import controller.PlayerControlsPanel;

/**
 * Output Media Player View.
 */
public class OutputPlayerView {

	/**
	 * Output Media Player View Frame.
	 */
	private JFrame videoOutputF;

	/**
	 * Output Media Player View Canvas.
	 */
	private Canvas videoOutputC;

	/**
	 * Output Media Player View JPanel.
	 */
	private JPanel videoOutputP;

	/**
	 * Output Media Player View width.
	 */
	private static int width = 500;

	/**
	 * Output Media Player View height.
	 */
	private static int height = 300;

	/**
	 * Generate a output Media Player View.
	 * 
	 * @param embeddedMediaPlayer
	 *            Media Player
	 * @param mediaPlayerFactory
	 *            Media Player Factory
	 * @param mediaPlayerComponent
	 *            Media Player Component
	 * @param controlsPanel
	 *            Media Player control Panel
	 */
	public OutputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {
		videoOutputF = new JFrame("Video Output Player");
		videoOutputF.setAlwaysOnTop(true);
		videoOutputF.setLayout(new BorderLayout());
		videoOutputF.setSize(width, height);
		videoOutputF.setLocation(600, 10);
		videoOutputF.setContentPane(mediaPlayerComponent);
		videoOutputF.add(controlsPanel, BorderLayout.SOUTH, 1);
		videoOutputF.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		videoOutputF.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				controlsPanel.updateVolume(0);
				videoOutputF.setVisible(false);
			}
		});
		// Video Anzeige
		videoOutputC = new Canvas();
		videoOutputC.setBackground(Color.black);
		// Video ins Panel
		videoOutputP = new JPanel();
		videoOutputP.setLayout(new BorderLayout());
		videoOutputP.add(videoOutputC, BorderLayout.CENTER);
		// Video Panel ins Frame
		videoOutputF.add(videoOutputP, BorderLayout.CENTER);
		videoOutputF.setVisible(true);
		// controlsPanel.updateVolume(0);
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoOutputC));

	}

	/**
	 * Updates the current Output Media Player View.
	 * 
	 * @param embeddedMediaPlayer
	 *            Media Player
	 * @param mediaPlayerFactory
	 *            Media Player Factory
	 * @param mediaPlayerComponent
	 *            Media Player Component
	 * @param controlsPanel
	 *            Media Player control Panel
	 */
	public void reInitOutputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {
		// update controls
		videoOutputF.setContentPane(mediaPlayerComponent);
		videoOutputF.remove(controlsPanel);
		videoOutputF.add(controlsPanel, BorderLayout.SOUTH, 1);
		// update player
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoOutputC));
	}

}
