package dslab.transfer.client;

import dslab.util.Config;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class MessageDeliverer implements Runnable{
    Config config;
    private final static String EARTH_PLANET_DOMAIN = "earth.planet";
    private final static String UNIVER_ZE_DOMAIN = "univer.ze";
    Socket socket;
    String[] message;


    public MessageDeliverer(Config config, String domainName, String[] message){
        this.message = message;
        this.config = config;
        if (domainName.equals(EARTH_PLANET_DOMAIN)){
            String[] parts = config.getString("earth.planet").split(":");
            System.out.println("host:" + parts[0] +", port:" + parts[1]);
            try {
                socket = new Socket(parts[0], Integer.parseInt(parts[1]));
            } catch (IOException e) {
                System.out.println("SocketException while handling socket at host: "
                        + parts[0] + " and port: " + Integer.parseInt(parts[1]));
            }
        }else if (domainName.equals(UNIVER_ZE_DOMAIN)){
            String[] parts = config.getString("univer.ze").split(":");
            System.out.println("host:" + parts[0] +", port:" + parts[1]);
            try {
                socket = new Socket(parts[0], Integer.parseInt(parts[1]));
            } catch (IOException e) {
                System.out.println("SocketException while handling socket at host: "
                        + parts[0] + " and port: " + Integer.parseInt(parts[1]));
            }
        }
    }

    @Override
    public void run() {
        try {
            // create a reader to retrieve messages send by the server
            if (socket != null) {
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // create a writer to send messages to the server
                PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());
                // create the client input reader from command line
                //userInputReader = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter outputWriter = new PrintWriter(System.out);


                int msg_index = 0;
                while (true) {

                    String answerFromServer = serverReader.readLine();
                    System.out.println("answer from mailbox server:" + answerFromServer);

                    if (msg_index < message.length) {
                        serverWriter.println(message[msg_index]);
                        serverWriter.flush();
                        msg_index++;
                    } else break;
                }
            }
        }



        catch (UnknownHostException e) {
            System.out.println("Cannot connect to host: " + e.getMessage());
        } catch (SocketException e) {
            // when the socket is closed, the I/O methods of the Socket will throw a SocketException
            // almost all SocketException cases indicate that the socket was closed
            System.out.println("SocketException while handling socket: " + e.getMessage());
        } catch (IOException e) {
            // you should properly handle all other exceptions
            throw new UncheckedIOException(e);
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignored because we cannot handle it
                }
            }

        }
    }
}
