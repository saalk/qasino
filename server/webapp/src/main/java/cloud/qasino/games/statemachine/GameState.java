package cloud.qasino.games.statemachine;

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

    // new
    NEW("new", "New games may not have PlayingCards, Leagues, Players","NEW"),
    PENDING_INVITATIONS("invite", "Game has Player with pending invitation","NEW"),
    PREPARED("accept", "Game is valid for playing","NEW"),

    // started
    PLAYING("play", "Game is being played and has Events","STARTED"),

    // ended
    FINISHED("finish", "Game has a Result and if possible a Winner","FINISHED"),
    QUIT("quit", "Game is stopped by a Player and has no Result or Winner","FINISHED"),
    CANCELLED("cancelled", "Game is cancelled by the system","FINISHED"),
    OLD("old", "Game is abandoned without Results or Winner","FINISHED"),

    // error,
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

    public static Set<GameState> cardGamesNew = EnumSet.of(NEW, PENDING_INVITATIONS, PREPARED);
    public static String[] cardGamesNewValues = new String[]{NEW.name(),
            PENDING_INVITATIONS.name(), PREPARED.name()};

    public static Set<GameState> cardGamesStarted = EnumSet.of(PLAYING);
    public static String[] cardGamesStartedValues = new String[]{PLAYING.name()};

    public static Set<GameState> cardGamesFinished = EnumSet.of(FINISHED, QUIT, CANCELLED, OLD);
    public static String[] cardGamesFinishedValues = new String[]{FINISHED.name(), QUIT.name(),
            CANCELLED.name(), OLD.name()};

    public static Set<GameState> cardGamesError = EnumSet.of(TIMEOUT, ERROR);
    public static String[] cardGamesErrorValues = new String[]{TIMEOUT.name(), ERROR.name(),
            CANCELLED.name()};


}