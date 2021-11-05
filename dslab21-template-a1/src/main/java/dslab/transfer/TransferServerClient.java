package dslab.transfer;

import dslab.DMTP.DmtProtocol;
import dslab.util.Config;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TransferServerClient implements Runnable{
        private Config config;
        private DmtProtocol dmtProtocol;

        // takes a parameter with then the client should construct a dmtp message to the the mailbox server
        public TransferServerClient(Config config, String message){
            this.config = config;
            this.dmtProtocol = new DmtProtocol(config);
        }


    @Override
    public void run() {
        Socket socket = null;
        BufferedReader userInputReader = null;

        try {
            /*
             * create a new tcp socket at specified host and port - make sure
             * you specify them correctly in the client properties file(see
             * client1.properties and client2.properties)
             */
            socket = new Socket(config.getString("monitoring.host"), config.getInt("monitoring.port"));
            // create a reader to retrieve messages send by the server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // create a writer to send messages to the server
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());
            // create the client input reader from command line
            userInputReader = new BufferedReader(new InputStreamReader(System.in));


            while (true) {

            }


            //catch exceptions
        } catch (UnknownHostException e) {
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

            if (userInputReader != null) {
                try {
                    userInputReader.close();
                } catch (IOException e) {
                    // Ignored because we cannot handle it
                }
            }
        }
    }
}
