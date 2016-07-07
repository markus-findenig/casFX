package tests.socket;

import javafx.application.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MoviePlayer extends Application
{

    public static void main(String[] args)
    {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception
    {
        Group root = new Group();
// http://127.0.0.1:7777/TheSimpsonsMovie-1080pTrailer.mp4
        Media media = new Media("http://127.0.0.1:7777/");
        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);

        root.getChildren().add(view);
        Scene scene = new Scene(root,400,400,Color.BLACK);

        stage.setScene(scene);
        stage.show();

        player.play();

    }

}