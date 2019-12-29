package cloud.qasino.card.domain.marvel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Url {
    private String type;
    private String url;
}
