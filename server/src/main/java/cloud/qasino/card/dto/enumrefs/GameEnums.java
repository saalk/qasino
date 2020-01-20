package cloud.qasino.card.dto.enumrefs;

import cloud.qasino.card.domain.qasino.statemachine.GameState;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.statemachine.QasinoStateMachine;
import lombok.Getter;

import java.util.Map;

@Getter
public class GameEnums {

    public Map<String, Type> type = Type.typeMapNoError;
    StyleEnums style = new StyleEnums();
    Map<String, GameState> state = GameState.gameStates;

}
