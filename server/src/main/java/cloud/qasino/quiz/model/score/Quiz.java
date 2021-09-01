
package cloud.qasino.quiz.model.score;

import java.util.List;
import javax.annotation.Generated;

import cloud.qasino.quiz.model.score.quiz.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "quizId",
    "meta",
    "computed",
    "author",
    "settings",
    "questions"
})
@Generated("jsonschema2pojo")
public class Quiz {

    @JsonProperty("quizId")
    private Integer quizId;
    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("computed")
    private ComputedFavourite computed;
    @JsonProperty("author")
    private Author author;
    @JsonProperty("settings")
    private Settings settings;
    @JsonProperty("questions")
    private List<Question> questions = null;

}
