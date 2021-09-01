package cloud.qasino.quiz.dto.enums;

import cloud.qasino.quiz.dto.enums.StyleEnums;
import cloud.qasino.quiz.statemachine.GameState;
import cloud.qasino.quiz.entity.enums.game.Type;
import lombok.Getter;

import java.util.Map;

@Getter
public class GameEnums {

    public Map<String, Type> type = Type.typeMapNoError;
    StyleEnums style = new StyleEnums();
    Map<String, GameState> state = GameState.gameStates;

}
