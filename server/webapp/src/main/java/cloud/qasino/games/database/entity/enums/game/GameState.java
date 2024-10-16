package cloud.qasino.games.database.entity.enums.game;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum GameState implements LabeledEnum {

    // SETUP - NEW
    INITIALIZED("initialized", "Game initialized, validate the game setup", GameStateGroup.SETUP),
    PENDING_INVITATIONS("pending_invitations", "Awaiting player invitations", GameStateGroup.SETUP),
    // SETUP - VALIDATE
    PREPARED("prepared", "Game validated, start shuffling", GameStateGroup.PREPARED),

    // PLAY
    STARTED("started", "Game shuffled, proceed to play", GameStateGroup.PLAYING),
    INITIATOR_MOVE("initiator_move", "Play a move", GameStateGroup.PLAYING),
    INVITEE_MOVE("invitee_move", "Waiting for invitee to do a move", GameStateGroup.PLAYING),
    BOT_MOVE("bot_move", "Push the bot player to do a next move", GameStateGroup.PLAYING),

    // ENDED
    FINISHED("finished", "Game finished, view results and setup a new one", GameStateGroup.FINISHED),
    STOPPED("stopped", "Game stopped, setup a new one", GameStateGroup.FINISHED),
    CANCELLED("cancelled", "Game abandoned, setup a new one", GameStateGroup.FINISHED),

    // ERROR
    ERROR("error", "Game in error", GameStateGroup.ERROR);

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, GameState> lookup
            = new HashMap<>();

    public static final Map<String, GameState> lookupNoError
            = new HashMap<>();

    static {
        for (GameState gameState : EnumSet.allOf(GameState.class))
            lookup.put(gameState.getLabel(), gameState);
    }

    static {
        for (GameState gameState : EnumSet.allOf(GameState.class))
            if (!gameState.getLabel().equalsIgnoreCase("error"))
                lookupNoError.put(gameState.getLabel(), gameState);
    }

    private String label;
    private String nextAction;
    private GameStateGroup group;

    public static GameState fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static GameState fromLabelWithDefault(String label) {
        GameState gameState = fromLabel(label);
        if (gameState == null) return GameState.ERROR;
        return gameState;
    }

    public static List<GameState> fromGroupWithDefault(GameStateGroup group) {
        List<GameState> gameStates = new ArrayList<>();
        for (GameState gameState : lookup.values()) {
            if (gameState.getGroup().equals(group)) {
                gameStates.add(gameState);
            }
        }
        if (gameStates.isEmpty()) {
            gameStates.add(GameState.ERROR);
        }
        return gameStates;
    }
}
