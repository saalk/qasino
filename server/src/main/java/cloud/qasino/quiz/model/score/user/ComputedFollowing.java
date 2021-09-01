
package cloud.qasino.quiz.model.score.user;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "following",
    "followingCount"
})
@Generated("jsonschema2pojo")
public class ComputedFollowing {

    @JsonProperty("following")
    private Boolean following;
    @JsonProperty("followingCount")
    private Integer followingCount;

}
