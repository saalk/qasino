package cloud.qasino.games.dto.navigation;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.User;
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
public class NavigationGame {

    private boolean hasBalance;      // icon is cards -> select/start game to enable nav-game
    // creating / selecting  only possible when <hasBalance = true>

    @JsonProperty("Game")
    private Game game; // including a list of players + intiator

    @JsonProperty("Friends")
    private List<User> friends; // todo friends

    @JsonProperty("Leagues")
    private List<League> leagues;

    private int totalUsers;
    private int totalBots;
    // selections
    private AnteToWin anteToWin;
    private BettingStrategy bettingStrategy;
    private Deck deck;
    private InsuranceCost insuranceCost;
    private RoundsToWin roundsToWin;
    private TurnsToWin turnsToWin;
}

