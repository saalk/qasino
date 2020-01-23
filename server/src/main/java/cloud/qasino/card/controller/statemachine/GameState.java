package cloud.qasino.card.controller.statemachine;

import cloud.qasino.card.entity.enums.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * <H1>CardGame</H1> A selection of card gameDtos that can be selected to play. <p> More gameDtos will be
 * added in future.
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum GameState implements LabeledEnum {

    // new
    NEW("new","New games may not have PlayingCards, Leagues, Players"),
    PENDING_INVITATIONS("invite", "Game has Player with pending invitation"),
    PREPARED ("accept","Game is valid for playing"),

    // started
    PLAYING ("play","Game is being played and has Events"),

    // ended
    FINISHED ("finish","Game has a Result and if possible a Winner"),
    QUIT ("quit","Game is stopped by a Player and has no Result or Winner"),

    // error
    ERROR ("error","Game has an unforseen 500 and needs a fix"),
    OLD("old","Game is abandoned without Results or Winner");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static Map<String, GameState> lookup
            = new HashMap<>();
    static {
        for(GameState gameState : EnumSet.allOf(GameState.class))
            lookup.put(gameState.getLabel(), gameState);
    }
    public static final Map<String, GameState> gameStates
            = new HashMap<>();
    static {
        for(GameState gameState : EnumSet.allOf(GameState.class))
            gameStates.put(gameState.getLabel(), gameState);
    }

    private String label;
    private String description;

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

    public static Set<GameState> cardGamesEnded = EnumSet.of(FINISHED, QUIT);
    public static String[] cardGamesEndedValues = new String[]{FINISHED.name(), QUIT.name()};

    public static Set<GameState> cardGamesError = EnumSet.of(OLD, ERROR);
    public static String[] cardGamesErrorValues = new String[]{OLD.name(), ERROR.name()};


}
