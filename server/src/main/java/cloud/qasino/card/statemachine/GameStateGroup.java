package cloud.qasino.card.statemachine;

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
    NEW("new"), STARTED("started"), FINISHED("finished"), ERROR("error");

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
            case NEW:
                return (List<GameState>) GameState.cardGamesNew;
            case STARTED:
                return (List<GameState>) GameState.cardGamesStarted;
            case FINISHED:
                return (List<GameState>) GameState.cardGamesFinished;
            default:
                return (List<GameState>) GameState.cardGamesError;
        }
    }
}

