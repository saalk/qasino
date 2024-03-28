package cloud.qasino.games.marvel.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix="marvel")
@PropertySource("classpath:marvel.properties")
@Validated
public class MarvelProperties {

    @NotEmpty
    private String scheme;
    @NotEmpty
    private String host;
    @NotEmpty
    private String publicKey;
    @NotEmpty
    private String privateKey;

    @Valid
    private Characters characters = new Characters();

    @Data
    public static class Characters {
        @NotEmpty
        private String path;
    }
}
