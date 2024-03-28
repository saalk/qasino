package cloud.qasino.card.marvel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource {
    private int available;
    private String collectionURI;
    private List<Item> items;
    private int returned;
}
