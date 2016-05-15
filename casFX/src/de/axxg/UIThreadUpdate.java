package de.axxg;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author AxxG
 * @see http://blog.axxg.de/
 */
public class UIThreadUpdate extends Application {
    
    private ListView<String> list;
    private int count = 0;
    
    
    @Override
    public void start(Stage primaryStage) {
        
        // Java 7 Style;-)
        list = new ListView<>();
        list.setPrefWidth(200);

        // Button erstellen
        Button btn = new Button();
        btn.setText("Starte einen Thread");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WorkerThread myThread = new WorkerThread(list, count);
                myThread.start();
                
                count++;
            }
        });
        
        
        // Layout der Anwendung
        VBox myBox = new VBox();
        // Komponenten dem Layout hinzufuegen
        myBox.setAlignment(Pos.CENTER);
        myBox.getChildren().add(btn);
        myBox.getChildren().add(list);
        
        // alles zusammensetzen
        StackPane root = new StackPane();
        root.getChildren().add(myBox);   
        Scene scene = new Scene(root, 300, 250);
        
        // Stage konfiguieren
        primaryStage.setTitle("AxxG - UI Thread Update");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
