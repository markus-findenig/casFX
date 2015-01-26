package tests.aufgabe3.log;

import java.util.ArrayList;

/**
 * Logger
 * 
 * @author
 * 
 */
public class Logger implements Runnable {

	private ArrayList<LogEntry> entries;

	public Logger() {this.entries = new ArrayList<LogEntry>();}

	synchronized public void put(LogEntry entry) {
		this.entries.add(entry);
		notifyAll();
	}

	synchronized private void consume() throws Exception {
		while (this.entries.isEmpty()) {
			wait();
		}
		for (LogEntry entry : this.entries) {System.out.println(entry.print());}
		System.out.println("---Logger Asleep---");

		this.entries.clear();
		notifyAll();
	}

	@Override
	public void run() {
		try {
			doit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doit() throws Exception {
		while (true) {
			consume();
			//wait();
			Thread.sleep(500);
		}
	}
}
