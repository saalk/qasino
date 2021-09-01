package cloud.qasino.quiz.statemachine;

import cloud.qasino.quiz.statemachine.GameState;
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

    public List<cloud.qasino.quiz.statemachine.GameState> listGameStatesForGameStateGroup(GameStateGroup gameStateGroup) {
        //  todo loop gamestate group
        switch (gameStateGroup) {
            case NEW:
                return (List<cloud.qasino.quiz.statemachine.GameState>) cloud.qasino.quiz.statemachine.GameState.quizGamesNew;
            case STARTED:
                return (List<cloud.qasino.quiz.statemachine.GameState>) cloud.qasino.quiz.statemachine.GameState.quizGamesStarted;
            case FINISHED:
                return (List<cloud.qasino.quiz.statemachine.GameState>) cloud.qasino.quiz.statemachine.GameState.quizGamesFinished;
            default:
                return (List<cloud.qasino.quiz.statemachine.GameState>) GameState.quizGamesError;
        }
    }
}

