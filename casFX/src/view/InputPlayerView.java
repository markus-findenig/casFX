package view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import controller.PlayerControlsPanel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class InputPlayerView {
	
	public static JFrame videoInputF;

	int width = 500;
	int height = 300;

	public InputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {
		
		videoInputF = new JFrame("Video Input Player");
		videoInputF.setAlwaysOnTop(true);
		videoInputF.setLayout(new BorderLayout());
		videoInputF.setSize(width, height);
		videoInputF.setLocation(10, 10);
		videoInputF.setContentPane(mediaPlayerComponent);
		videoInputF.add(controlsPanel, BorderLayout.SOUTH);
		videoInputF.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		videoInputF.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				videoInputF.setVisible(false);
				//PlayerViewController.thInitPlayerInput.stop();
				//embeddedMediaPlayer.stop();
				//videoInputF.dispose();
			}
		});
		videoInputF.setVisible(true);
		Canvas videoInputC = new Canvas();
		videoInputC.setBackground(Color.black);
		JPanel videoInputP = new JPanel();
		videoInputP.setLayout(new BorderLayout());
		videoInputP.add(videoInputC, BorderLayout.CENTER);
		videoInputF.add(videoInputP, BorderLayout.CENTER);
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
		
	}

}
