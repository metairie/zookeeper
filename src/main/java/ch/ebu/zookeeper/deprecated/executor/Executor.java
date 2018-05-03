package ch.ebu.zookeeper.deprecated.executor;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * A simple example program to use DataMonitor to start and
 * stop executables based on a znode. The program watches the
 * specified znode and saves the data that corresponds to the
 * znode in the filesystem. It also starts the specified program
 * with the specified arguments when the znode exists and kills
 * the program if the znode goes away.
 */
public class Executor implements Watcher, Runnable, DataMonitor.DataMonitorListener {
    DataMonitor dm;
    ZooKeeper zk;
    String filename;
    String exec[];
    Process child;

    public Executor(String hostPort, String znode, String filename, String exec[]) throws KeeperException, IOException {
        this.filename = filename;
        this.exec = exec;
        zk = new ZooKeeper(hostPort, 3000, this);
        dm = new DataMonitor(zk, znode, null, this);
    }

    public static void main(String[] args) {
        System.out.println("main executor");
        if (args.length < 4) {
            System.err.println("USAGE: Executor hostPort znode filename program [args ...]");
            System.exit(2);
        }
        String hostPort = args[0];
        String znode = args[1];
        String filename = args[2];
        String exec[] = new String[args.length - 3];
        System.arraycopy(args, 3, exec, 0, exec.length);
        try {
            new Executor(hostPort, znode, filename, exec).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // watcher
    public void process(WatchedEvent event) {
        dm.process(event);
    }

    public void run() {
        System.out.println("executor.run");
        try {
            synchronized (this) {
                System.out.println("just before wait()");
                while (!dm.dead) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("executor EXCEPTION INTERRUPTED" + e.getLocalizedMessage());
        }
    }

    public void closing(int rc) {
        System.out.println("executor.closing");
        synchronized (this) {
            notifyAll();
        }
    }

    public void exists(byte[] data) {
        System.out.println("executor.exists");
        if (data == null) {
            if (child != null) {
                System.out.println("Killing process");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                }
            }
            child = null;
        } else {
            if (child != null) {
                System.out.println("Stopping child");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Starting child");
        }
    }
}