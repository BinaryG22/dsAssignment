package dslab.transfer.client;

import dslab.protocol.DmtProtocol;
import dslab.util.Config;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TransferServerClient implements Runnable{
        private Config config;
        private DmtProtocol dmtProtocol;
        private String[] message;
        private Config domain_config;

        // takes a parameter with then the client should construct a dmtp message to the the mailbox server
        public TransferServerClient(Config config, String[] message){
            this.config = config;
            this.dmtProtocol = new DmtProtocol(config);
            this.message = message;
            //instantiating this new Config does not work
            domain_config = new Config("domains");
        }


    @Override
    public void run() {
        Socket socket = null;

        try {
            /*
             * create a new tcp socket at specified host and port - make sure
             * you specify them correctly in the client properties file(see
             * client1.properties and client2.properties)
             */

            if (message[2].contains("earth.planet")) {
                String[] parts = domain_config.getString("earth.planet").split(":");
                socket = new Socket(parts[0], Integer.parseInt(parts[1]));
            }else System.out.println("wrong index number on message for looking up adress");

            assert socket != null;
            // create a reader to retrieve messages send by the server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // create a writer to send messages to the server
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());
            // create the client input reader from command line
            //userInputReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter outputWriter = new PrintWriter(System.out);

            int msg_index = 0;
            System.out.println("message is:" + Arrays.toString(message));
            while (true) {

                String answerFromServer = serverReader.readLine();
                System.out.println("answer from mailbox server:" + answerFromServer);

                if (msg_index < message.length) {
                    serverWriter.println(message[msg_index]);
                    serverWriter.flush();
                    msg_index++;
                }else break;
            }

            //close socket?
            socket.close();
    }catch (UnknownHostException e) {
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
