package tests.rtp2;

import java.io.*;
import java.util.Vector;
import java.net.InetAddress;
import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.control.*;

/**
 * 
 * https://jan.newmarch.name/internetdevices/jmf/lecture.html
 *
 */
public class RTPServer implements ControllerListener {
    private boolean realized = false;
    private boolean configured = false;
    private String ipAddress = "192.168.178.101";
    private String destUrl = "rtp://192.168.178.101:42050/audio/1";
    private MediaLocator dest = new MediaLocator(destUrl);

    public static void main(String [] args) {
	new RTPServer();
    }

    public RTPServer() {
	Processor p;

	String srcFile = "D:/Users/Videos/Test/TheSimpsonsMovie1080pTrailer.avi";
	MediaLocator src = new MediaLocator("file:" + srcFile);

	try {
	    p = Manager.createProcessor(src);
	    p.addControllerListener(this);

	    p.configure();
	    while (! configured) {
		try {
		    Thread.currentThread().sleep(100L);;
		} catch (InterruptedException e) {
		    // ignore
		}
	    }

	    setTrackFormat(p);
	    p.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW_RTP));

	    p.realize();
	    while (! realized) {
		try {
		    Thread.currentThread().sleep(100L);;
		} catch (InterruptedException e) {
		    // ignore
		}
	    }

	    transmit(p);

	} catch(Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }


    /**
     * It doesn't seem to be enough to set the processor output format.
     * This code sets the format for each track within the stream to a 
     * supported format. On my system this a 4-bit format which sounds
     * crappy, but is recognised by the client. 
     * Without this track setting, the client barfs because the default
     * track format is a 16-bit mpeg stream which the client doesn't
     * understand
     */
    private void setTrackFormat(Processor p) {
	// Get the tracks from the processor
	TrackControl [] tracks = p.getTrackControls();
	
	// Do we have atleast one track?
	if (tracks == null || tracks.length < 1) {
	    System.out.println("Couldn't find tracks in processor");
	    System.exit(1);
	}
	
	// Set the output content descriptor to RAW_RTP
	// This will limit the supported formats reported from
	// Track.getSupportedFormats to only valid RTP formats.
	ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
	p.setContentDescriptor(cd);
	
	Format supported[];
	Format chosen;
	boolean atLeastOneTrack = false;
	
	// Program the tracks.
	for (int i = 0; i < tracks.length; i++) {
	    Format format = tracks[i].getFormat();
	    if (tracks[i].isEnabled()) {
		
		supported = tracks[i].getSupportedFormats();
		for (int n = 0; n < supported.length; n++)
		    System.out.println("Supported format: " + supported[n]);
		
		// We've set the output content to the RAW_RTP.
		// So all the supported formats should work with RTP.
		// We'll just pick the first one.
		
		if (supported.length > 0) {
		    chosen = supported[0];
		    tracks[i].setFormat(chosen);
		    System.err.println("Track " + i + " is set to transmit as:");
		    System.err.println("  " + chosen);
		    atLeastOneTrack = true;
		} else
		    tracks[i].setEnabled(false);
	    } else
		tracks[i].setEnabled(false);
	}
    }

    /**
     * This code uses the RTP Manager to handle a session
     * stream. It is more complex than the simpler version
     * below, but can also handle more complex streams

    private void transmit(Processor p) {
	try {
	    DataSource output = p.getDataOutput();
	    PushBufferDataSource pbds = (PushBufferDataSource) output;
	    RTPManager rtpMgr = RTPManager.newInstance();
	    SessionAddress localAddr, destAddr;
	    SendStream sendStream;
	    int port = 42506;
	    SourceDescription srcDesList[];

	    localAddr = new SessionAddress( InetAddress.getLocalHost(),
					    port);
	    
	    InetAddress	ipAddr = InetAddress.getByName(ipAddress);
	    destAddr = new SessionAddress( ipAddr, port);
	    
	    rtpMgr.initialize(localAddr);
	    
	    rtpMgr.addTarget(destAddr);
	    
	    System.err.println( "Created RTP session: " + ipAddress + " " + port);
	    
	    sendStream = rtpMgr.createSendStream(output, 0);		
	    sendStream.start();

	    p.start();
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    */

    private void transmit(Processor p) {
	try {
	    DataSource output = p.getDataOutput();
	    DataSink rtpSink;
	    rtpSink = Manager.createDataSink(output, dest);
	    System.out.println("Sink content type: " + rtpSink.getContentType());
	    System.out.println("Sink media type: " + rtpSink.getOutputLocator().toString());
	    rtpSink.open();
	    rtpSink.start();
	    p.start();
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    public synchronized void controllerUpdate(ControllerEvent evt) {
	if (evt instanceof RealizeCompleteEvent) {
	    realized = true;
	} else 	if (evt instanceof ConfigureCompleteEvent) {
	    configured = true;
	} else if (evt instanceof EndOfMediaEvent) {
	    System.exit(0);
	} else {
	    // System.out.println(evt.toString());
	}
    }
}
