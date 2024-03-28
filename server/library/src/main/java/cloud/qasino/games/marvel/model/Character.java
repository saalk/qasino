package cloud.qasino.games.marvel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Character {
    private int id;
    private String name;
    private String description;
    private String modified;
    private Thumbnail thumbnail;
    private String resourceURI;
    private Resource comics;
    private Resource series;
    private Resource stories;
    private Resource events;
    private List<Url> urls;
}
