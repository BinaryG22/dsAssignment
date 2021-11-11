package dslab.monitoring;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread to listen for incoming data packets on the given socket.
 */
public class ListenerThread extends Thread {

    private DatagramSocket datagramSocket;
    private ConcurrentHashMap<String, Integer> servers;
    private  ConcurrentHashMap<String, Integer> addresses;

    public ListenerThread(DatagramSocket datagramSocket, ConcurrentHashMap servers, ConcurrentHashMap addresses) {
        this.datagramSocket = datagramSocket;
        this.servers = servers;
        this.addresses = addresses;

    }

    public void run() {

        System.out.println("Thread ID: "+this.getId());


        byte[] buffer;
        DatagramPacket packet;
        try {
            while (true) {
                buffer = new byte[128];
                // create a datagram packet of specified length (buffer.length)
                /*
                 * Keep in mind that, in UDP, packet delivery is not guaranteed,
                 * and the order of the delivery/processing is also not guaranteed.
                 */
                packet = new DatagramPacket(buffer, buffer.length);

                // wait for incoming packets from client
                datagramSocket.receive(packet);
                // get the data from the packet
                String request = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received request-packet from client: " + request);

                // get the address of the sender (client) from the received
                // packet
                InetAddress address = packet.getAddress();
                // get the port of the sender from the received packet
                String[] parts = request.split("\\s");
                String[] parts_1 = parts[0].split(":");
                int port = Integer.parseInt(parts_1[1]);

                System.out.println(Arrays.toString(parts));

                addToListOfAddresses(parts[1]);
                addToListOfServers(address, port);
            }

        } catch (SocketException e) {
            // when the socket is closed, the send or receive methods of the DatagramSocket will throw a SocketException
            System.out.println("SocketException while waiting for/handling packets: " + e.getMessage());

        } catch (IOException e) {
            // other exceptions should be handled correctly in your implementation
            throw new UncheckedIOException(e);
        } finally {
            if (datagramSocket != null && !datagramSocket.isClosed()) {
                datagramSocket.close();
            }
        }

    }

    public  synchronized void  addToListOfAddresses(String sender) {
        if (!addresses.containsKey(sender)){
            addresses.put(sender, 1);
        }else {

            int currentCount = addresses.get(sender);
            int newValue = currentCount + 1;
            addresses.replace(sender, newValue);
        }
    }

    public  synchronized void addToListOfServers(InetAddress address, int port) {
        String key = address + ":" + port;
        if (!servers.containsKey(key)){
            servers.put(key, 1);
        }else {
            int currentCount = servers.get(key);
            int newValue = currentCount + 1;
            servers.replace(key, newValue);
        }
    }


}
