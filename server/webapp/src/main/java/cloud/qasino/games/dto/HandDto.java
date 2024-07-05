package cloud.qasino.games.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandDto {

    // FROM GAME and specific PLAYER
    private int roundNumber;
    private int seatNumber;
    private String cardsInRoundAndSeat;
    private String cardsDeltaInRoundAndSeat;
}

