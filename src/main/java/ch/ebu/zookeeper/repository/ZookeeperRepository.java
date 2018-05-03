package ch.ebu.zookeeper.repository;

import ch.ebu.zookeeper.configuration.ZookeeperProperties;
import ch.ebu.zookeeper.fwk.ZKWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Repository
public class ZookeeperRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperRepository.class);
    private final CountDownLatch connectionLatch = new CountDownLatch(1);
    private ZooKeeper zookeeper;

    @Autowired
    public ZookeeperRepository(ZookeeperProperties zookeeperProperties) throws IOException, InterruptedException {
        zookeeper = new ZooKeeper(zookeeperProperties.getUrl(), zookeeperProperties.getTimeout(), watchedEvent -> {
            connectionLatch.countDown();
        });
        connectionLatch.await();
    }

    /**
     * @param path      url path
     * @param watchFlag if we want to watch the value
     * @return value
     */
    public String get(String path, boolean watchFlag) {
        byte[] b;
        try {
            if (watchFlag) {
                b = zookeeper.getData(path, new ZKWatcher() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, Stat stat) {
                        LOG.info("process result ZK repo !");
                    }
                }, null);
            } else {
                b = zookeeper.getData(path, null, null);
            }
            return new String(b, "UTF-8");
        } catch (UnsupportedEncodingException | KeeperException | InterruptedException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return "";
    }

    /**
     * Create a ZNode
     *
     * @param path       url path
     * @param znodevalue request body {"znodevalue": "bla bla bla bla"}
     */
    public void create(String path, byte[] znodevalue) {
        try {
            Stat stat = getZNodeStats(path);
            zookeeper.create(path, znodevalue, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    /**
     * update a ZNode
     *
     * @param path       url path
     * @param znodevalue request body {"znodevalue": "bla bla bla bla"}
     */
    public void update(String path, byte[] znodevalue) {
        try {
            int version = zookeeper.exists(path, true).getVersion();
            zookeeper.setData(path, znodevalue, version);
        } catch (KeeperException | InterruptedException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    /**
     * delete a ZNode
     *
     * @param path url path
     */
    public void delete(String path) {
        try {
            int version = zookeeper.exists(path, true).getVersion();
            zookeeper.delete(path, version);
        } catch (KeeperException | InterruptedException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    /**
     * Close the zookeeper connection
     */
    public void closeConnection() {
        try {
            zookeeper.close();
        } catch (InterruptedException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    public List<String> getZNodeChildren(String path) throws KeeperException, InterruptedException {
        Stat stat = getZNodeStats(path);
        List<String> children = null;

        if (stat != null) {
            children = zookeeper.getChildren(path, false);
            for (String aChildren : children) System.out.println(aChildren);
        } else {
            LOG.info("Node does not exist");
        }
        return children;
    }

    private Stat getZNodeStats(String path) throws KeeperException, InterruptedException {
        Stat stat = zookeeper.exists(path, true);
        if (stat != null) {
            LOG.info("Node exists and the node version is " + stat.getVersion());
        } else {
            LOG.info("Node does not exist");
        }
        return stat;
    }

}
