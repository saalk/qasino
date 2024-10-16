package cloud.qasino.games.database.entity.enums.game.gamestate;

import cloud.qasino.games.database.entity.enums.game.GameState;
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
public enum GameStateGroup {
    SETUP("setup"),
    PREPARED("prepared"),
    PLAYING("playing"),
    FINISHED("finished"),
    ERROR("error");

    private String label;

    public static Map<String, GameStateGroup> lookup
            = new HashMap<>();

    public static final Map<String, GameStateGroup> gameStateGroupMapNoError
            = new HashMap<>();
    static {
        for(GameStateGroup gameStateGroup : EnumSet.allOf(GameStateGroup.class))
            if (!gameStateGroup.getLabel().equalsIgnoreCase("error"))
                gameStateGroupMapNoError.put(gameStateGroup.getLabel(), gameStateGroup);
    }

    public static GameStateGroup fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static GameStateGroup fromLabelWithDefault(String label) {
        GameStateGroup gameStateGroup = fromLabel(label);
        if (gameStateGroup == null) return GameStateGroup.ERROR;
        return gameStateGroup;
    }

    public  static List<GameState> listGameStatesForGameStateGroup(GameStateGroup gameStateGroup) {
        return GameState.fromGroupWithDefault(gameStateGroup);
    }

    public  static List<GameState> listGameStatesForGameStateGroups(List<GameStateGroup> gameStateGroups) {
        List<GameState> gameStates = new ArrayList<>();
        for (GameStateGroup gameStateGroup : gameStateGroups) {
            gameStates.addAll(listGameStatesForGameStateGroup(gameStateGroup));
        }
        return gameStates;
    }

    public  static String[] listGameStatesStringsForGameStateGroup(GameStateGroup gameStateGroup) {
        List<GameState> gameStateList = GameState.fromGroupWithDefault(gameStateGroup);
        List<String> list = new ArrayList<>();
        for (GameState gameState : gameStateList) {
            list.add(gameState.getLabel().toUpperCase());
        }
        return list.toArray(String[]::new);
    }
}

