package dslab.transfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dslab.ComponentFactory;
import dslab.DMTP.DmtpServerThread;
import dslab.util.Config;

public class TransferServer implements ITransferServer, Runnable {
    /*
    TCP
     */
    private ServerSocket tcp_server = null;
    private Socket tcp_clientSocket = null;
    private BufferedReader server_reader;
    private PrintWriter server_writer;
    private ExecutorService threadPool;

    //save a Buffer for handling saving (or better preparing or creating) messages and forwarding messages
    /*
    TCP
     */
    private Config config;


    /**
     * Creates a new server instance.
     *
     * @param componentId the id of the component that corresponds to the Config resource
     * @param config      the component config
     * @param in          the input stream to read console input from
     * @param out         the output stream to write console output to
     */
    public TransferServer(String componentId, Config config, InputStream in, PrintStream out) {
        this.config = config;
        server_reader = new BufferedReader(new InputStreamReader(in));
        server_writer = new PrintWriter(out);
    }

    @Override
    public void run() {
        try {
            tcp_server = new ServerSocket(config.getInt("tcp.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {


                System.out.println("Server is UP and listening on port: " + tcp_server.getLocalPort());
                Socket newClient = tcp_server.accept();
                threadPool = Executors.newCachedThreadPool();
                threadPool.submit(new DmtpServerThread(newClient, config));


                // closing or shutdown
            } catch (IOException e) {
                e.printStackTrace();
                shutdown();
            }

        }
    }

    @Override
    public void shutdown() {
        threadPool.shutdownNow();
        try {
            tcp_server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        ITransferServer server = ComponentFactory.createTransferServer(args[0], System.in, System.out);
        server.run();
    }

}
