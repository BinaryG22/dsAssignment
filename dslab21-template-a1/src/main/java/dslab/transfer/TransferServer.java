package dslab.transfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.ac.tuwien.dsg.orvell.Shell;
import at.ac.tuwien.dsg.orvell.StopShellException;
import at.ac.tuwien.dsg.orvell.annotation.Command;
import dslab.ComponentFactory;
import dslab.util.Config;

public class TransferServer implements ITransferServer, Runnable {
    private String component_id;
    private Config config;
    private ServerSocket tcp_server = null;
private Shell shell;


    /**
     * Creates a new server instance.
     *
     * @param componentId the id of the component that corresponds to the Config resource
     * @param config      the component config
     * @param in          the input stream to read console input from
     * @param out         the output stream to write console output to
     */
    public TransferServer(String componentId, Config config, InputStream in, PrintStream out) {
        this.component_id = componentId;
        this.config = config;
        this.shell = new Shell(in, out);
        this.shell.register(this);
    }

    @Override
    public void run() {
        try {
            tcp_server = new ServerSocket(config.getInt("tcp.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Transfer Server is UP and listening on port: " + tcp_server.getLocalPort());

        new ListenerThread(tcp_server, config).start();

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
        if (tcp_server != null) {
            try {
                tcp_server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ITransferServer server = ComponentFactory.createTransferServer(args[0], System.in, System.out);
        server.run();


    }

}
