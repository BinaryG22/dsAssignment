package dslab.monitoring;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import at.ac.tuwien.dsg.orvell.annotation.Command;
import dslab.ComponentFactory;
import dslab.shell.IShell;
import dslab.util.Config;

public class MonitoringServer implements IMonitoringServer {

    private Config config;
    private DatagramSocket datagramSocket;
    public static ConcurrentHashMap<String, Integer> adresses;
    public static ConcurrentHashMap<String, Integer> servers;
    private static AtomicInteger count_adressList = new AtomicInteger(0);
    private static AtomicInteger count_serverList = new AtomicInteger(0);
    private PrintWriter out ;
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
        adresses = new ConcurrentHashMap<>();
        servers = new ConcurrentHashMap<>();
        this.out = new PrintWriter(out);

    }

    public static synchronized void  addToListOfAddresses(String sender) {
        if (!adresses.containsKey(sender)){
            adresses.put(sender, 1);
        }else {

            int currentCount = adresses.get(sender);
            int newValue = currentCount + 1;
            System.out.println("newValue = " + newValue);
            adresses.replace(sender, newValue);
        }
    }

    public static synchronized void addToListOfServices(InetAddress address, int port) {
        String key = address + ":" + port;
        if (!servers.containsKey(key)){
            servers.put(key, 1);
        }else {
            int currentCount = servers.get(key);
            int newValue = currentCount + 1;
            System.out.println("newValue = " + newValue);
            servers.replace(key, newValue);
        }
    }

    @Override
    public void run() {
        try {
            // constructs a datagram socket and binds it to the specified port
            datagramSocket = new DatagramSocket(config.getInt("udp.port"));

            System.out.println("Monitoring Server is UP and listening on port: " + datagramSocket.getLocalPort());


            // create a new thread to listen for incoming packets
            new ListenerThread(datagramSocket).start();
        } catch (IOException e) {
            throw new RuntimeException("Cannot listen on UDP port.", e);
        }

        // close socket and listening thread
        //close();
    }

    @Command
    @Override
    public void addresses() {
        // TODO
        for (String key: adresses.keySet()){
            System.out.println(key +" "+adresses.get(key));
        }

        for (String key: adresses.keySet()){
            out.println(key + " " + adresses.get(key));
            out.flush();
        }
    }

    public static void DEBUG_ADRESSES(){
        // TODO
        for (String key: adresses.keySet()){
            System.out.println(key +" "+adresses.get(key));
        }
    }

    public static void DEBUG_SERVERS(){
        for (String key: servers.keySet()){
            System.out.println(key +" "+servers.get(key));
        }
    }

    @Command
    @Override
    public void servers() {
        for (String key: servers.keySet()){
            System.out.println(key +" "+servers.get(key));
        }


        for (String key: servers.keySet()){
            out.println(key + " " + servers.get(key));
            out.flush();
        }
    }

    @Override
    public void shutdown() {
        // TODO
    }


    public void close() {
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

        IShell shell = ComponentFactory.createShellExample(args[0], System.in, System.out);
        shell.run();
    }

}
