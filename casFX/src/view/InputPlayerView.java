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
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import controller.PlayerControlsPanel;
import controller.SimulatorViewController;

/**
 * Input Player View
 */
public class InputPlayerView {
	
	/**
	 * Input Player View Frame
	 */
	private static JFrame videoInputF;
	
	/**
	 * Input Player View Canvas
	 */
	private static Canvas videoInputC;
	
	/**
	 * Input Player View JPanel
	 */
	private static JPanel videoInputP;

	/**
	 * Input Player View width
	 */
	private static int width = 500;
	
	/**
	 * Input Player View height
	 */
	private static int height = 300;

//	/**
//	 * Erzeugt eine Input Player View
//	 * @param embeddedMediaPlayer - Media Player
//	 * @param mediaPlayerFactory - Media Player Factory
//	 * @param mediaPlayerComponent - Media Player Component
//	 * @param controlsPanel - Media Player control Panel
//	 */
//	public InputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
//			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {
//		videoInputF = new JFrame("Video Input Player");
//		videoInputF.setVisible(true);
//		videoInputF.setAlwaysOnTop(true);
//		videoInputF.setLayout(new BorderLayout());
//		videoInputF.setSize(width, height);
//		videoInputF.setLocation(10, 10);
//		//videoInputF.setContentPane(mediaPlayerComponent);
//		
//		videoInputF.add(controlsPanel, BorderLayout.SOUTH);
//		videoInputF.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		videoInputF.addWindowListener(new java.awt.event.WindowAdapter() {
//			@Override
//			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
//				//controlsPanel.updateVolume(0);
//				videoInputF.setVisible(false);
//			}
//		});
//		
//		videoInputF.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//               SimulatorViewController.getPlayers().get(0).mediaPlayer().pause();
//            }
//		});
//		
//		
//		
//		
//		videoInputC = new Canvas();
//		videoInputC.setBackground(Color.black);
//		
//		
//		
//		
////		videoInputP.add(videoInputC, BorderLayout.CENTER);
//		
//		//SimulatorViewController.getPlayers().get(0).mediaPlayer().setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
//		
//		//controlsPanel.updateVolume(0);
//		//embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
//		
//		SimulatorViewController.getPlayers().get(0).mediaPlayer().setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
////		
////		videoInputP.add(SimulatorViewController.getPlayers().get(0).videoSurface(), BorderLayout.CENTER);
//		
//		videoInputP = new JPanel();
//		videoInputP.setLayout(new BorderLayout());
//		videoInputP.add(SimulatorViewController.getPlayers().get(0).videoSurface());
//		
//		videoInputF.add(videoInputP, BorderLayout.CENTER);
//		
//		
//		
//	}
	
	/**
	 * Erzeugt eine Input Player View
	 * @param embeddedMediaPlayer - Media Player
	 * @param mediaPlayerFactory - Media Player Factory
	 * @param mediaPlayerComponent - Media Player Component
	 * @param controlsPanel - Media Player control Panel
	 */
	public InputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {
		
		// mainFrame
		videoInputF = new JFrame("Video Input Player");
		videoInputF.setAlwaysOnTop(true);
		videoInputF.setLayout(new BorderLayout());
		videoInputF.setSize(width, height);
		videoInputF.setLocation(10, 10);
		
		videoInputF.setContentPane(mediaPlayerComponent);
		videoInputF.add(controlsPanel, BorderLayout.SOUTH);
		
		videoInputF.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		videoInputF.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				controlsPanel.updateVolume(0);
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
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
		
		
	}
	
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
	
	public void reInitInputPlayerView(EmbeddedMediaPlayer mediaPlayer, MediaPlayerFactory mediaPlayerFactory) {
		// Player Anzeige ins Canvas
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));

	}

	/**
	 * Aktualisiert die aktuelle Input Player View
	 * @param embeddedMediaPlayer - Media Player
	 * @param mediaPlayerFactory - Media Player Factory
	 * @param mediaPlayerComponent - Media Player Component
	 * @param controlsPanel - Media Player control Panel
	 */
	public void reInitInputPlayerView(EmbeddedMediaPlayer embeddedMediaPlayer, MediaPlayerFactory mediaPlayerFactory,
			EmbeddedMediaPlayerComponent mediaPlayerComponent, PlayerControlsPanel controlsPanel) {

		//videoOutputF = new JFrame("Video Output Player");
//		videoInputF.setAlwaysOnTop(true);
//		videoInputF.setLayout(new BorderLayout());
//		videoInputF.setSize(width, height);
//		videoInputF.setLocation(10, 10);
		//videoInputF.remove(mediaPlayerComponent);
		
		
		
		videoInputF.setContentPane(mediaPlayerComponent);
//		videoInputF.remove(controlsPanel);
		//videoInputF.add(controlsPanel, BorderLayout.SOUTH);
		
		
		
		
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
		
		//controlsPanel.updateVolume(0);
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoInputC));
		
		
		
		
	}

	/**
	 * Liefert das aktuelle Frame {@link videoInputF} von der Input Player View.
	 * @return Gibt das aktuelle Frame zurück.
	 */
	public static JFrame getVideoInputF() {
		return videoInputF;
	}



	
}
