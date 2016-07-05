package tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @web http://java-buddy.blogspot.com/
 */
public class JavaTCPServer {

	private static int port;
	private static ServerSocket serverSocket;

	public static void main(String[] args) {
//		if (args.length != 1) {
//			System.out.println("usage: java -jar JavaTCPServer.jar <port>");
//			System.exit(1);
//		}

		//port = Integer.parseInt(args[0]);
		port = 80;
		System.out.println("Port: " + port);

		Socket socket = null;
		Scanner scanner = null;
		try {
			serverSocket = new ServerSocket(port);

			socket = serverSocket.accept();
			scanner = new Scanner(socket.getInputStream());
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

			String line = scanner.nextLine();

			System.out.println("received: " + line);
			printWriter.println("echo: " + line);
		} catch (IOException ex) {
			Logger.getLogger(JavaTCPServer.class.getName()).log(Level.SEVERE, null, ex);
		} finally {

			if (socket != null) {
				try {
					scanner.close();
					socket.close();
				} catch (IOException ex) {
					Logger.getLogger(JavaTCPServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		}

	}

}