package cloud.qasino.card.domain.qasino.enumrefs;

import cloud.qasino.card.entity.enums.event.Action;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.statemachine.QasinoStateMachine;
import lombok.Getter;

import java.util.Map;

@Getter
public class EventEnums {

    Map<String, Action> action = Action.actionMapNoError;

}
