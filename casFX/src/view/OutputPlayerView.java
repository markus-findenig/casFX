package view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import controller.PlayerControlsPanel;
import controller.PlayerViewController;
import model.SimulatorModel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class OutputPlayerView {

	private static JFrame videoOutputF;

	private static Canvas videoOutputC;

	int width = 500;
	int height = 300;

	public OutputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {

		videoOutputF = new JFrame("Video Output Player");
		videoOutputF.setAlwaysOnTop(true);
		videoOutputF.setLayout(new BorderLayout());
		videoOutputF.setSize(width, height);
		videoOutputF.setLocation(600, 10);
		videoOutputF.setContentPane(mediaPlayerComponent);
		videoOutputF.add(controlsPanel, BorderLayout.SOUTH);
		videoOutputF.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		videoOutputF.addWindowListener(new java.awt.event.WindowAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				PlayerViewController.exitOutputPlayerView();
				
				// PlayerViewController.thInitPlayerOutput.stop();
				//SimulatorModel.setDecryptionState(false);
				embeddedMediaPlayer.stop();
				embeddedMediaPlayer.release();
				mediaPlayerFactory.release();
				videoOutputF.dispose();

			}
		});
		videoOutputF.setVisible(true);
		videoOutputC = new Canvas();
		videoOutputC.setBackground(Color.black);
		JPanel videoOutputP = new JPanel();
		videoOutputP.setLayout(new BorderLayout());
		videoOutputP.add(videoOutputC, BorderLayout.CENTER);
		videoOutputF.add(videoOutputP, BorderLayout.CENTER);
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoOutputC));
	}

	public void reInitOutputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {
		
		videoOutputF = new JFrame("Video Output Player");
		videoOutputF.setLayout(new BorderLayout());
		videoOutputF.setSize(width, height);
		videoOutputF.setLocation(600, 10);
		videoOutputF.setContentPane(mediaPlayerComponent);
		videoOutputF.add(controlsPanel, BorderLayout.SOUTH);
		videoOutputF.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		videoOutputF.addWindowListener(new java.awt.event.WindowAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				// PlayerViewController.thInitPlayerOutput.stop();
				embeddedMediaPlayer.stop();
				videoOutputF.dispose();

			}
		});
		videoOutputF.setVisible(true);
		videoOutputC = new Canvas();
		videoOutputC.setBackground(Color.black);
		JPanel videoOutputP = new JPanel();
		videoOutputP.setLayout(new BorderLayout());
		videoOutputP.add(videoOutputC, BorderLayout.CENTER);
		videoOutputF.add(videoOutputP, BorderLayout.CENTER);
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoOutputC));

	}
}
