package cloud.qasino.games.response.view;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
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
public class PageGamePlay {

    // buttons
    // 1 new Turn
    // - input is
    //      higher/lower/pass for human
    //      next for bot
    // game automatically ends after 1 round

    // Main - 1
    @JsonProperty("GamePlay")
    public Game selectedGame;
    public GameStateGroup gameStateGroup;
    // Stats
    @JsonProperty("Table")
    public SectionTable table;

    @JsonProperty("GameResults")
    public List<Result> gameResults;


}
