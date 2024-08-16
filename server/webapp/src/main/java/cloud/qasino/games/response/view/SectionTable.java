package cloud.qasino.games.response.view;

import cloud.qasino.games.database.entity.Playing;
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

    @JsonProperty("Playing")
    private Playing playing;
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
