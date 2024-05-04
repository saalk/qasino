package cloud.qasino.games.database.entity.enums.game;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.move.Move;
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

    // SETUP
    INITIALIZED("initialized", "Setup or change ante, League and Players", GameStateGroup.SETUP),
    PENDING_INVITATIONS("pending_invitations", "Awaiting invitations", GameStateGroup.SETUP),
    PREPARED("prepared", "Validated, start shuffling.", GameStateGroup.PREPARED),

    // HIGHLOW
    STARTED("started", "Proceed with first Move", GameStateGroup.PLAYING),
    NEXT_MOVE("next_move", "Proceed with next Move", GameStateGroup.PLAYING),
    NEXT_TURN("next_turn", "Proceed with next Player", GameStateGroup.PLAYING),

    // ENDED
    FINISHED("finished", "Show Results", GameStateGroup.FINISHED),
    QUIT("quit", "Game stopped by player", GameStateGroup.FINISHED),
    CANCELLED("cancelled", "Game abandoned", GameStateGroup.FINISHED),

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
            if (!gameState.getLabel().toLowerCase().equals("error"))
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
