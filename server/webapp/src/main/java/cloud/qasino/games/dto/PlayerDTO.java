package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.time.Month;
import java.util.List;

@Data
/**
 * The purpose of using this DTO is to separate the internal representation of user data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class PlayerDTO {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String EMAIL_MESSAGE = "{email.message}";

    // for create and update
    private long gameId;
    @NotBlank(message = PlayerDTO.NOT_BLANK_MESSAGE)
    private long initiator;
    GameState state;
    GameStateGroup gameStateGroup;
    Type type;
    private String style;
    private int ante;
    League league;
    List<Player> players;
    boolean isActivatePlayerInitiator;
    List<Result> results;

    // derived
    private AnteToWin anteToWin;
    private BettingStrategy bettingStrategy;
    private DeckConfiguration deckConfiguration;
    private OneTimeInsurance oneTimeInsurance;
    private RoundsToWin roundsToWin;
    private TurnsToWin turnsToWin;

    // for view
    private int year;
    private Month month;
    private String week;
    private int weekday;

}

