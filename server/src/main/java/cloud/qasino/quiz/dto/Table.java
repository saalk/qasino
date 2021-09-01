package cloud.qasino.quiz.dto;

import cloud.qasino.quiz.dto.Seat;
import cloud.qasino.quiz.entity.Quiz;
import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.entity.Player;
import cloud.qasino.quiz.entity.Turn;
import cloud.qasino.quiz.entity.enums.game.Style;
import cloud.qasino.quiz.entity.enums.game.Type;
import cloud.qasino.quiz.entity.enums.move.Move;
import cloud.qasino.quiz.entity.enums.player.Avatar;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Table {

    @JsonProperty("SelectedGame")
    private Game selectedGame;
    @JsonProperty("LastTurn")
    private Turn lastTurn;
    @JsonProperty("PossibleMoves")
    private List<Move> possibleMoves;
    @JsonProperty("Stock")
    private List<Quiz> stockNotInHand;
    @JsonProperty("Stats")
    private String totalVsStockQuizs;
    @JsonProperty("quizLeft")
    private int quizsLeft;
    @JsonProperty("Seats")
    private List<Seat> seats;

}
