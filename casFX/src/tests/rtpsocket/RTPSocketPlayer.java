package tests.rtpsocket;

/*
 * @(#)RTPSocketPlayer.java	1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

//modified by mchsieh
import javax.media.rtp.*;
import javax.media.*;
import javax.media.protocol.*;
import java.io.*;
import java.net.*;
import com.sun.media.ui.*;

/**
 * A RTP over UDP player which will receive RTP UDP packets and stream
 * them to the JMF RTP Player or (RTPSM) which is not aware of the
 * underlying network/transport protocol. This sample uses the
 * interfaces defined in javax.media.rtp.RTPSocket and its related classes.
 */
public class RTPSocketPlayer implements ControllerListener{
    /////// ENTER SESSION PARAMETERS BELOW FOR YOUR RTP SESSION /////////////////
    /**
     * RTP Session address, multicast, unicast or broadcast address
     */
//    String address = "224.144.251.245";
    static String address = "127.0.0.1";
    /**
     * RTP Session port
     */
//    int port = 9000;
    static int port = 8554;
    /////////////DO NOT CHANGE ANYTHING BELOW THIS LINE//////////////////////////
    
    // our main rtpsocket abstraction to which we will create and send
    // to the Manager for appropriate handler creation
    RTPSocket rtpsocket = null;
    // the control RTPPushDataSource of the RTPSocket above
    RTPPushDataSource rtcpsource = null;
    // The GUI to handle our player
    PlayerWindow playerWindow;
    // The handler created for our RTP session, as returned by the Manager
    Player player;
    // maximum size of buffer for UDP receive from the sockets
    private  int maxsize = 2000;

    //mchsieh udphandler extends threads
    UDPHandler  rtp = null;
    UDPHandler rtcp = null;
    
    public static void main(String[] args){
    	System.out.println("Usage: RTPSocketPlayer sessionaddress sessionport");
	switch(args.length) {
	case 0:
    		System.out.println("default sessionaddress " + address);
    		System.out.println("default sessionport " + port);
	 	break;
	case 1:
		address = args[0];
		break;
	case 2:
		address = args[0];
		port = Integer.parseInt(args[1]);
		break;
	default:
		break;
	}
	
	new RTPSocketPlayer();
    }
    public RTPSocketPlayer(){
	
	// create the RTPSocket
	rtpsocket = new RTPSocket();
	// set its content type : rtpraw
	rtpsocket.setContentType("rtpraw");
	// set the RTP Session address and port of the RTP data
	rtp = new UDPHandler(address, port);
	// set the above UDP Handler to be the sourcestream of the rtpsocket
	rtpsocket.setOutputStream(rtp);
	rtpsocket.setInputStream(rtp);
	// set the RTP Session address and port of the RTCP data
	rtcp = new UDPHandler(address, port +1);
	// get a handle over the RTCP Datasource so that we can set
	// the sourcestream and deststream of this source to the rtcp
	// udp handler we created above.
	rtcpsource = rtpsocket.getControlChannel();
	// Since we intend to send RTCP packets from the network to
	// RTPSM and vice-versa, we need to set the RTCP UDP handler
	// as both the input and output stream of the rtcpsource.
	rtcpsource.setOutputStream(rtcp);
	rtcpsource.setInputStream(rtcp);
	// connect the RTP socket data source before creating
	// the player
	try{
	    rtpsocket.connect();
	    player = Manager.createPlayer(rtpsocket);
	} catch (NoPlayerException e){
	    System.err.println(e.getMessage());
	    e.printStackTrace();
	    return;
	}
	catch (IOException e){
	    System.err.println(e.getMessage());
	    e.printStackTrace();
	    return;
	}
	if (player != null){
	    player.addControllerListener(this);
	    // send this player to out playerwindow
	    playerWindow = new PlayerWindow(player);
	}
    }
    @Override
	public synchronized void controllerUpdate(ControllerEvent ce) {
	if (ce instanceof ControllerErrorEvent){
	  if (player != null)
	    player.removeControllerListener(this);
	  playerWindow = null;
	  // stop udp handlers
	  if (rtp != null)
	    rtp.close();
	  if (rtcp != null)
	    rtcp.close();
	}
	
    }
    // method used by inner class UDPHandler to open a datagram or
    // multicast socket as the case maybe
    private DatagramSocket InitSocket(String sockaddress, int sockport){
	InetAddress addr = null;
	DatagramSocket sock = null;
	try{
	    addr = InetAddress.getByName(sockaddress);
	    if (addr.isMulticastAddress()){
		MulticastSocket msock = new MulticastSocket(sockport);
		msock.joinGroup(addr);
		sock = msock;		
	    }else{		
		sock = new
		    DatagramSocket(sockport,addr);
	    }
	    return sock;
	}
	catch (SocketException e){
	    e.printStackTrace();
	    return null;
	}
	catch (UnknownHostException e){
	    e.printStackTrace();
	    return null;
	}
	catch (IOException e){
	    e.printStackTrace();
	    return null;
	}
	
    }// end of InitSocket
    
