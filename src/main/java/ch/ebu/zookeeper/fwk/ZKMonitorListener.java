package ch.ebu.zookeeper.fwk;

public interface ZKMonitorListener {
    void exists(byte data[]);
    void closing(int rc);
}