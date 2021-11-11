package dslab.monitoring;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

import at.ac.tuwien.dsg.orvell.Shell;
import at.ac.tuwien.dsg.orvell.StopShellException;
import at.ac.tuwien.dsg.orvell.annotation.Command;
import dslab.ComponentFactory;

import dslab.util.Config;

public class MonitoringServer implements IMonitoringServer {

    private Config config;
    private DatagramSocket datagramSocket;
    public  ConcurrentHashMap<String, Integer> adresses = new ConcurrentHashMap<>();
    public  ConcurrentHashMap<String, Integer> servers = new ConcurrentHashMap<>();
    private Shell shell;
    /**
     * Creates a new server instance.
     *
     * @param componentId the id of the component that corresponds to the Config resource
     * @param config the component config
     * @param in the input stream to read console input from
     * @param out the output stream to write console output to
     */
    public MonitoringServer(String componentId, Config config, InputStream in, PrintStream out) {
        this.config = config;
        this.shell = new Shell(in, out);
        this.shell.register(this);
    }


    @Override
    public void run() {
        try {
            // constructs a datagram socket and binds it to the specified port
            datagramSocket = new DatagramSocket(config.getInt("udp.port"));

            System.out.println("Monitoring Server is UP and listening on port: " + datagramSocket.getLocalPort());


            // create a new thread to listen for incoming packets
            new ListenerThread(datagramSocket, servers, adresses).start();
        } catch (IOException e) {
            throw new RuntimeException("Cannot listen on UDP port.", e);
        }

        //run shell
        shell.run();

        // close socket and listening thread
        //close();
    }

    @Command
    @Override
    public void addresses() {
        for (String key: adresses.keySet()){
            shell.out().println(key + " " + adresses.get(key));
        }
    }




        @Command
        @Override
        public void servers(){
            for (String key: servers.keySet()){
                shell.out().println(key + " " + servers.get(key));
            }
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
        if (datagramSocket != null) {
            datagramSocket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        IMonitoringServer server = ComponentFactory.createMonitoringServer(args[0], System.in, System.out);
        server.run();
    }

}