    //INNER CLASS UDPHandler which will receive UDP RTP Packets and
    //stream them to the handler of the sources stream. IN case of
    //RTCP , it will also accept RTCP packets and send them on the
    //underlying network.
    public class UDPHandler extends Thread
    implements PushSourceStream, OutputDataStream{
	DatagramSocket mysock = null;
	DatagramPacket dp = null;
	SourceTransferHandler outputhandler = null;
	String myaddress = null;
	int myport;
	boolean closed = false;
	// in the constructor we open the socket and create the main
	// UDPHandler thread.
	public UDPHandler(String haddress, int hport){
	    myaddress = haddress;
	    myport = hport;
	    mysock = InitSocket(myaddress,myport);	    	    
	    setDaemon(true);
	    //mchsieh automatically starts calls the start method to start the thread
	    start();
	}
	// the main thread simply receives RTP data packets from the
	// network and transfer's this data to the output handler of
	// this stream.
	@Override
	public void run(){
	    int len;
	    while(true){
		if (closed){
		    cleanup();
		    return;
		}
		try{
		    do{
			dp = new DatagramPacket(new byte[maxsize],maxsize);
			mysock.receive(dp);
			if (closed){
			    cleanup();
			    return;
			}
			len = dp.getLength();
			if (len > (maxsize >> 1))  
			    maxsize = len << 1;
		    }
		    while (len >= dp.getData().length);
		}catch (Exception e){
		    cleanup();
		    return;
		}
		
		if (outputhandler != null)
		    outputhandler.transferData(this);
	    }
	}
	public void close(){
	  closed = true;
	}
	private void cleanup(){
	  mysock.close();
	  stop();
	}
	// methods of PushSourceStream
	@Override
	public Object[] getControls() {
	    return new Object[0];
	}
	
	@Override
	public Object getControl(String controlName) {
	    return null;
	}
	@Override
	public  ContentDescriptor getContentDescriptor(){
	    return null;
	}
	@Override
	public long getContentLength(){
	    return SourceStream.LENGTH_UNKNOWN;
	}
	@Override
	public boolean endOfStream(){
	    return false;
	}
	// method by which data is transferred from the underlying
	// network to the RTPSM.
	@Override
	public int read(byte buffer[],
			int offset,
			int length){
	    System.arraycopy(dp.getData(),
			     0,
			     buffer,
			     offset,
			     dp.getLength());
	    return dp.getData().length;
	    
	}		 
	
	@Override
	public int getMinimumTransferSize(){
	    return dp.getLength();
	}
	@Override
	public void setTransferHandler(SourceTransferHandler transferHandler){
	    this.outputhandler = transferHandler;
	}
	// methods of OutputDataStream used by teh RTPSM to transfer
	// data to the underlying network.
	@Override
	public int write(byte[] buffer,
			 int offset,
			 int length){
	    InetAddress addr = null;
	    try{
		addr = InetAddress.getByName(myaddress);
	    }catch (UnknownHostException e){
	    }
	    DatagramPacket dp = new DatagramPacket(buffer,length,addr,myport);
	    try{
		mysock.send(dp);
	    }catch (IOException e){}
	    return dp.getLength();
	}
	
    }
    
    
}// end of Test

    


