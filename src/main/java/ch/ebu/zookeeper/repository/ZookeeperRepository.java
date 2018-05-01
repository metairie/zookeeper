package ch.ebu.zookeeper.repository;

import ch.ebu.zookeeper.configuration.ZKConnection;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ZookeeperRepository {

    @Autowired
    private static ZKConnection zkConnection;

    public void create(String path, byte[] data) throws KeeperException, InterruptedException {

    }

    public Stat getZNodeStats(String path) throws KeeperException, InterruptedException {
        return null;
    }

    public Object getZNodeData(String path, boolean watchFlag) throws KeeperException, InterruptedException {
        return null;
    }

    public void update(String path, byte[] data) throws KeeperException, InterruptedException {

    }

    public List<String> getZNodeChildren(String path) throws KeeperException, InterruptedException {
        return null;
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        ZooKeeper zookeeper = zkConnection.getZookeeper();
        int version = zookeeper.exists(path, true).getVersion();
        zookeeper.delete(path, version);
    }
}
