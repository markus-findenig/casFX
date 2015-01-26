package tests;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.test.basic.PlayerControlsPanel;
import uk.co.caprica.vlcj.test.basic.PlayerVideoAdjustPanel;

import com.sun.jna.NativeLibrary;




public class Canvas_Demo {

  
    //Main function :
    public static void main(String[] args) {
    	
    	
        NativeLibrary.addSearchPath("libvlc", "D:\\ProgLoc\\VideoLAN\\VLC");

        
        EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        EmbeddedMediaPlayer embeddedMediaPlayer = mediaPlayerComponent.getMediaPlayer();

        Canvas videoSurface = new Canvas();
        videoSurface.setBackground(Color.black);
        videoSurface.setSize(800, 600);

        List<String> vlcArgs = new ArrayList<String>();

        vlcArgs.add("--no-plugins-cache");
        vlcArgs.add("--no-video-title-show");
        vlcArgs.add("--no-snapshot-preview");

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
        mediaPlayerFactory.setUserAgent("vlcj test player");
        embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
        embeddedMediaPlayer.setPlaySubItems(true);

        final PlayerControlsPanel controlsPanel = new PlayerControlsPanel(embeddedMediaPlayer);
        PlayerVideoAdjustPanel videoAdjustPanel = new PlayerVideoAdjustPanel(embeddedMediaPlayer);

//            mediaPlayerComponent.getMediaPlayer().playMedia(Constant.PATH_ROOT + Constant.PATH_MEDIA + "tmp.mp4");
        JFrame mainFrame = new JFrame();
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setBackground(Color.black);
        mainFrame.add(videoSurface, BorderLayout.CENTER);
        mainFrame.add(controlsPanel, BorderLayout.SOUTH);
        mainFrame.add(videoAdjustPanel, BorderLayout.EAST);

        //create a button which will hide the panel when clicked.
        mainFrame.pack();
        mainFrame.setVisible(true);

        embeddedMediaPlayer.playMedia("E:\\Users\\Videos\\Test\\TheSimpsonsMovie-1080pTrailer.mp4");
        

    }

}