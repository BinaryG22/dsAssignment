package dslab.mailbox.dmtp;

import dslab.mailbox.MailboxServer;
import dslab.protocol.DmtProtocol;
import dslab.util.Config;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;


public class Mailbox_DmtpServerThread extends Thread{
    private Socket clientSocket;
    private DmtProtocol dmtProtocol;
    private ExecutorService threadPool;
    private Config config;
    private ArrayList<String> users_messageBeingSentTo =  new ArrayList<>();



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

            writer.println(dmtProtocol.checkConnection(clientSocket));
            writer.flush();


            String request;
            String response;
            // read client requests
            //debug now here

            while ((request = reader.readLine()) != null) {
                System.out.println("Client sent the following request: " + request);

                if (request.startsWith("to")){
                    String user_config_location = config.getString("users.config");
                    Config userConfig = new Config(user_config_location);
                    String[] parts = request.split("\\s");
                    String[] addresses = null;
                    String recipients_asString = parts[1];
                    if (recipients_asString.contains(",")) {
                        addresses = parts[1].split(",");
                    }else addresses = new String[] {parts[1]};
                    for (String adress:addresses
                    ) {
                        String[] parseAdress = adress.split("@");
                        if (!userConfig.listKeys().contains(parseAdress[0])){
                            response = "error unknown user " + parseAdress[0];
                            System.out.println("server answers: " +response);
                            writer.println(response);
                            writer.flush();
                        }else {
                            users_messageBeingSentTo.add(parseAdress[0]);
                            response = "ok " + users_messageBeingSentTo.size();
                            System.out.println("server answers: " +response);
                            writer.println(response);
                            writer.flush();
                        }
                    }
                }else{
                    if (request.equals("save")){
                        String sender = dmtProtocol.getSender();
                        String subject = dmtProtocol.getSubject();
                        String data = dmtProtocol.getData();

                        MailboxServer.saveMessageInHashMap(users_messageBeingSentTo, sender, subject, data);
                        break;
                    }

                    response = dmtProtocol.validateRequest(request);
                    System.out.println("server answers: " +response);
                    writer.println(response);
                    writer.flush();
                }




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
