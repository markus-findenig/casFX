package tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @web http://java-buddy.blogspot.com/
 */
public class JavaTCPClient {

	public static void main(String[] args) {
//		if (args.length != 2) {
//			System.out.println("usage: java -jar JavaTCPClient.jar <IP address> <port>");
//			System.exit(1);
//		}
		
		String ip = "127.0.0.1";
		

		Socket socket = null;
		try {
			//InetAddress inetAddress = InetAddress.getByName(args[0]);
			InetAddress inetAddress = InetAddress.getByName(ip);
			//int port = Integer.parseInt(args[1]);
			int port = 80;

			socket = new Socket(inetAddress, port);
			System.out.println("InetAddress: " + inetAddress);
			System.out.println("Port: " + port);

			Scanner scanner = new Scanner(socket.getInputStream());
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

			Scanner userScanner = new Scanner(System.in);
			String userInput = userScanner.nextLine();

			printWriter.println(userInput);
			String serverEcho = scanner.nextLine();
			System.out.println(serverEcho);

		} catch (UnknownHostException ex) {
			Logger.getLogger(JavaTCPClient.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(JavaTCPClient.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ex) {
					Logger.getLogger(JavaTCPClient.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

}