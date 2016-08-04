package tests.socket;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * http://www.csee.umbc.edu/~pmundur/courses/CMSC691C/lab5-kurose-ross.html
 * 
 * 
 * @author Titan
 *
 */

public class Server {

	public final static int SOCKET_PORT = 8554; // you may change this
	public final static String FILE_TO_SEND = "D:\\Users\\Videos\\Test\\TheSimpsonsMovie1080pTrailer.mp4"; // you

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
	    ServerSocket servsock = null;
	    Socket sock = null;
	    try {
	      servsock = new ServerSocket(SOCKET_PORT);
	      while (true) {
	        System.out.println("Waiting...");
	        try {
	          sock = servsock.accept();
	          System.out.println("Accepted connection : " + sock);
	          // send file
	          File myFile = new File (FILE_TO_SEND);
	          byte [] mybytearray  = new byte [(int)myFile.length()];
	          fis = new FileInputStream(myFile);
	          bis = new BufferedInputStream(fis);
	          bis.read(mybytearray,0,mybytearray.length);
	          os = sock.getOutputStream();
	          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
	          os.write(mybytearray,0,mybytearray.length);
	          os.flush();
	          System.out.println("Done.");
	        }
	        finally {
	          if (bis != null) bis.close();
	          if (os != null) os.close();
	          if (sock!=null) sock.close();
	        }
	      }
	    }
	    finally {
	      if (servsock != null) servsock.close();
	    }
	}
}