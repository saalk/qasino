package cloud.qasino.games.dto.enums;

import cloud.qasino.games.statemachine.trigger.GameTrigger;
import cloud.qasino.games.statemachine.trigger.TurnTrigger;
import lombok.Getter;

import java.util.Map;

@Getter
public class TriggerEnums {
    Map<String, GameTrigger> gameTrigger = GameTrigger.gameTriggerMapNoError;
    Map<String, TurnTrigger> turnTrigger = TurnTrigger.turnTriggerMapNoError;
}
