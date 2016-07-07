package tests.udp;
import java.net.*;

public class TimeClient {
    
    public TimeClient() {
    }
    
    public static void main(String[] args) {
        try {
        	final String hostname;
            if (args.length != 1) {
                System.out.println("Usage: java TimeClient hostname");
                System.out.println("Use default: hostname=localhost");
                hostname = "localhost";
            } else {
            	hostname = args[0];
            }
            byte[] buf = new byte[80];
            InetAddress address = InetAddress.getByName(hostname);
            DatagramSocket socket = new DatagramSocket(); // UDP socket
            socket.setSoTimeout(2000);
            DatagramPacket packet = new DatagramPacket(
                buf, // sending request data: empty for time service
                buf.length, // max data length
                address, // remote host address
                8889); // remote host port
            // send request
            socket.send(packet);
            System.out.println("Time request sent.");
            // receive response
            System.out.println("Waiting for response ...");
            socket.receive(packet);
            // display response
            String time = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Time received: " + time);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
