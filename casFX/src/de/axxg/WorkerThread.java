package de.axxg;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.ListView;

/**
 *
 * @author AxxG
 * @see http://blog.axxg.de/
 */
public class WorkerThread extends Thread{
    
    private ListView<String> list = null;

    public WorkerThread(ListView<String> list, int count) {
        setDaemon(true);
        setName("Thread " + count);
        this.list = list;
    }

    @Override
    public void run() {

        while (!this.isInterrupted()) {
            
            // UI updaten
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    // entsprechende UI Komponente updaten
                    list.getItems().add(0, getName() + " sagt Hallo!");
                }
            });

            // Thread schlafen
            try {
                // fuer 3 Sekunden
                sleep(TimeUnit.SECONDS.toMillis(3));
            } catch (InterruptedException ex) {
                Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

}
