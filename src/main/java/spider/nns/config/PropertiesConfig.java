package spider.nns.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "spider.nns")
public class PropertiesConfig {
    private String connectURL;
}
