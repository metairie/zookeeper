/*
 * @(#)ZKManager.java
 * @author Binu George
 * Globinch.com
 * copyright https://www.java.globinch.com. All rights reserved.
 */
package ch.ebu.zookeeper.deprecated;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * ZKManager Interface. Defines the methods to manipulate znodes
 *
 * @author Binu George
 * @version 1.0
 * https://www.java.globinch.com. All rights reserved
 * @since 2016
 */
public interface ZKManager {
    /**
     * Create a Znode and save some data
     *
     * @param path
     * @param data
     * @throws KeeperException
     * @throws InterruptedException
     */
    void create(String path, byte[] data) throws KeeperException, InterruptedException;

    /**
     * Get the ZNode Stats
     *
     * @param path
     * @return Stat
     * @throws KeeperException
     * @throws InterruptedException
     */
    Stat getZNodeStats(String path) throws KeeperException, InterruptedException;

    /**
     * Get ZNode Data
     *
     * @param path
     * @param watchFlag
     * @throws KeeperException
     * @throws InterruptedException
     */
    Object getZNodeData(String path, boolean watchFlag) throws KeeperException, InterruptedException;

    /**
     * Update the ZNode Data
     *
     * @param path
     * @param data
     * @throws KeeperException
     * @throws InterruptedException
     */
    void update(String path, byte[] data) throws KeeperException, InterruptedException;

    /**
     * Get ZNode children
     *
     * @param path
     * @throws KeeperException
     * @throws InterruptedException return List<String>
     */
    List<String> getZNodeChildren(String path) throws KeeperException, InterruptedException;

    /**
     * Delete the znode
     *
     * @param path
     * @throws KeeperException
     * @throws InterruptedException
     */
    void delete(String path) throws KeeperException, InterruptedException;
}