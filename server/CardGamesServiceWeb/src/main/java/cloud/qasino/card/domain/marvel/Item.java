package cloud.qasino.card.domain.marvel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    private String resourceURI;
    private String name;
    private String type;
}