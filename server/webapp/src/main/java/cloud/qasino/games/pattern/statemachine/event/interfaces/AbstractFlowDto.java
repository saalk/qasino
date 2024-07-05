package cloud.qasino.games.pattern.statemachine.event.interfaces;

import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.orchestration.interfaces.EventHandlingResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractFlowDto { //implements FlowEventCallback {

    private String gameId;
    private String visitorId;
    private GameState startState;
    private Event gameEvent;
    private Event currentPlayEvent;
    private EventHandlingResponse eventHandlingResponse;

    public AbstractFlowDto() {
    }
    public void updateState(final GameState newState) {
        throw new IllegalStateException("Unable to update state");
    }
    public GameState getCurrentState() {
        throw new IllegalStateException("Unable to retrieve state");
    }
}
