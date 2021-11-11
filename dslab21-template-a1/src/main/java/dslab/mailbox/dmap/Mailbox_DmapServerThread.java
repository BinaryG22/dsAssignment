package dslab.mailbox.dmap;

import dslab.protocol.DmaProtocol;
import dslab.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


public class Mailbox_DmapServerThread extends Thread{
    private Socket clientSocket;
    private DmaProtocol dmaProtocol;

    public Mailbox_DmapServerThread(Socket clientSocket, Config config){
        this.clientSocket = clientSocket;
        this.dmaProtocol = new DmaProtocol(config);
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

            writer.println(dmaProtocol.checkConnection(clientSocket));
            writer.flush();


            String request;
            String response;
            // read client requests
            while ((request = reader.readLine()) != null) {
                System.out.println("Client sent the following request: " + request);

                /*
                 * check if request has the correct format: !ping
                 * <client-name>
                 */
                if (request.equals("list")){
                    if (dmaProtocol.isLoggedIn()){
                        response = dmaProtocol.validateRequest(request);
                        for (String[] message: dmaProtocol.getAllMessages()
                        ) {
                            writer.println(message[0] + " " + message[1] + " " + message[2]);
                            writer.flush();
                        }
                        dmaProtocol.clearMessages();
                    }else {
                        response = "error you must first log in";
                        writer.println(response);
                        writer.flush();
                    }
                }else if (request.startsWith("show")){
                    if (dmaProtocol.isLoggedIn()) {
                        if (dmaProtocol.validateRequest(request).equals("ok")) {
                            for (String messagesInDmtp : dmaProtocol.getMessagesById()
                            ) {
                                writer.println(messagesInDmtp);
                                writer.flush();
                            }
                        }
                    }else{
                        response = "error you must first log in";
                        writer.println(response);
                        writer.flush();
                    }
                }else {
                    response = dmaProtocol.validateRequest(request);
                    writer.println(response);
                    writer.flush();


                    System.out.println(response);
                    if (response.equals("ok bye")){
                        break;
                    }
                }
            }
            writer.close();
            // construct response here
        }  catch (IOException e) {

        }

    }
}
