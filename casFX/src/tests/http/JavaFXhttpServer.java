package tests.http;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class JavaFXhttpServer {

	public static void main(String[] args) {
	
	// Creates a server on localhost, port 7777, runs on background thread
	// Note that Media does not recognize localhost, you'll have to use 127.0.0.1
	HttpServer httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 7777), 0);
	httpServer.createContext("/", new CustomHttpHandler("/dir/to/files/to/play"));
	httpServer.start();
	
	
	}
}
