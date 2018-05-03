package ch.ebu.zookeeper.fwk;

import lombok.Data;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public abstract class ZKWatcher implements Watcher, StatCallback {
    private CountDownLatch latch;

    public ZKWatcher() {
        latch = new CountDownLatch(1);
    }

    public abstract void processResult(int rc, String path, Object ctx, Stat stat);

    public void process(WatchedEvent event) {
        latch.countDown();
    }

    public void await() throws InterruptedException {
        latch.await();
    }

}