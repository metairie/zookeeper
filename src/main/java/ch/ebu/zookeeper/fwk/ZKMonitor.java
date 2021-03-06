package ch.ebu.zookeeper.fwk;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ZKMonitor extends ZKWatcher implements AsyncCallback.StatCallback {
    private static final Logger LOG = LoggerFactory.getLogger(ZKMonitor.class);
    private ZooKeeper zk;
    private String znode;
    private ZKWatcher chainedWatcher;
    private boolean dead;
    private ZKMonitorListener listener;
    private byte prevData[];
    private boolean enabled = true;

    public ZKMonitor(ZooKeeper zk, String znode, ZKWatcher chainedWatcher, ZKMonitorListener listener) {
        this.zk = zk;
        this.znode = znode;
        this.chainedWatcher = chainedWatcher;
        this.listener = listener;
        zk.exists(znode, true, this, null);
    }

    @Override
    public void process(WatchedEvent event) {
        LOG.info("Process method called");
        String path = event.getPath();
        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected:
                    break;
                case Expired:
                    dead = true;
                    listener.closing(KeeperException.Code.SessionExpired);
                    break;
            }
        } else {
            if (path != null && path.equals(znode)) {
                zk.exists(znode, true, this, null);
            }
        }
        if (chainedWatcher != null) {
            chainedWatcher.process(event);
        }
    }

    /**
     * asynch call when zookeeper.exists() is invoked
     **/
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        LOG.info("Process Result - trigger");
        boolean exists;
        switch (rc) {
            case KeeperException.Code.Ok:
                exists = true;
                break;
            case KeeperException.Code.NoNode:
                exists = false;
                break;
            case KeeperException.Code.SessionExpired:
            case KeeperException.Code.NoAuth:
                dead = true;
                listener.closing(rc);
                return;
            default:
                zk.exists(znode, true, this, null);
                return;
        }
        byte b[] = null;
        if (exists) {
            try {
                LOG.info("Try get data from cluster zookeeper");
                b = zk.getData(znode, false, null);
            } catch (InterruptedException | KeeperException e) {
                return;
            }
        }

        if (enabled && ((b != null && prevData != null) && (!Arrays.equals(prevData, b)))) {
            LOG.info("Object exist and has changed. Call listener");
            listener.exists(b);
        }
        prevData = b;
    }

    public boolean isDead() {
        return dead;
    }

    public void enabled() {
        enabled = true;
    }

    public void disabled() {
        enabled = false;
    }
}