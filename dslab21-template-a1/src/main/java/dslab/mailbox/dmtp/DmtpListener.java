package dslab.mailbox.dmtp;

import dslab.util.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DmtpListener extends Thread{
    ServerSocket dmtpServerSocket = null;
    Socket client = null;
    private ExecutorService dmtp_threadPool;
    private Config config;

    public DmtpListener(ServerSocket serverSocket, Config config){
        this.config = config;
        this.dmtpServerSocket = serverSocket;
    }

    @Override
    public void run() {
        dmtp_threadPool = Executors.newCachedThreadPool();
        while (true) {
            try {
                client = dmtpServerSocket.accept();
                dmtp_threadPool.submit(new Mailbox_DmtpServerThread(client, config));
            } catch (IOException e) {
            }
        }
    }

    private void shutdown() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
