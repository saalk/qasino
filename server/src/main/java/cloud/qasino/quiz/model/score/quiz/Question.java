
package cloud.qasino.quiz.model.score.quiz;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "questionId",
    "text",
    "category",
    "choices",
    "answer",
    "explanation"
})
@Generated("jsonschema2pojo")
public class Question {

    @JsonProperty("questionId")
    private Integer questionId;
    @JsonProperty("text")
    private String text;
    @JsonProperty("category")
    private String category;
    @JsonProperty("choices")
    private Object choices;
    @JsonProperty("answer")
    private String answer;
    @JsonProperty("explanation")
    private String explanation;

}
