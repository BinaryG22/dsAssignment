package dslab.transfer.client;

import dslab.protocol.DmtProtocol;
import dslab.util.Config;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TransferServerClient implements Runnable {
    private Config config;
    private DmtProtocol dmtProtocol;
    private String[] message;
    private Config domain_config;
    private PrintWriter writer;

    // takes a parameter with then the client should construct a dmtp message to the the mailbox server
    public TransferServerClient(Config config, String[] message, PrintWriter writer) {
        this.config = config;
        this.dmtProtocol = new DmtProtocol(config);
        this.message = message;
        //instantiating this new Config does not work
        domain_config = new Config("domains");
        this.writer = writer;
    }


    @Override
    public void run() {
        Socket socket = null;
        /*
         * create a new tcp socket at specified host and port - make sure
         * you specify them correctly in the client properties file(see
         * client1.properties and client2.properties)
         */

        if (message[2].contains("earth.planet")) {
            new Thread(new MessageDeliverer(domain_config, "earth.planet", message)).start();
        }

        if (message[2].contains("univer.ze")) {
            new Thread(new MessageDeliverer(domain_config, "univer.ze", message)).start();
        }

        String[] addresses = null;
        addresses = message[2].split(",");
        for (String adress : addresses
        ) {
            String[] parseAdress = adress.split("@");
            if (!(parseAdress[1].equals("earth.planet") || parseAdress[1].equals("univer.ze"))) {
                String reponse = "error message could not be sent to: " + adress;
                writer.println(reponse);
                writer.flush();
            }
        }

    }

}
