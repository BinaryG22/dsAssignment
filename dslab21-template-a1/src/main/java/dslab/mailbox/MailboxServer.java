package dslab.mailbox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import dslab.ComponentFactory;
import dslab.mailbox.dmap.DmapListener;
import dslab.mailbox.dmtp.DmtpListener;
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
    private String component_id;
    /*
    DMTP
     */

    private Config config;

    private static  ConcurrentHashMap<Integer, String[]> concurrentHashMap_messages;

   private static AtomicInteger hashMap_id = new AtomicInteger(1);

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
        this.component_id = componentId;
        dmap_server_reader = new BufferedReader(new InputStreamReader(in));
        dmap_server_writer = new PrintWriter(out);
        concurrentHashMap_messages = new ConcurrentHashMap<>();
    }

    public static synchronized void saveMessageInHashMap(String[] messageForMailboxServer) {
        int key = hashMap_id.get();
        concurrentHashMap_messages.put(key, messageForMailboxServer);
        System.out.println("hashmap:" + concurrentHashMap_messages.toString());
        hashMap_id = new AtomicInteger(hashMap_id.incrementAndGet());
        System.out.println("new hasmap id: " + hashMap_id);
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

            System.out.println("DMAP Server is UP and listening on port: " + dmap_mailboxServer.getLocalPort());
            Thread dmapListener = new DmapListener(dmap_mailboxServer, config);
            dmapListener.start();

            //DMTP
            System.out.println("DMTP Server is UP and listening on port " + dmtp_mailboxServer.getLocalPort());
            Thread dmtpListener = new DmtpListener(dmtp_mailboxServer, config);
            dmtpListener.start();

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
