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
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

public class InputPlayerView {
	
	private static JFrame videoInputF;
	
	private static Canvas videoInputC;
	
	private static JPanel videoInputP;

	private static int width = 500;
	private static int height = 300;

	public InputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {
		
		//controlsPanel.updateVolume(0);
		
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
				controlsPanel.updateVolume(0);
				videoInputF.setVisible(false);
				//PlayerViewController.thInitPlayerInput.stop();
				//embeddedMediaPlayer.stop();
				//videoInputF.dispose();
			}
		});
		videoInputF.setVisible(true);
		videoInputC = new Canvas();
		videoInputC.setBackground(Color.black);
		
		videoInputP = new JPanel();
		videoInputP.setLayout(new BorderLayout());
		videoInputP.add(videoInputC, BorderLayout.CENTER);
		videoInputF.add(videoInputP, BorderLayout.CENTER);
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
		
	}
	
	public InputPlayerView(MediaListPlayer mediaListPlayer, MediaPlayerFactory factory) {
		// TODO Auto-generated constructor stub
	}

	public void reInitInputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {
		
		//controlsPanel.updateVolume(0);
		
		//videoOutputF = new JFrame("Video Output Player");
//		videoInputF.setAlwaysOnTop(true);
//		videoInputF.setLayout(new BorderLayout());
//		videoInputF.setSize(width, height);
//		videoInputF.setLocation(10, 10);
		videoInputF.setContentPane(mediaPlayerComponent);
//		videoInputF.removeAll();
		videoInputF.add(controlsPanel, BorderLayout.SOUTH);
//		videoInputF.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		videoInputF.addWindowListener(new java.awt.event.WindowAdapter() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
//				controlsPanel.updateVolume(0);
//				videoInputF.setVisible(false);
//				
//				// PlayerViewController.thInitPlayerOutput.stop();
//				//embeddedMediaPlayer.stop();
//				//videoInputF.dispose();
//
//			}
//		});
//		videoInputF.setVisible(true);
//		//videoOutputC = new Canvas();
//		videoInputC.setBackground(Color.black);
//		//videoOutputP = new JPanel();
//		videoInputP.setLayout(new BorderLayout());
//		videoInputP.removeAll();
//		videoInputP.add(videoInputC, BorderLayout.CENTER);
//		videoInputF.add(videoInputP, BorderLayout.CENTER);
		
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
	}

	public static JFrame getVideoInputF() {
		return videoInputF;
	}

	
}
