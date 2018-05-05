package ch.ebu.zookeeper.web;

import ch.ebu.zookeeper.repository.ZookeeperRepository;
import ch.ebu.zookeeper.web.DTO.ZooBodyDTO;
import ch.ebu.zookeeper.web.DTO.ZooResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/queue/{rootNode}/**", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = "application/json; application/xml")
public class ZooQueueWS {

    private static final Logger LOG = LoggerFactory.getLogger(ZooQueueWS.class);
    private final ZookeeperRepository zookeeperRepository;

    @Autowired
    public ZooQueueWS(ZookeeperRepository zookeeperRepository) {
        this.zookeeperRepository = zookeeperRepository;
    }

    @PostMapping
    public ZooResponseDTO post(@PathVariable("rootNode") String rootNode, HttpServletRequest request) {
        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String uri = request.getRequestURI();
        LOG.info("POST " + uri);
        zookeeperRepository.register(restOfTheUrl);
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Register to Queue " + restOfTheUrl);
        return zooDTO;
    }

    @DeleteMapping
    public ZooResponseDTO delete(@PathVariable("rootNode") String rootNode, HttpServletRequest request) {
        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String uri = request.getRequestURI();
        LOG.info("DEL " + uri);
        zookeeperRepository.unregister(restOfTheUrl);
        ZooResponseDTO zooDTO = new ZooResponseDTO();
        zooDTO.setResponse("Unregister to Queue " + restOfTheUrl);
        return zooDTO;
    }

}