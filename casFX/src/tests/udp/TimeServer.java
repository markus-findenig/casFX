package tests.udp;
import java.net.*;
import java.util.*;

public class TimeServer {
    
    /** Creates a new instance of TimeServer */
    public TimeServer() {
    }
    
    public static void main(String[] args) {
        try {            
            DatagramSocket socket = new DatagramSocket(8889); // listener
            byte[] buf = new byte[80];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (true) {
                // wait for request
                System.out.println("Wait for time request ...");
                socket.receive(packet); // blocking, waits for data
                System.out.println("Time request received.");
                // create and send response
                buf = (new Date()).toString().getBytes();
                packet.setData(buf);
                socket.send(packet);
                System.out.println("Time sent.");            
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    
}
