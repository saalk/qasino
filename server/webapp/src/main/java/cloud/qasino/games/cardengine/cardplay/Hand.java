package cloud.qasino.games.cardengine.cardplay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hand {

    // FROM GAME and specific PLAYER
    private int roundAndSeatNumber;
    private String cardsInRoundAndSeat;
    private String cardsDeltaInRoundAndSeat;
}

