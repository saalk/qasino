package cloud.qasino.card.domain.qasino.enumrefs;

import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.statemachine.QasinoStateMachine;
import lombok.Getter;

import javax.swing.plaf.nimbus.State;
import java.util.Map;

@Getter
public class GameEnums {

    public Map<String, Type> type = Type.typeMapNoError;
    StyleEnums style = new StyleEnums();
    Map<String, QasinoStateMachine.GameState> state = QasinoStateMachine.lookup;

}
