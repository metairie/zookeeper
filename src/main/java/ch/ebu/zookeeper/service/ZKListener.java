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

@Service
public class ZKListener extends ZKWatcher implements Runnable, ZKMonitorListener {
    private static final Logger LOG = LoggerFactory.getLogger(ZKListener.class);
    private ZKMonitor zkMonitor;
    private ZooKeeper zooKeeper;

    public ZKListener(ZookeeperProperties zookeeperProperties) {
        LOG.info("Zookeeper build service (ZKLISTENER): " + this.toString());
        try {
            zooKeeper = new ZooKeeper(zookeeperProperties.getUrl(), zookeeperProperties.getTimeout(), this);
            zkMonitor = new ZKMonitor(zooKeeper, zookeeperProperties.getRootNode(), null, this);
        } catch (IOException e) {
            LOG.info("executor.run");
        }
    }

    @Override
    public void run() {
        LOG.info("Zookeeper service run");
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
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        LOG.info("watcher.processResult");
    }

    // -- watcher
    @Override
    public void process(WatchedEvent event) {
        LOG.info("watcher.process");
    }

    // -- listener
    @Override
    public void exists(byte[] data) {
        LOG.info("ZKMonitorListener.exists");
    }

    @Override
    public void closing(int rc) {
        LOG.info("ZKMonitorListener.closing");
        synchronized (this) {
            notifyAll();
        }
    }

}
