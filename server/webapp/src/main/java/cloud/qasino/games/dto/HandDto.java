package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class HandDto {

    // core
    List<Card> cards;
    List<String> rankSuits;

    // derived
    private int roundNumber;
    private int seatNumber;
    private String cardsInRoundAndSeat;
    private String cardsDeltaInRoundAndSeat;
}

