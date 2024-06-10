package cloud.qasino.games.dto.view;

import cloud.qasino.games.database.entity.Turn;
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
//    @JsonIgnore
//    @JsonProperty("CardsInStock")
//    private List<Card> cardsInStockNotInHand;
    @JsonProperty("Stock")
    private String stringCardsInStockNotInHand;
    @JsonProperty("countStockAndTotal")
    private String countStockAndTotal;

    @JsonProperty("Seats")
    private List<SectionSeat> seats;
}
