package cloud.qasino.card.statemachine;

//events handler
public interface StateTransitionsManager {
    public ProcessData processEvent(ProcessData data) throws ProcessException;
}
