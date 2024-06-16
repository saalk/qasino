package cloud.qasino.games.response.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionHand {
    private int roundNumber;
    private String cardsInRound;
}

