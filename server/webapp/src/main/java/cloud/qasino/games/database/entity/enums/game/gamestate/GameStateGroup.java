package cloud.qasino.games.database.entity.enums.game.gamestate;

import cloud.qasino.games.database.entity.enums.game.GameState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum GameStateGroup {
    SETUP("setup"), STARTED("started"), FINISHED("finished"), ERROR("error");

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

    public List<GameState> listGameStatesForGameStateGroup(GameStateGroup gameStateGroup) {
        //  todo loop gamestate group
        switch (gameStateGroup) {
            case SETUP:
                return (List<GameState>) GameState.setupGameStates;
            case STARTED:
                return (List<GameState>) GameState.highlowGameStates;
            case FINISHED:
                return (List<GameState>) GameState.finishedGameStates;
            default:
                return (List<GameState>) GameState.cardGamesError;
        }
    }
}

