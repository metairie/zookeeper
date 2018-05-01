package ch.ebu.zookeeper.fwk;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZKWatcher implements Watcher, StatCallback {
    CountDownLatch latch;

    public ZKWatcher() {
        latch = new CountDownLatch(1);
    }

    public void processResult(int rc, String path, Object ctx, Stat stat) {
    }

    public void process(WatchedEvent event) {
        System.out.println("Watcher fired on path: " + event.getPath() + " state: " + event.getState() + " type " + event.getType());
        latch.countDown();
    }

    public void await() throws InterruptedException {
        latch.await();
    }

}