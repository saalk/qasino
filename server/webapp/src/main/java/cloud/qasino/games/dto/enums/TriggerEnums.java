package cloud.qasino.games.dto.enums;

import cloud.qasino.games.statemachine.event.GameEvent;
import cloud.qasino.games.statemachine.event.TurnEvent;
import lombok.Getter;

import java.util.Map;

@Getter
public class TriggerEnums {
    Map<String, GameEvent> gameEvent = GameEvent.gameEventMapNoError;
    Map<String, TurnEvent> turnEvent = TurnEvent.turnEventMapNoError;
}
