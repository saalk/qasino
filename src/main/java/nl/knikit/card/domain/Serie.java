package nl.knikit.card.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Serie {
    private int id;
    private String title;
    private String description;
    private int startYear;
    private int endYear;
    private String rating;
    private String type;
    private String modified;
}
