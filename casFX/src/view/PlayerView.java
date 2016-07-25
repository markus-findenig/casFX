package view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.PlayerControlsPanel;
import controller.PlayerViewController;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class PlayerView {

	private static JFrame videoInputF;

	private static JFrame videoOutputF;

	/**
	 * Video Player Output
	 * 
	 * @param embeddedMediaPlayer
	 * @param mediaPlayerFactory
	 * @param mediaPlayerComponent
	 * @param controlsPanel
	 * @return PlayerView
	 */
	public PlayerView(String type, EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {

		int width = 500;
		int height = 300;

		if (type == "Output") {
			videoOutputF = new JFrame("Video Output Player");
			videoOutputF.setLayout(new BorderLayout());
			videoOutputF.setSize(width, height);
			videoOutputF.setLocation(600, 10);
			videoOutputF.setContentPane(mediaPlayerComponent);
			videoOutputF.add(controlsPanel, BorderLayout.SOUTH);
			videoOutputF.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			videoOutputF.addWindowListener(new java.awt.event.WindowAdapter() {
				@SuppressWarnings("deprecation")
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					embeddedMediaPlayer.stop();
					videoOutputF.dispose();
					PlayerViewController.thInitPlayerOutput.stop();
				}
			});
			videoOutputF.setVisible(true);
			Canvas videoOutputC = new Canvas();
			videoOutputC.setBackground(Color.black);
			JPanel videoOutputP = new JPanel();
			videoOutputP.setLayout(new BorderLayout());
			videoOutputP.add(videoOutputC, BorderLayout.CENTER);
			videoOutputF.add(videoOutputP, BorderLayout.CENTER);
			embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoOutputC));
			// disable player view
			//videoOutputF.setVisible(false);

		} else if (type == "Input") {
			videoInputF = new JFrame("Video Input Player");
			videoInputF.setLayout(new BorderLayout());
			videoInputF.setSize(width, height);
			videoInputF.setLocation(10, 10);
			videoInputF.setContentPane(mediaPlayerComponent);
			videoInputF.add(controlsPanel, BorderLayout.SOUTH);
			videoInputF.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			videoInputF.addWindowListener(new java.awt.event.WindowAdapter() {
				@SuppressWarnings("deprecation")
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					embeddedMediaPlayer.stop();
					videoInputF.dispose();
					PlayerViewController.thInitPlayerInput.stop();
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
			// disable player view
			//videoInputF.setVisible(false);
		}

	}

//	public void showOutputPlayer() {
//		videoOutputF.setVisible(true);
//	}
//
//	public void showInputPlayer() {
//		videoInputF.setVisible(true);
//	}

}
