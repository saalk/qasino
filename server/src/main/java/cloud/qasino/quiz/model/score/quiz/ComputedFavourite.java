
package cloud.qasino.quiz.model.score.quiz;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "favorited",
    "favoritesCount"
})
@Generated("jsonschema2pojo")
public class ComputedFavourite {

    @JsonProperty("favorited")
    private Boolean favorited;
    @JsonProperty("favoritesCount")
    private Integer favoritesCount;

}
