package ch.ebu.zookeeper.fwk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ZKQueue implements Watcher {

    static Integer mutex;

    @Override
    public void process(WatchedEvent watchedEvent) {

    }

}
