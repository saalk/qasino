package cloud.qasino.card.dto;

import cloud.qasino.card.entity.Card;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.Turn;
import cloud.qasino.card.entity.enums.game.Style;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.move.Move;
import cloud.qasino.card.entity.enums.player.Avatar;
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
    private List<Card> stockNotInHand;
    @JsonProperty("Stats")
    private String totalVsStockCards;
    @JsonProperty("cardLeft")
    private int cardsLeft;
    @JsonProperty("Seats")
    private List<Seat> seats;

}
