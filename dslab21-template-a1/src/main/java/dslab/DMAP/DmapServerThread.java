package dslab.DMAP;

import dslab.DMAP.DmaProtocol;
import dslab.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class DmapServerThread extends Thread{
    private Socket clientSocket;
    private DmaProtocol dmaProtocol;

    public DmapServerThread(Socket clientSocket, Config config){
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

            writer.println("Server answers: " + dmaProtocol.checkConnection(clientSocket));
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

                response = dmaProtocol.validateRequest(request);

                writer.println("Server answers: " + response);
                writer.flush();
            }
            clientSocket.close();
            // construct response here
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
