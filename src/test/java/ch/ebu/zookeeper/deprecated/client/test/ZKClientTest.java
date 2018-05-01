package ch.ebu.zookeeper.deprecated.client.test;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ZKClientTest {
/*
    private static ZKClientManagerImpl zkmanager = new ZKClientManagerImpl();

    private String path = "/Node1";

    byte[] data = "www.java.globinch.com ZK Client Data".getBytes();

    @Before
    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    @Test
    public void testCreate() throws KeeperException, InterruptedException {
        // data in byte array

        zkmanager.create(path, data);
        Stat stat = zkmanager.getZNodeStats(path);
        assertNotNull(stat);
        zkmanager.delete(path);
    }

    @Test
    public void testGetZNodeStats() throws KeeperException,
            InterruptedException {
        zkmanager.create(path, data);
        Stat stat = zkmanager.getZNodeStats(path);
        assertNotNull(stat);
        assertNotNull(stat.getVersion());
        zkmanager.delete(path);
    }

    @Test
    public void testGetZNodeData() throws KeeperException, InterruptedException {
        zkmanager.create(path, data);
        String data = (String) zkmanager.getZNodeData(path, false);
        assertNotNull(data);
        zkmanager.delete(path);
    }

    @Test
    public void testUpdate() throws KeeperException, InterruptedException {
        zkmanager.create(path, data);
        String data = "www.java.globinch.com Updated Data";
        byte[] dataBytes = data.getBytes();
        zkmanager.update(path, dataBytes);
        String retrivedData = (String) zkmanager.getZNodeData(path, false);
        assertNotNull(retrivedData);
        zkmanager.delete(path);
    }

    @Test
    public void testGetZNodeChildren() throws KeeperException, InterruptedException {
        zkmanager.create(path, data);
        List<String> children = zkmanager.getZNodeChildren(path);
        assertNotNull(children);
        zkmanager.delete(path);
    }


    public void testDelete() throws KeeperException, InterruptedException {
        zkmanager.create(path, data);
        zkmanager.delete(path);
        Stat stat = zkmanager.getZNodeStats(path);
        assertNull(stat);
    }
*/
}