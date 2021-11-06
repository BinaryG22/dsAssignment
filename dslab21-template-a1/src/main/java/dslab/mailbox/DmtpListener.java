package dslab.mailbox;

import dslab.DMTP.DmtpServerThread;
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
        try {
            client = dmtpServerSocket.accept();
            dmtp_threadPool = Executors.newCachedThreadPool();
            dmtp_threadPool.submit(new DmtpServerThread(client, config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
