package ch.ebu.zookeeper.configuration;

import lombok.Data;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Configuration
@Data
public class ZKConnection {

    // ZooKeeper ensemble
    private ZooKeeper zookeeper;
    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    public ZKConnection() {
    }

    /**
     * Initialize the Zookeeper connection
     *
     * @param host
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public ZooKeeper connect(String host) throws IOException, InterruptedException {
        zookeeper = new ZooKeeper(host, 2000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        });

        connectionLatch.await();
        return zookeeper;
    }

    /**
     * Method to disconnect from zookeeper server
     *
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        zookeeper.close();
    }

}