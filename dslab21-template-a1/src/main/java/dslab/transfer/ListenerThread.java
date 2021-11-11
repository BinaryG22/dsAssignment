package dslab.transfer;

import dslab.transfer.server.Transfer_DmtpServerThread;
import dslab.util.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListenerThread extends Thread {
    private ServerSocket tcp_server = null;
    private Socket tcp_clientSocket = null;
    private ExecutorService threadPool;
    private Config config;

    public ListenerThread(ServerSocket serverSocket, Config config){
        this.config = config;
        this.tcp_server = serverSocket;
    }


    @Override
    public void run() {

            threadPool = Executors.newCachedThreadPool();


        while (true) {
            Socket newClient = null;
            try {
                newClient = tcp_server.accept();
                threadPool.submit(new Transfer_DmtpServerThread(tcp_server, newClient, config));

            } catch (IOException e) {
            }
        }

    }
}
