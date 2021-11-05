package dslab.mailbox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dslab.ComponentFactory;
import dslab.DMAP.DmapServerThread;
import dslab.DMTP.DmtpServerThread;
import dslab.util.Config;

public class MailboxServer implements IMailboxServer, Runnable {
    /*
    DMAP
     */
    private ServerSocket dmap_mailboxServer = null;
    private Socket dmap_clientSocket = null;
    private BufferedReader dmap_server_reader;
    private PrintWriter dmap_server_writer;
    private ExecutorService dmap_threadPool;
    /*
    DMAP
     */

    /*
    DMTP
     */
    private ServerSocket dmtp_mailboxServer = null;
    private Socket dmtp_clientSocket = null;
    private BufferedReader dmtp_server_reader;
    private PrintWriter dmtp_server_writer;
    private ExecutorService dmtp_threadPool;

    /*
    DMTP
     */

    private Config config;

    /**
     * Creates a new server instance.
     *
     * @param componentId the id of the component that corresponds to the Config resource
     * @param config the component config
     * @param in the input stream to read console input from
     * @param out the output stream to write console output to
     */
    public MailboxServer(String componentId, Config config, InputStream in, PrintStream out) {
        this.config = config;
        dmap_server_reader = new BufferedReader(new InputStreamReader(in));
        dmap_server_writer = new PrintWriter(out);
    }

    @Override
    public void run() {
        try {
            dmap_mailboxServer = new ServerSocket(config.getInt("dmap.tcp.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            dmtp_mailboxServer = new ServerSocket(config.getInt("dmtp.tcp.port"));
        } catch (IOException e){
            e.printStackTrace();
        }

        while (true) {
            try {

                //DMAP
                System.out.println("Server is UP and listening on port: " + dmap_mailboxServer.getLocalPort());
                Socket dmap_newClient = dmap_mailboxServer.accept();
                dmap_threadPool = Executors.newCachedThreadPool();
                dmap_threadPool.submit(new DmapServerThread(dmap_newClient, config));

                //DMTP
                System.out.println("DMTP Server is UP and listening on port " + dmtp_mailboxServer.getLocalPort());
                Socket dmtp_newClient = dmtp_mailboxServer.accept();
                dmap_threadPool = Executors.newCachedThreadPool();
                dmap_threadPool.submit(new DmtpServerThread(dmtp_newClient, config));


                // closing or shutdown
            } catch (IOException e) {
                e.printStackTrace();
                shutdown();
            }

        }
    }

    @Override
    public void shutdown() {
        // TODO
    }

    public static void main(String[] args) throws Exception {
        IMailboxServer server = ComponentFactory.createMailboxServer(args[0], System.in, System.out);
        server.run();
    }
}
