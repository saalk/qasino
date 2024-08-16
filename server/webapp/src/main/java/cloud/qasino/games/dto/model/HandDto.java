package cloud.qasino.games.dto.model;

import lombok.Data;

@Data
public class HandDto {

    // core
//    List<String> rankSuitsList;
    String rankSuits;

    // derived
    private int roundNumber;
    private int seatNumber;
    private String cardsInRoundAndSeat;
    private String cardsDeltaInRoundAndSeat;
}

