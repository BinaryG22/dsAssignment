package dslab.shell;

import at.ac.tuwien.dsg.orvell.annotation.Command;
import dslab.monitoring.MonitoringServer;
import dslab.util.Config;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Shell implements IShell, Runnable{


    private Config config;
    private at.ac.tuwien.dsg.orvell.Shell shell;
    private String loggedInUser = null;
    PrintWriter out;

    public Shell(String componentId, Config config, InputStream inputStream, PrintStream outputStream) {
        this.config = config;
        this.out = new PrintWriter(outputStream);

        /*
         * First, create a new Shell instance and provide an InputStream to read from,
         * as well as an OutputStream to write to. If you want to test the application
         * manually, simply use System.in and System.out.
         */
        shell = new at.ac.tuwien.dsg.orvell.Shell(inputStream, outputStream);
        /*
         * Next, register all commands the Shell should support. In this example
         * this class implements all desired commands.
         */
        shell.register(this);

        /*
         * The prompt of a shell is just a visual aid that indicates that the shell
         * can read a command. Note that the prompt may not be output correctly when
         * running the application via ant.
         */
        shell.setPrompt(componentId + "> ");
    }

    @Override
    public void run() {
        /*
         * Finally, make the Shell process the commands read from the
         * InputStream by invoking Shell.run(). Note that Shell implements the
         * Runnable interface, so you could theoretically run it in a new thread.
         * However, it is typically desirable to have one process blocking the main
         * thread. Reading from System.in (which is what the Shell does) is a good
         * candidate for this.
         */
        shell.run();

        /*
         * The run method blocks until the read loop exits. To exit the loop
         * programmatically, a Command method may throw a StopShellException, which is
         * caught inside the Shell run method, causing the loop to break gracefully.
         */
        System.out.println("Exiting the shell, bye!");
    }

    @Override
    public void shutdown() {

    }

    @Command
    @Override
    public void test() {
        System.out.println("testing shell");
    }

    @Command
    @Override
    public void servers(){
        for (String key: MonitoringServer.servers.keySet()){
            out.println(key + " " + MonitoringServer.servers.get(key));
            out.flush();
        }
    }
}
