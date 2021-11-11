package dslab.transfer.server;

import dslab.protocol.DmtProtocol;
import dslab.transfer.client.TransferServerClient;
import dslab.util.Config;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Transfer_DmtpServerThread extends Thread {
    private final Socket clientSocket;
    private final DmtProtocol dmtProtocol;
    private ExecutorService threadPool;
    private final Config config;
    private final ServerSocket serverSocket;


    public Transfer_DmtpServerThread(ServerSocket serverSocket, Socket clientSocket, Config config) {
        this.config = config;
        this.clientSocket = clientSocket;
        this.dmtProtocol = new DmtProtocol(config);
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        // wait for Client to connect
        try {
            System.out.println("Thread ID: " + this.getId());
            // prepare the input reader for the socket
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // prepare the writer for responding to clients requests
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());


            System.out.println(dmtProtocol.checkConnection(clientSocket));

            writer.println(dmtProtocol.checkConnection(clientSocket));
            writer.flush();


            String request;
            String response;
            // read client requests
            //debug now here

            while (true) {
                request = reader.readLine();
                System.out.println("Client sent the following request: " + request);

                /*
                 * check if request has the correct format: !ping
                 * <client-name>
                 */

                response = dmtProtocol.validateRequest(request);


                if (request.equals("send") && response.equals("ok")) {
                    String[] messageForMailboxServer = dmtProtocol.getMessageForMailboxServer();
                    System.out.println(Arrays.toString(messageForMailboxServer));
                    threadPool = Executors.newCachedThreadPool();
                    threadPool.submit(new TransferServerClient(config, messageForMailboxServer));

                    sendToMonitorServer(dmtProtocol.getSender());

                    dmtProtocol.resetAllValues();
                }

                System.out.println("server answers: " + response);
                writer.println(response);
                writer.flush();
            }
            // construct response here
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }


    }

    private void sendToMonitorServer(String sender) {
        DatagramSocket socket = null;

        try {
            // open a new DatagramSocket
            socket = new DatagramSocket();

            byte[] buffer;
            DatagramPacket packet;

            //which host adress to use?
            String toSend = InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort()+ " " + sender;

                // convert the input String to a byte[]
                buffer = toSend.getBytes();

                // create the datagram packet with all the necessary information
                // for sending the packet to the server
                packet = new DatagramPacket(buffer, buffer.length,
                        InetAddress.getByName(config.getString("monitoring.host")),
                        config.getInt("monitoring.port"));

                // send request-packet to server
                socket.send(packet);
        } catch (IOException e) {

        }
        if (socket != null) {
            socket.close();
        }
    }
}
