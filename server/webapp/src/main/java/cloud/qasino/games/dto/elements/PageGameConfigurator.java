package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Visitor;
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
public class PageGameConfigurator {

    private boolean initiatedByYou;

    @JsonProperty("SelectedGame")
    private Game selectedGame;
    private boolean hasBalance;
    private int totalVisitors;
    private int totalBots;

    @JsonProperty("Friends")
    private List<Visitor> visitorsToInvite; // todo friends

    @JsonProperty("Leagues")
    private List<League> leaguesToSelect;

    // selections
    private AnteToWin anteToWin;
    private BettingStrategy bettingStrategy;
    private Deck deck;
    private InsuranceCost insuranceCost;
    private RoundsToWin roundsToWin;
    private TurnsToWin turnsToWin;

}

