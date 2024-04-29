package cloud.qasino.games.database.entity.enums.game.gamestate;

import cloud.qasino.games.database.entity.enums.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum GameStateGroup {
    SETUP("setup"), PREPARED("prepared"), STARTED("started"), FINISHED("finished"), ERROR("error");

    private String label;

    public static Map<String, GameStateGroup> lookup
            = new HashMap<>();


    public static GameStateGroup fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static GameStateGroup fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static GameStateGroup fromLabelWithDefault(String label) {
        GameStateGroup gameStateGroup = fromLabel(label);
        if (gameStateGroup == null) return GameStateGroup.ERROR;
        return gameStateGroup;
    }

    public static GameStateGroup fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

    public  static Set<GameState> listGameStatesForGameStateGroup(GameStateGroup gameStateGroup) {
        //  todo loop gamestate group
        switch (gameStateGroup) {
            case SETUP:
                return GameState.setupGameStates;
            case PREPARED:
                return GameState.preparedGameStates;
            case STARTED:
                return GameState.highlowGameStates;
            case FINISHED:
                return GameState.finishedGameStates;
            default:
                return GameState.cardGamesError;
        }
    }
}

