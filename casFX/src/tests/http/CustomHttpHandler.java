package tests.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomHttpHandler implements HttpHandler {
	private String rootDirectory;

	public CustomHttpHandler(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	public void handle(HttpExchange httpExchange) throws IOException {
		URI uri = httpExchange.getRequestURI();
		File file = new File(rootDirectory + uri.getPath()).getCanonicalFile();

		Headers responseHeaders = httpExchange.getResponseHeaders();

		if (uri.toString().contains(".ts")) {
			responseHeaders.set("Content-Type", "video/MP2T");
		} else {
			responseHeaders.set("Content-Type", "application/vnd.apple.mpegurl");
		}

		if (file.exists()) {
			byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
			httpExchange.sendResponseHeaders(200, 0);

			OutputStream outputStream = httpExchange.getResponseBody();
			outputStream.write(bytes);
			outputStream.close();
		}
	}
}
