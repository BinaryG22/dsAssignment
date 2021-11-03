package dslab.transfer.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerThread extends Thread{
    private Socket clientSocket;

    public ServerThread(Socket clientSocket){
        this.clientSocket = clientSocket;
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

                String request;
                // read client requests
                while(true) {
                    System.out.println("currently in while loop in server thread");
                    String line = reader.readLine();
                    if(line == null) break;
                    writer.println("Echo: " + line);
                }
                clientSocket.close();
                // construct response here
            } catch (IOException e) {
                e.printStackTrace();
            }



    }
}
