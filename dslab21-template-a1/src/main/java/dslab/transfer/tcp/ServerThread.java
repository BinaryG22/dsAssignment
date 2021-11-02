package dslab.transfer.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerThread extends Thread{
    private ServerSocket serverSocket;

    public ServerThread(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            Socket socket = null;
            // wait for Client to connect
            try {
                socket = serverSocket.accept();
                // prepare the input reader for the socket
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // prepare the writer for responding to clients requests
                PrintWriter writer = new PrintWriter(socket.getOutputStream());

                String request;
                // read client requests
                while ((request = reader.readLine()) != null) {
                    System.out.println("Client sent the following request: " + request);
                }
                // construct response here
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
}
