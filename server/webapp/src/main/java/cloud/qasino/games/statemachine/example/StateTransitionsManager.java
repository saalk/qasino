package cloud.qasino.games.statemachine.example;

//events handler
public interface StateTransitionsManager {
    public ProcessData processEvent(ProcessData data) throws ProcessException;
}
