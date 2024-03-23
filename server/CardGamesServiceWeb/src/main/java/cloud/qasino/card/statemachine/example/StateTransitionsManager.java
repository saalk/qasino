package cloud.qasino.card.statemachine.example;

import cloud.qasino.card.statemachine.example.ProcessData;
import cloud.qasino.card.statemachine.example.ProcessException;

//events handler
public interface StateTransitionsManager {
    public ProcessData processEvent(ProcessData data) throws ProcessException;
}
