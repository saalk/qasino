package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.style.*;
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
public class PageGameSetup {

    // buttons
    // 1 update game details
    // - input is
    //      Type highlow
    //      ante int
    //      (o) League add or delete
    //      (0) Style update
    // 2 play a game when prepared

    // Main - 1,2
    @JsonProperty("GameSetup")
    private Game selectedGame;

    // TODO selections per type are default for now
    private AnteToWin anteToWin;
    private BettingStrategy bettingStrategy;
    private Deck deck;
    private InsuranceCost insuranceCost;
    private RoundsToWin roundsToWin;
    private TurnsToWin turnsToWin;

    @JsonProperty("SelectLeague")
    private List<League> leaguesToSelect;



}

