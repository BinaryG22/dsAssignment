package dslab.shell;

import at.ac.tuwien.dsg.orvell.annotation.Command;

public interface IShell extends Runnable{

    /**
     * Performs a shutdown and a release of all resources.
     */
    void shutdown();

    void test();


    @Command
    void servers();
}
