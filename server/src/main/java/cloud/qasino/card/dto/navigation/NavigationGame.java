package cloud.qasino.card.dto.navigation;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.League;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.game.style.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NavigationGame {

    private boolean hasBalance;      // icon is cards -> select/start game to enable nav-game
    // creating / selecting  only possible when <hasBalance = true>

    @JsonProperty("Game")
    private Game game; // including a list of players + intiator

    @JsonProperty("Friends")
    private List<User> friends;

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

