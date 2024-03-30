package cloud.qasino.games.library.marvel.properties;

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

    private final String BASE_URL = "https://gateway.marvel.com/v1/public/";
    private final String API_KEY = "37d075af85235a76c92b60dcc53bdf38";
    private final String PRIVATE_KEY = "ad760beea0edbe11702b1928b6974d13bcdc6841";

    @NotEmpty
    private String scheme = "https://";
    @NotEmpty
    private String host = "gateway.marvel.com/v1/public/";
    @NotEmpty
    private String publicKey = API_KEY;
    @NotEmpty
    private String privateKey= PRIVATE_KEY;

    @Valid
    private Characters characters = new Characters();

    @Data
    public static class Characters {
        @NotEmpty
        private String path = "/characters";
    }
}
