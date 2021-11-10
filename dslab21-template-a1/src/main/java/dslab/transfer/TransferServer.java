package dslab.transfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dslab.ComponentFactory;
import dslab.shell.IShell;
import dslab.transfer.server.Transfer_DmtpServerThread;
import dslab.util.Config;

public class TransferServer implements ITransferServer, Runnable {
    private String component_id;
    private Config config;
    private ServerSocket tcp_server = null;



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
    }

    @Override
    public void shutdown() {

    }

    public static void main(String[] args) throws Exception {
        ITransferServer server = ComponentFactory.createTransferServer(args[0], System.in, System.out);
        server.run();

        System.out.println("config id/name: " + args[0]);
        IShell shell = ComponentFactory.createShellExample(args[0], System.in, System.out);
        shell.run();

    }

}
