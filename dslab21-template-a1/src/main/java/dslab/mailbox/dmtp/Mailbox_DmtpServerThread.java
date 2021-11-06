package dslab.mailbox.dmtp;

import dslab.mailbox.MailboxServer;
import dslab.protocol.DmtProtocol;
import dslab.util.Config;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;


public class Mailbox_DmtpServerThread extends Thread{
    private Socket clientSocket;
    private DmtProtocol dmtProtocol;
    private ExecutorService threadPool;
    private Config config;


    public Mailbox_DmtpServerThread(Socket clientSocket, Config config){
        this.config = config;
        this.clientSocket = clientSocket;
        this.dmtProtocol = new DmtProtocol(config);
    }

    @Override
    public void run() {
        // wait for Client to connect
        try {
            System.out.println("Thread ID: "+this.getId());
            // prepare the input reader for the socket
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // prepare the writer for responding to clients requests
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());


            System.out.println(dmtProtocol.checkConnection(clientSocket));

            writer.println("Server answers: " + dmtProtocol.checkConnection(clientSocket));
            writer.flush();


            String request;
            String response;
            // read client requests
            //debug now here

            while ((request = reader.readLine()) != null) {
                System.out.println("Client sent the following request: " + request);
                response = dmtProtocol.validateRequest(request);

                if (response.equals("save")){
                    MailboxServer.saveMessageInHashMap(dmtProtocol.getToSaveInMailBoxServer());
                    break;
                }

                System.out.println("server answers: " +response);
                writer.println("Server answers: " + response);
                writer.flush();


            }
            clientSocket.close();
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
}
