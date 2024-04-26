package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.move.Move;
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
public class SectionTable {

    @JsonProperty("CurrentTurn")
    private Turn currentTurn;
    @JsonProperty("PossibleMoves")
    private List<Move> possibleMoves;
    @JsonProperty("CardsInStock")
    private List<Card> cardsInStockNotInHand;
    @JsonProperty("StringCardsInStock")
    private String stringCardsInStockNotInHand;
    @JsonProperty("CountCardsInStockNotInHand")
    private int countCardsInStockNotInHand;

    @JsonProperty("Seats")
    private List<SectionSeat> seats;
}
