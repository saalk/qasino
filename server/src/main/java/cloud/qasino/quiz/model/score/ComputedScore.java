package cloud.qasino.quiz.model.score;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "answeredCount",
        "correctCount",
        "currentQuestion",
        "currentIndex",
        "currentPercentToPass",
        "passed"
})
@Generated("jsonschema2pojo")
public class ComputedScore {

    @JsonProperty("answeredCount")
    private Integer answeredCount;
    @JsonProperty("correctCount")
    private Integer correctCount;
    @JsonProperty("currentQuestion")
    private Integer currentQuestion;
    @JsonProperty("currentIndex")
    private Integer currentIndex;
    @JsonProperty("currentPercentToPass")
    private Integer currentPercentToPass;
    @JsonProperty("passed")
    private Boolean passed;

}
