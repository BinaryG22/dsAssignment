package dslab.monitoring;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Thread to listen for incoming data packets on the given socket.
 */
public class ListenerThread extends Thread {

    private DatagramSocket datagramSocket;

    public ListenerThread(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void run() {

        System.out.println("Thread ID: "+this.getId());


        byte[] buffer;
        DatagramPacket packet;
        try {
            while (true) {
                buffer = new byte[512];
                // create a datagram packet of specified length (buffer.length)
                /*
                 * Keep in mind that, in UDP, packet delivery is not guaranteed,
                 * and the order of the delivery/processing is also not guaranteed.
                 */
                packet = new DatagramPacket(buffer, buffer.length);

                // wait for incoming packets from client
                datagramSocket.receive(packet);
                // get the data from the packet
                String request = new String(packet.getData());

                System.out.println("Received request-packet from client: " + request);

                // get the address of the sender (client) from the received
                // packet
                InetAddress address = packet.getAddress();
                // get the port of the sender from the received packet
                String[] parts = request.split("\\s");
                String[] parts_1 = parts[0].split(":");
                int port = Integer.parseInt(parts_1[1]);



                addToListOfAdresses(parts[1]);
                addToListOfServers(address, port);

                System.out.println("adresses");
                MonitoringServer.DEBUG_ADRESSES();
                System.out.println("servers");
                MonitoringServer.DEBUG_SERVERS();
            }

        } catch (SocketException e) {
            // when the socket is closed, the send or receive methods of the DatagramSocket will throw a SocketException
            System.out.println("SocketException while waiting for/handling packets: " + e.getMessage());
            return;
        } catch (IOException e) {
            // other exceptions should be handled correctly in your implementation
            throw new UncheckedIOException(e);
        } finally {
            if (datagramSocket != null && !datagramSocket.isClosed()) {
                datagramSocket.close();
            }
        }

    }

    private void addToListOfServers(InetAddress address, int port) {
        MonitoringServer.addToListOfServices(address, port);
    }

    private void addToListOfAdresses(String sender) {
        // to do sth with the list in the monitor server
        MonitoringServer.addToListOfAddresses(sender);
    }


}
