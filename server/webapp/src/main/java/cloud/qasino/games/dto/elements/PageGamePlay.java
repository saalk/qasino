package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Turn;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageGamePlay {

    // Main and stats
    @JsonProperty("ActiveGame")
    private Game selectedGame;
    @JsonProperty("gameName")
    private String gameName; // game type and gameId concat
    private int currentTurn;
    private int currentRound;
    // Pending actions
    private Turn activeTurn;
    @JsonProperty("Table")
    private SectionTable table;

}
