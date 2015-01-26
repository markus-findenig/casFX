package tests.aufgabe3.server;

import java.io.*;
import java.net.Socket;
import java.util.Date;

import tests.aufgabe3.log.LogEntry;
import tests.aufgabe3.log.Logger;

/**
 * Server Service
 * 
 * @author
 * 
 */
public class Service implements Runnable {

	private Socket socket;
	private String documentRoot;
	private Logger logger;

	public Service(Socket serverSocket, String basicFilePath, Logger logger) {
		this.socket = serverSocket;
		this.documentRoot = basicFilePath;
		this.logger = logger;
	}

	@Override
	public void run() {
		try {
			doit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doit() throws Exception {
		// FROM CLIENT
		BufferedReader fromClient = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		String clientRequest = fromClient.readLine();

		// LOG IT
		logger.put(new LogEntry(new Date(), clientRequest, this.socket
				.getInetAddress(), this.socket.getPort(), Thread
				.currentThread().getName()));

		String[] requestArgs = clientRequest.split(" ");
		String reqFilePath = this.documentRoot + requestArgs[1];

		File reqFile = new File(reqFilePath);
		if (reqFile.isDirectory()) {
			reqFilePath += "index.html";
			reqFile = new File(reqFilePath);
		}

		if (reqFile.exists()) {
			FileInputStream fis = new FileInputStream(reqFile);
			byte[] fileContent = new byte[fis.available()];
			fis.read(fileContent, 0, fileContent.length);
			fis.close();

			// TO CLIENT
			DataOutputStream toClient = new DataOutputStream(
					socket.getOutputStream());
			toClient.write(fileContent);
		}
		socket.close();
	}

}
