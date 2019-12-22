package cloud.qasino.card.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("marvel")
public class MarvelServiceProperties {
    private String scheme;
    private String host;
    private String publicKey;
    private String privateKey;
    private Characters characters = new Characters();

    @Data
    public static class Characters {
        private String path;
    }
}
