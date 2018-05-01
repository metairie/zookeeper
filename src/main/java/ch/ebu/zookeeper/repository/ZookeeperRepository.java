package ch.ebu.zookeeper.repository;

import ch.ebu.zookeeper.configuration.ZookeeperProperties;
import ch.ebu.zookeeper.fwk.ZKWatcher;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Repository
public class ZookeeperRepository {

    @Autowired
    private final ZookeeperProperties zookeeperProperties;

    private final CountDownLatch connectionLatch = new CountDownLatch(1);
    private ZooKeeper zookeeper;

    public ZookeeperRepository(ZookeeperProperties zookeeperProperties) throws IOException, InterruptedException {
        this.zookeeperProperties = zookeeperProperties;
        zookeeper = new ZooKeeper(zookeeperProperties.getUrl(), 2000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == Event.KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        });
        connectionLatch.await();
    }

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zookeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public Stat getZNodeStats(String path) throws KeeperException, InterruptedException {
        Stat stat = zookeeper.exists(path, true);
        if (stat != null) {
            System.out.println("Node exists and the node version is " + stat.getVersion());
        } else {
            System.out.println("Node does not exists");
        }
        return stat;
    }

    /**
     * Close the zookeeper connection
     */
    public void closeConnection() {
        try {
            zookeeper.close();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public Object getZNodeData(String path, boolean watchFlag) throws KeeperException, InterruptedException {
        try {
            Stat stat = getZNodeStats(path);
            byte[] b = null;
            if (stat != null) {
                if (watchFlag) {
                    ZKWatcher watch = new ZKWatcher();
                    b = zookeeper.getData(path, watch, null);
                    watch.await();
                } else {
                    b = zookeeper.getData(path, null, null);
                }
                return new String(b, "UTF-8");
            } else {
                System.out.println("Node does not exists");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void update(String path, byte[] data) throws KeeperException, InterruptedException {
        int version = zookeeper.exists(path, true).getVersion();
        zookeeper.setData(path, data, version);
    }

    public List<String> getZNodeChildren(String path) throws KeeperException, InterruptedException {
        Stat stat = getZNodeStats(path);
        List<String> children = null;

        if (stat != null) {
            children = zookeeper.getChildren(path, false);
            for (int i = 0; i < children.size(); i++)
                System.out.println(children.get(i));

        } else {
            System.out.println("Node does not exists");
        }
        return children;
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        int version = zookeeper.exists(path, true).getVersion();
        zookeeper.delete(path, version);
    }
}
