package dslab.mailbox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import at.ac.tuwien.dsg.orvell.Shell;
import at.ac.tuwien.dsg.orvell.StopShellException;
import at.ac.tuwien.dsg.orvell.annotation.Command;
import dslab.ComponentFactory;
import dslab.mailbox.dmap.DmapListener;
import dslab.mailbox.dmtp.DmtpListener;
import dslab.util.Config;

public class MailboxServer implements IMailboxServer, Runnable {
    /*
    DMAP
     */
    private ServerSocket dmap_mailboxServer = null;
    /*
    DMAP
     */

    /*
    DMTP
     */
    private ServerSocket dmtp_mailboxServer = null;
    /*
    DMTP
     */

    private Config config;
    private Shell shell;

    public static ConcurrentHashMap<String, ConcurrentHashMap<Integer, String[]>> getConcurrentHashMap_messages() {
        return concurrentHashMap_messages;
    }

    //outer hashmap maps user to hashmap
    // inner hashmap maps id to message
    private static  ConcurrentHashMap<String, ConcurrentHashMap<Integer, String[]>> concurrentHashMap_messages;

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
        concurrentHashMap_messages = new ConcurrentHashMap<>();
        this.shell = new Shell(in, out);
        this.shell.register(this);
    }

    public static synchronized void saveMessageInHashMap(ArrayList<String> users, String sender, String subject, String data) {
        int key = hashMap_id.get();

        for (String user: users
             ) {
            if (!concurrentHashMap_messages.containsKey(user)) {
                concurrentHashMap_messages.put(user, new ConcurrentHashMap<Integer, String[]>());
            }
            concurrentHashMap_messages.get(user).put(key, new String[]{sender, subject, data});
        }

        hashMap_id = new AtomicInteger(hashMap_id.incrementAndGet());


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

            shell.run();

    }

    @Command
    @Override
    public void shutdown() {
        close();
        throw new StopShellException();
    }

    private void close() {


        /*
         * Note that closing the socket also triggers an exception in the
         * listening thread
         */
        if (dmtp_mailboxServer != null) {
            try {
                dmtp_mailboxServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (dmap_mailboxServer != null){
            try {
                dmap_mailboxServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        IMailboxServer server = ComponentFactory.createMailboxServer(args[0], System.in, System.out);
        server.run();


    }
}
