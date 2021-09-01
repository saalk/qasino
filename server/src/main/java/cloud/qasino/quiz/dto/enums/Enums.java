package cloud.qasino.quiz.dto.enums;

import cloud.qasino.quiz.dto.enums.QuizEnums;
import cloud.qasino.quiz.dto.enums.GameEnums;
import cloud.qasino.quiz.dto.enums.PlayerEnums;
import cloud.qasino.quiz.dto.enums.TurnEnums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Enums {

    @JsonProperty("GameEnums")
    private GameEnums game = new GameEnums();
    @JsonProperty("PlayerEnums")
    private PlayerEnums player = new PlayerEnums();
    @JsonProperty("QuizEnums")
    private QuizEnums quiz = new QuizEnums();
    @JsonProperty("TurnEnums")
    private TurnEnums turn = new TurnEnums();

    public Enums() {

    }
}