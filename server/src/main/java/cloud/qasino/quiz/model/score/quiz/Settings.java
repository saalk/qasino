
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
    "final",
    "maxSecondsPerQuestion",
    "numberOfHints",
    "allowExit",
    "allowGoBack",
    "randomizeQuestions",
    "randomizeAnswers",
    "minimumPercentToPass"
})
@Generated("jsonschema2pojo")
public class Settings {

    @JsonProperty("final")
    private String _final;
    @JsonProperty("maxSecondsPerQuestion")
    private Integer maxSecondsPerQuestion;
    @JsonProperty("numberOfHints")
    private Integer numberOfHints;
    @JsonProperty("allowExit")
    private String allowExit;
    @JsonProperty("allowGoBack")
    private String allowGoBack;
    @JsonProperty("randomizeQuestions")
    private String randomizeQuestions;
    @JsonProperty("randomizeAnswers")
    private String randomizeAnswers;
    @JsonProperty("minimumPercentToPass")
    private Integer minimumPercentToPass;

}
