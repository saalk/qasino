package applyextra.commons.event;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.orchestration.Event;
import applyextra.commons.orchestration.EventHandlingResponse;
import applyextra.commons.state.CreditCardsStateMachine.State;

public abstract class AbstractFlowDTO {

    @Getter
    @Setter
    private String requestId;

    @Setter
    @Getter
    protected String customerId;

    @Getter
    @Setter
    protected String employeeId;

    private State startState;
    private Event currentEvent;
    private EventHandlingResponse eventHandlingResponse;

    public State getStartState() {
        return startState;
    }

    public void setStartState(final State startState) {
        this.startState = startState;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(final Event currentEvent) {
        this.currentEvent = currentEvent;
    }


    public AbstractFlowDTO() {
    }

    public void updateState(final State newState) {
        throw new IllegalStateException("Unable to update state");
    }

    public State getCurrentState() {
        throw new IllegalStateException("Unable to retrieve state");
    }

    public void setEventHandlingResponse(final EventHandlingResponse eventHandlingResponse) {
        this.eventHandlingResponse = eventHandlingResponse;
    }

    public EventHandlingResponse getEventHandlingResponse() {
        return eventHandlingResponse;
    }
}
