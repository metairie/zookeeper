package ch.ebu.zookeeper.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("zookeeper")
@Data
@Configuration
public class ZookeeperProperties {

    private String url;

    private String rootNode;

}
