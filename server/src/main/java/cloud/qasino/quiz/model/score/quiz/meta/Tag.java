
package cloud.qasino.quiz.model.score.quiz.meta;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name"
})
@Generated("jsonschema2pojo")
public class Tag {

    @JsonProperty("name")
    private String name;

}
