package dslab.mailbox.dmap;

import dslab.util.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DmapListener extends Thread{
    ServerSocket dmapServerSocket = null;
    Socket client = null;
    private ExecutorService dmap_threadPool;
    private Config config;

    public DmapListener(ServerSocket serverSocket, Config config){
        this.config = config;
        this.dmapServerSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                dmap_threadPool = Executors.newCachedThreadPool();
                client = dmapServerSocket.accept();
                dmap_threadPool.submit(new Mailbox_DmapServerThread(client, config));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
