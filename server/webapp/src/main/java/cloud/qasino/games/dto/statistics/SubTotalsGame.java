package cloud.qasino.games.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubTotalsGame {

    public int totalGamesSetup;
    public int totalGamesPrepared;
    public int totalGamesPlaying;
    public int totalsGamesFinished;
}
