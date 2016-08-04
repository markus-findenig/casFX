package tests;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * http://www.programcreek.com/java-api-examples/index.php?api=javafx.scene.media.MediaPlayer
 * @author Titan
 *
 */
public class Server {
	public static void main(String args[]) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(8888);

        /**
         * Formula for lag = (byte_size/sample_rate)*2
         * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
         * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
         * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
         */

        System.out.println("Starting Server.");

        Server http = new Server();
        http.registerRoom();

        Thread thread = new Thread(){
            @Override
			public void run() {
                JFXPanel fxPanel = new JFXPanel();
                System.out.println("Thread Running");
                //Media hit = new Media(Paths.get("resorces/dummy.mp4").toUri().toString());
                Media hit = new Media(Paths.get("D://Users//Videos//Test//TheSimpsonsMovie-1080pTrailer.mp4").toUri().toString());
                
                MediaPlayer mediaPlayer = new MediaPlayer(hit);
                mediaPlayer.play();
            }
        };

        thread.start();

        byte[] receiveData = new byte[1100];//1280

        float sampleRate = 128;
		AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		javax.sound.sampled.DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(format);
        sourceDataLine.start();

        FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(1.00f);

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        ByteArrayInputStream bias = new ByteArrayInputStream(receivePacket.getData());

        //Reducing the volume of the stream
        //FloatControl volume = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        //volume.setValue(-10.0F);

        boolean status = true;
		while (status) {
            serverSocket.receive(receivePacket);
            AudioInputStream ais = new AudioInputStream(bias, format, receivePacket.getLength());

            toSpeaker(receivePacket.getData());
        }
        sourceDataLine.drain();
        sourceDataLine.close();
    }

	private static void toSpeaker(byte[] data) {
		// TODO Auto-generated method stub
		
	}

	private void registerRoom() {
		// TODO Auto-generated method stub
		
	}
}
