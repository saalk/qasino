package cloud.qasino.quiz.model.score.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "userId"
})
@Generated("jsonschema2pojo")
public class Favourite {

    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("userId")
    private Integer quizId;

}
