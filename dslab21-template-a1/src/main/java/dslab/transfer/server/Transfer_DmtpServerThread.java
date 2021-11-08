package dslab.transfer.server;

import dslab.protocol.DmtProtocol;
import dslab.transfer.client.TransferServerClient;
import dslab.util.Config;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Transfer_DmtpServerThread extends Thread{
    private Socket clientSocket;
    private DmtProtocol dmtProtocol;
    private ExecutorService threadPool;
    private Config config;


    public Transfer_DmtpServerThread(Socket clientSocket, Config config){
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

                while (true) {
                    request = reader.readLine();
                    System.out.println("Client sent the following request: " + request);

                    /*
                     * check if request has the correct format: !ping
                     * <client-name>
                     */

                    response = dmtProtocol.validateRequest(request);

                    if (response.equals("send")){
                        String[] messageForMailboxServer = dmtProtocol.getMessageForMailboxServer();
                        System.out.println(Arrays.toString(messageForMailboxServer));
                        threadPool = Executors.newCachedThreadPool();
                        threadPool.submit(new TransferServerClient(config, messageForMailboxServer));
                    }


                    System.out.println("server answers: " +response);
                    writer.println("Server answers: " + response);
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
}
