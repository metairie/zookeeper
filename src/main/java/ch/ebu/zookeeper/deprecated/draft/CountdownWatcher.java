package ch.ebu.zookeeper.deprecated.draft;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.common.Time;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class CountdownWatcher implements Watcher {
    // XXX this doesn't need to be volatile! (Should probably be final)
    public volatile CountDownLatch clientConnected;
    // Set to true when connected to a read-only server, or a read-write (quorum) server.
    private volatile boolean connected;
    // Set to true when connected to a quorum server.
    private volatile boolean syncConnected;
    // Set to true when connected to a quorum server in read-only mode
    private volatile boolean readOnlyConnected;

    public CountdownWatcher() {
        reset();
    }

    synchronized public void reset() {
        clientConnected = new CountDownLatch(1);
        connected = false;
        syncConnected = false;
        readOnlyConnected = false;
    }

    synchronized public void process(WatchedEvent event) {
        Event.KeeperState state = event.getState();
        if (state == Event.KeeperState.SyncConnected) {
            connected = true;
            syncConnected = true;
            readOnlyConnected = false;
        } else if (state == Event.KeeperState.ConnectedReadOnly) {
            connected = true;
            syncConnected = false;
            readOnlyConnected = true;
        } else {
            connected = false;
            syncConnected = false;
            readOnlyConnected = false;
        }

        notifyAll();
        if (connected) {
            clientConnected.countDown();
        }
    }

    synchronized public boolean isConnected() {
        return connected;
    }

    synchronized public void waitForConnected(long timeout)
            throws InterruptedException, TimeoutException {
        long expire = Time.currentElapsedTime() + timeout;
        long left = timeout;
        while (!connected && left > 0) {
            wait(left);
            left = expire - Time.currentElapsedTime();
        }
        if (!connected) {
            throw new TimeoutException("Failed to connect to ZooKeeper server.");

        }
    }

    synchronized public void waitForSyncConnected(long timeout)
            throws InterruptedException, TimeoutException {
        long expire = Time.currentElapsedTime() + timeout;
        long left = timeout;
        while (!syncConnected && left > 0) {
            wait(left);
            left = expire - Time.currentElapsedTime();
        }
        if (!syncConnected) {
            throw new TimeoutException("Failed to connect to read-write ZooKeeper server.");
        }
    }

    synchronized public void waitForReadOnlyConnected(long timeout)
            throws InterruptedException, TimeoutException {
        long expire = System.currentTimeMillis() + timeout;
        long left = timeout;
        while (!readOnlyConnected && left > 0) {
            wait(left);
            left = expire - System.currentTimeMillis();
        }
        if (!readOnlyConnected) {
            throw new TimeoutException("Failed to connect in read-only mode to ZooKeeper server.");
        }
    }

    synchronized public void waitForDisconnected(long timeout)
            throws InterruptedException, TimeoutException {
        long expire = Time.currentElapsedTime() + timeout;
        long left = timeout;
        while (connected && left > 0) {
            wait(left);
            left = expire - Time.currentElapsedTime();
        }
        if (connected) {
            throw new TimeoutException("Did not disconnect");

        }
    }
}
