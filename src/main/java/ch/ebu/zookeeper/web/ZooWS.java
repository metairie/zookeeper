package ch.ebu.zookeeper.web;

import ch.ebu.zookeeper.deprecated.ZKClientManagerImpl;
import org.apache.zookeeper.KeeperException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/Node1", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = "application/json; application/xml")
public class ZooWS {

    @GetMapping
    public ZooDTO get() {
        ZKClientManagerImpl zkmanager = new ZKClientManagerImpl();

        // get Node1
        String data = null;
        try {
            data = (String) zkmanager.getZNodeData("/Node1", false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Value =" + data);
        return new ZooDTO();
    }

}
