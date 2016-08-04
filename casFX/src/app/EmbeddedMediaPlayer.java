/*
 * Copyright (c) 2012, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package app;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class EmbeddedMediaPlayer extends Application {

    //private static final String MEDIA_URL = "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";

    //private File file = new File("D:\\Users\\Videos\\Test\\TheSimpsonsMovie1080pTrailer.mp4");
    
	//private File file = new File("D:\\Users\\Videos\\Test\\stream.mp4");
	private File file = new File("D:\\Users\\Videos\\odd.mp4");
	
	private final String MEDIA_URL = file.toURI().toString();
	
	//private final String MEDIA_URL = "http://239.0.0.1:5004";
	
	// Note the 127.0.0.1 here, localhost will NOT work!
	//Media myMedia = new Media("http://127.0.0.1:7777/something.m3u8")
		
    @Override
    public void start(Stage primaryStage) {
    	
    	        
        primaryStage.setTitle("Embedded Media Player");
        Group root = new Group();
        Scene scene = new Scene(root, 450, 250);

        // create media player
        Media media = new Media (MEDIA_URL);
        
        
        media.getDuration();
       
        
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        
        
        MediaControl mediaControl = new MediaControl(mediaPlayer);
        scene.setRoot(mediaControl);
        System.out.println("media.getDuration()" + media.getDuration());
        //mediaPlayer.play();

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        
        
//        mediaPlayer.setOnReady(new Runnable() {
//			@Override
//			public void run() {
//				int w = mediaPlayer.getMedia().getWidth();
//				int h = mediaPlayer.getMedia().getHeight();
//				
//				primaryStage.setMaxWidth(400);
//				primaryStage.setMaxHeight(200);
//			}
//        	
//        });
        
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
