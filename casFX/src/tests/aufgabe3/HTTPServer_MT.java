package tests.aufgabe3;

import java.net.*;

import tests.aufgabe3.log.Logger;
import tests.aufgabe3.server.Service;

/**
 * Ü 4.3 Java ± Multi-Threaded HTTP Server - PFLICHTABGABE Erweitern Sie die
 * Funktionalität des Servers aus Ü 4.2 wie folgt. Anstatt die Anfragen von
 * Clients seriell in einem Thread zu behandeln, soll jede Anfrage durch einen
 * neuen Thread behandelt werden. Weiters ist der Server durch eine
 * Logging-Komponente zu erweitern. Jeder Zugriff auf den Server (Zeitpunkt,
 * Request, IP und Port des Clients) soll dabei mitprotokolliert werden. Dieses
 * Logging soll zentral durch einen eigenen Thread realisiert werden, der in
 * Abständen von 5 Sekunden das Zugriffsprotokoll auf die Konsole schreibt. Die
 * Zugriffe müssen daher mit geeigneten Mechanismen von den behandelnden Threads
 * an diesen Logging- Thread kommuniziert werden. Sorgen Sie für eine geeignete
 * Synchronisation der Threads.
 * 
 */
public class HTTPServer_MT {
	/**
	 * argv[0] ... port (default 80)
	 * argv[1] ... documentroot (default "documentRoot")
	 */
	public static void main(String argv[]) throws Exception {
		
		int port = 80;
		String documentroot = "D://Users//Dropbox//workspace//11W//RN_blatt_4_3//documentRoot";

		//ServerSocket listenSocket = new ServerSocket(Integer.valueOf(argv[0]));
		ServerSocket listenSocket = new ServerSocket(port);

		Logger logger = new Logger();
		new Thread(logger).start();

		while (true) {
			Socket serverSocket = listenSocket.accept();
			//Service socket = new Service(serverSocket, argv[1], logger);
			Service socket = new Service(serverSocket, documentroot, logger);
			new Thread(socket).start();
		}
	}
}
