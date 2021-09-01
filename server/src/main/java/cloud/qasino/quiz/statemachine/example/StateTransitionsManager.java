package cloud.qasino.quiz.statemachine.example;

import cloud.qasino.quiz.statemachine.example.ProcessData;
import cloud.qasino.quiz.statemachine.example.ProcessException;

//events handler
public interface StateTransitionsManager {
    public ProcessData processEvent(ProcessData data) throws ProcessException;
}
