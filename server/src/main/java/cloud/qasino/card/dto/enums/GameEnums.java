package cloud.qasino.card.dto.enums;

import cloud.qasino.card.statemachine.GameState;
import cloud.qasino.card.entity.enums.game.Type;
import lombok.Getter;

import java.util.Map;

@Getter
public class GameEnums {

    public Map<String, Type> type = Type.typeMapNoError;
    StyleEnums style = new StyleEnums();
    Map<String, GameState> state = GameState.gameStates;

}
