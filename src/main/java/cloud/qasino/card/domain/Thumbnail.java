package cloud.qasino.card.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Thumbnail {
    private String path;
    private String extension;
}
