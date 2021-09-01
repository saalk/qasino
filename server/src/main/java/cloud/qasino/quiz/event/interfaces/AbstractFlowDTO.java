package cloud.qasino.quiz.event.interfaces;

import cloud.qasino.quiz.event.interfaces.Event;
import cloud.qasino.quiz.statemachine.GameState;
import cloud.qasino.quiz.orchestration.interfaces.EventHandlingResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFlowDTO { //implements FlowEventCallback {

    @Getter
    @Setter
    private String gameId;

    @Setter
    @Getter
    protected String userId;
/*

    @Getter
    @Setter
    protected String employeeId;
*/

    private GameState startState;
    private cloud.qasino.quiz.event.interfaces.Event currentEvent;
    private EventHandlingResponse eventHandlingResponse;

    public GameState getStartState() {
        return startState;
    }

    public void setStartState(final GameState startState) {
        this.startState = startState;
    }

    public cloud.qasino.quiz.event.interfaces.Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(final Event currentEvent) {
        this.currentEvent = currentEvent;
    }


    public AbstractFlowDTO() {
    }

    public void updateState(final GameState newState) {
        throw new IllegalStateException("Unable to update state");
    }

    public GameState getCurrentState() {
        throw new IllegalStateException("Unable to retrieve state");
    }

    public void setEventHandlingResponse(final EventHandlingResponse eventHandlingResponse) {
        this.eventHandlingResponse = eventHandlingResponse;
    }

    public EventHandlingResponse getEventHandlingResponse() {
        return eventHandlingResponse;
    }
}
