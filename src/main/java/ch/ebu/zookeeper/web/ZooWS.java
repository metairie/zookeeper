package ch.ebu.zookeeper.web;

import ch.ebu.zookeeper.repository.ZookeeperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/{root}/**", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = "application/json; application/xml")
public class ZooWS {

    private static final Logger LOG = LoggerFactory.getLogger(ZooWS.class);
    private final ZookeeperRepository zookeeperRepository;

    @Autowired
    public ZooWS(ZookeeperRepository zookeeperRepository) {
        this.zookeeperRepository = zookeeperRepository;
    }

    @GetMapping()
    public ZooResponseDTO get(@PathVariable("root") String root, HttpServletRequest request) {
        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        LOG.info("GET " + restOfTheUrl);
        String data = zookeeperRepository.get(restOfTheUrl, true);
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Node " + restOfTheUrl + " value : " + data);
        return zooDTO;
    }

    @PostMapping
    public ZooResponseDTO post(@PathVariable("root") String root, HttpServletRequest request, @RequestBody ZooBodyDTO zooBodyDTO) {
        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        LOG.info("POST " + restOfTheUrl);
        zookeeperRepository.create(restOfTheUrl, zooBodyDTO.getZnodevalue().getBytes());
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Node " + restOfTheUrl + " created with value : " + zooBodyDTO.getZnodevalue());
        return zooDTO;
    }

    @PutMapping
    public ZooResponseDTO put(@PathVariable("root") String root, HttpServletRequest request, @RequestBody ZooBodyDTO zooBodyDTO) {
        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        LOG.info("PUT " + restOfTheUrl);
        zookeeperRepository.update(restOfTheUrl, zooBodyDTO.getZnodevalue().getBytes());
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Node " + restOfTheUrl + " modified with value : " + zooBodyDTO.getZnodevalue());
        return zooDTO;
    }

    @DeleteMapping
    public ZooResponseDTO delete(@PathVariable("root") String root, HttpServletRequest request) {
        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        LOG.info("DEL " + restOfTheUrl);
        zookeeperRepository.delete(restOfTheUrl);
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Node " + restOfTheUrl + " deleted");
        return zooDTO;
    }

}
