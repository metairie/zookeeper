package ch.ebu.zookeeper.service;

import ch.ebu.zookeeper.configuration.ZookeeperProperties;
import ch.ebu.zookeeper.fwk.ZKMonitor;
import ch.ebu.zookeeper.fwk.ZKMonitorListener;
import ch.ebu.zookeeper.fwk.ZKWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Basic Service Class for zookeeper client
 * allow
 * - connection mgmt
 * - watch znode changes
 */

@Service
public class ZookeeperService extends ZKWatcher implements Runnable, ZKMonitorListener {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperService.class);
    private ZKMonitor zkMonitor;
    private ZooKeeper zooKeeper;

    public ZookeeperService(ZookeeperProperties zookeeperProperties) {
        try {
            zooKeeper = new ZooKeeper(zookeeperProperties.getUrl(), zookeeperProperties.getTimeout(), this);
            zkMonitor = new ZKMonitor(zooKeeper, zookeeperProperties.getRootNode(), null, this);
        } catch (IOException e) {
            LOG.info("executor.run");
        }
    }

    @Override
    public void run() {
        LOG.info("executor.run");
        try {
            synchronized (this) {
                while (!zkMonitor.isDead()) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void exists(byte[] data) {

    }

    @Override
    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }

    @Override
    public void process(WatchedEvent event) {
        zkMonitor.process(event);
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

}
