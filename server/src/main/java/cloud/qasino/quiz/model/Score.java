package cloud.qasino.quiz.model;

import cloud.qasino.quiz.model.score.Answer;
import cloud.qasino.quiz.model.score.ComputedScore;
import cloud.qasino.quiz.model.score.User;
import com.fasterxml.jackson.annotation.*;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "scoreId",
        "userId",
        "quizId",
        "created",
        "updated",
        "hintsTaken",
        "computed",
        "answers",
        "user"
})
@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "scores", indexes = {@Index(name = "scores_index", columnList = "score_id", unique =
        true)})
public class Score {

    @Id
    @GeneratedValue
    @Column(name = "score_id", nullable = false)
    @JsonProperty("scoreId")
    private Integer scoreId;


    // Foreign keys

    @JsonIgnore
    // PlGa: many Scores can be part of the same User
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey
            (name = "fk_user_id"), nullable = true)
    @JsonProperty("user_id")
    private Integer userId;
    @JsonIgnore
    // PlGa: many Scores can be part of the same Quiz
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", foreignKey = @ForeignKey
            (name = "fk_quiz_id"), nullable = true)
    @JsonProperty("quiz_id")
    private Integer quizId;


    // Normal fields

    @JsonProperty("created")
    private String created;
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("hintsTaken")
    private Integer hintsTaken;
    @JsonProperty("computed")
    private ComputedScore computed;
    @JsonProperty("answers")
    private List<Answer> answers = null;
    @JsonProperty("user")
    private User user;

}
