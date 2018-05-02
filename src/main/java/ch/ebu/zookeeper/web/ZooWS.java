package ch.ebu.zookeeper.web;

import ch.ebu.zookeeper.repository.ZookeeperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/{path}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = "application/json; application/xml")
public class ZooWS {

    private final ZookeeperRepository zookeeperRepository;

    @Autowired
    public ZooWS(ZookeeperRepository zookeeperRepository) {
        this.zookeeperRepository = zookeeperRepository;
    }

    @GetMapping()
    public ZooResponseDTO get(@PathVariable("path") String path) {
        String data = zookeeperRepository.get("/" + path, true);
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Node " + path + " value : " + data);
        return zooDTO;
    }

    @PostMapping
    public ZooResponseDTO post(@PathVariable("path") String path, @RequestBody ZooBodyDTO zooBodyDTO) {
        zookeeperRepository.create("/" + path, zooBodyDTO.getZnodevalue().getBytes());
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Node " + path + " created with value : " + zooBodyDTO.getZnodevalue());
        return zooDTO;
    }

    @PutMapping
    public ZooResponseDTO put(@PathVariable("path") String path, @RequestBody ZooBodyDTO zooBodyDTO) {
        zookeeperRepository.update("/" + path, zooBodyDTO.getZnodevalue().getBytes());
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Node " + path + " modified with value : " + zooBodyDTO.getZnodevalue());
        return zooDTO;
    }

    @DeleteMapping
    public ZooResponseDTO delete(@PathVariable("path") String path) {
        zookeeperRepository.delete("/" + path);
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Node " + path + " deleted");
        return zooDTO;
    }

}
