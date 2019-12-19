package nl.knikit.card.domain;

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
