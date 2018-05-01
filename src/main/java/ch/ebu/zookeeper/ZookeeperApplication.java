package ch.ebu.zookeeper;

import ch.ebu.zookeeper.deprecated.ZKClientManagerImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZookeeperApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ZookeeperApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ZKClientManagerImpl zkmanager = new ZKClientManagerImpl();

        // get Node1
        String data = (String) zkmanager.getZNodeData("/Node1", false);

        byte[] d = "test".getBytes();
        zkmanager.create( "/Node1", d);
        System.out.println("Value =" + data);

    }
}
