package cloud.qasino.quiz.model.score;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "questionId",
        "answer",
        "secondsToAnswer"
})
@Generated("jsonschema2pojo")
public class Answer {

    @JsonProperty("questionId")
    private Integer questionId;
    @JsonProperty("answer")
    private String answer;
    @JsonProperty("secondsToAnswer")
    private Integer secondsToAnswer;

}
