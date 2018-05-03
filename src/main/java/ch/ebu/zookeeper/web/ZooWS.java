package ch.ebu.zookeeper.web;

import ch.ebu.zookeeper.repository.ZookeeperRepository;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/Node1", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = "application/json; application/xml")
public class ZooWS {

    @Autowired
    ZookeeperRepository zookeeperRepository;

    @GetMapping
    public ZooDTO get() {

        // get Node1
        String data = null;
        try {
            data = (String) zookeeperRepository.getZNodeData("/Node1", false);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        ZooDTO zooDTO = new ZooDTO();
        zooDTO.setResponse("value = " + data);
        return new ZooDTO();
    }

}
