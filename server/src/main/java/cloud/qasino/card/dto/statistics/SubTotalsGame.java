package cloud.qasino.card.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubTotalsGame {

    public int totalNewGames;
    public int totalStartedGames;
    public int totalsFinishedGames;
}
