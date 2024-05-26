package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.League;
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
    @JsonProperty("GameInSetup")
    private Game selectedGame;

    @JsonProperty("BotPlayer")
    private Player botPlayer;

    @JsonProperty("HumanPlayer")
    private Player humanPlayer;

    // TODO selections per type are default for now
    @JsonProperty("AnteToWin")
    private AnteToWin anteToWin;
    @JsonProperty("BettingStrategy")
    private BettingStrategy bettingStrategy;
    @JsonProperty("DeckConfiguration")
    private DeckConfiguration deckConfiguration;
    @JsonProperty("OneTimeInsurance")
    private OneTimeInsurance oneTimeInsurance;
    @JsonProperty("RoundsToWin")
    private RoundsToWin roundsToWin;
    @JsonProperty("TurnsToWin")
    private TurnsToWin turnsToWin;

    @JsonProperty("SelectLeague")
    private List<League> leaguesToSelect;



}

