package cloud.qasino.quiz.model.score.user;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

import cloud.qasino.quiz.model.score.user.ComputedFollowing;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "userId"
})
@Generated("jsonschema2pojo")
public class Follows {

    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("userId")
    private Integer authorId;

}
