package cloud.qasino.games.database.entity.enums.game;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum GameState implements LabeledEnum {

    // SETUP
    INITIALIZED("initialized", "New games may or may not have ante, cards, leagues or players","SETUP"),
    PENDING_INVITATIONS("pending_invitations", "Game has Player(s) with pending invitation","SETUP"),
    PREPARED("prepared", "Game is valid for playing","SETUP"),

    // HIGHLOW
    STARTED("started", "Game is being played and has Events","STARTED"),
    CASHED("cashed", "Game is being played and has Events","STARTED"),

    // ENDED
    FINISHED("finished", "Game has a Result and if possible a Winner","FINISHED"),
    QUIT("quit", "Game is stopped by a Player and has no Result or Winner","FINISHED"),
    CANCELLED("cancelled", "Game is cancelled by the system","FINISHED"),
    OLD("old", "Game is abandoned without Results or Winner","FINISHED"),

    // ERROR,
    ERROR("error", "Game has an unforseen 500 and needs a fix","ERROR"),
    TIMEOUT("timeout", "Game has an unforseen timeout and needs a fix","ERROR");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static Map<String, GameState> lookup
            = new HashMap<>();

    static {
        for (GameState gameState : EnumSet.allOf(GameState.class))
            lookup.put(gameState.getLabel(), gameState);
    }

    public static final Map<String, GameState> gameStates
            = new HashMap<>();

    static {
        for (GameState gameState : EnumSet.allOf(GameState.class))
            gameStates.put(gameState.getLabel(), gameState);
    }

    private String label;
    private String description;
    private String group;

    public static GameState fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }


    public static GameState fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static GameState fromLabelWithDefault(String label) {
        GameState gameState = fromLabel(label);
        if (gameState == null) return GameState.ERROR;
        return gameState;
    }

    public static GameState fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

    public static final Set<GameState> setupGameStates = EnumSet.of(INITIALIZED, PENDING_INVITATIONS, PREPARED);
    public static final String[] setupGameStatesValues = new String[]{INITIALIZED.name(),
            PENDING_INVITATIONS.name(), PREPARED.name()};

    public static final Set<GameState> highlowGameStates
            = EnumSet.of(STARTED, CASHED);
    public static final String[] highlowGameStatesValues = new String[]{STARTED.name(), CASHED.name()};

    public static final Set<GameState> finishedGameStates = EnumSet.of(FINISHED, QUIT, CANCELLED, OLD);
    public static final String[] finishedGameStatesValues = new String[]{FINISHED.name(), QUIT.name(),
            CANCELLED.name(), OLD.name()};

    public static final Set<GameState> cardGamesError = EnumSet.of(TIMEOUT, ERROR);
    public static final String[] errorGameStatesValues = new String[]{TIMEOUT.name(), ERROR.name(),
            CANCELLED.name()};


}
