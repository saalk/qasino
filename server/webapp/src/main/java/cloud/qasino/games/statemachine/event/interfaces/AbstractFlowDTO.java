package cloud.qasino.games.statemachine.event.interfaces;

import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.orchestration.interfaces.EventHandlingResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractFlowDTO { //implements FlowEventCallback {

    private String gameId;
    private String visitorId;
    private GameState startState;
    private Event currentGameEvent;
    private Event currentTurnEvent;
    private EventHandlingResponse eventHandlingResponse;

    public AbstractFlowDTO() {
    }
    public void updateState(final GameState newState) {
        throw new IllegalStateException("Unable to update state");
    }
    public GameState getCurrentState() {
        throw new IllegalStateException("Unable to retrieve state");
    }
}
