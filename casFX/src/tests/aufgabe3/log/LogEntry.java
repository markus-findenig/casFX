package tests.aufgabe3.log;

import java.net.InetAddress;
import java.util.Date;

/**
 * Logger Entry
 * 
 * @author 
 *
 */
public class LogEntry {

	private Date date;
	private String request;
	private InetAddress clientIP;
	private int port;
	private String threadName;

	public LogEntry(Date date, String request, InetAddress clientIP, int port,
			String threadName) {
		super();
		this.date = date;
		this.request = request;
		this.clientIP = clientIP;
		this.port = port;
		this.threadName = threadName;
	}

	public Date getDate() {
		return date;
	}

	public String getRequest() {
		return request;
	}

	public InetAddress getClientIP() {
		return clientIP;
	}

	public int getPort() {
		return port;
	}

	public String getThreadName() {
		return this.threadName;
	}

	public String print() {
		return getDate() + " " + getRequest() + " "
				+ getClientIP().getHostAddress() + " " + getPort()
				+ " Served By: " + getThreadName();
	}

}
