package cloud.qasino.card.dto;

import cloud.qasino.card.database.entity.Card;
import cloud.qasino.card.database.entity.Game;
import cloud.qasino.card.database.entity.Turn;
import cloud.qasino.card.database.entity.enums.move.Move;
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
