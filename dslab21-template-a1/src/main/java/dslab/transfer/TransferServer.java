package dslab.transfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dslab.ComponentFactory;
import dslab.transfer.tcp.ServerThread;
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
            threadPool = Executors.newFixedThreadPool(10);
            threadPool.execute(new ServerThread(tcp_server));

            System.out.println("Server is UP and listening on port: " + tcp_server.getLocalPort());

            try {
                server_reader.readLine();
            } catch (IOException e) {

            }

            // closing or shutdown
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void shutdown() {
        // TODO
    }

    public static void main(String[] args) throws Exception {
        ITransferServer server = ComponentFactory.createTransferServer(args[0], System.in, System.out);
        server.run();
    }

}
