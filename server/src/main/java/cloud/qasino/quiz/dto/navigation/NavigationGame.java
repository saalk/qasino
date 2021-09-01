package cloud.qasino.quiz.dto.navigation;

import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.entity.League;
import cloud.qasino.quiz.entity.User;
import cloud.qasino.quiz.entity.enums.game.style.*;
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

    private boolean hasBalance;      // icon is quizzes -> select/start game to enable nav-game
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

