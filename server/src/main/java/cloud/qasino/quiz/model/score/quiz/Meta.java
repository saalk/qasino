package cloud.qasino.quiz.model.score.quiz;

import cloud.qasino.quiz.model.score.quiz.meta.Tag;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.List;

@Table(indexes = {
        @Index(name = "idx_meta_tags", columnList = "tags")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "title",
        "description",
        "subject",
        "audiance",
        "created",
        "updated",
        "tagList"
})
@Generated("jsonschema2pojo")
public class Meta {

    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("audiance")
    private String audiance;
    @JsonProperty("created")
    private String created;
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("tagList")
    private List<Tag> tags;

}
