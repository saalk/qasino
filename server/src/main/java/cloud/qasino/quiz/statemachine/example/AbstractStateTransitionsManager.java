package cloud.qasino.quiz.statemachine.example;

import cloud.qasino.quiz.statemachine.example.ProcessData;
import cloud.qasino.quiz.statemachine.example.ProcessException;
import cloud.qasino.quiz.statemachine.example.StateTransitionsManager;

//enforces initialization of the state where needed
public abstract class AbstractStateTransitionsManager implements StateTransitionsManager {
    protected abstract cloud.qasino.quiz.statemachine.example.ProcessData initializeState(cloud.qasino.quiz.statemachine.example.ProcessData data) throws cloud.qasino.quiz.statemachine.example.ProcessException;
    protected abstract cloud.qasino.quiz.statemachine.example.ProcessData processStateTransition(cloud.qasino.quiz.statemachine.example.ProcessData data) throws cloud.qasino.quiz.statemachine.example.ProcessException;
    @Override
    public cloud.qasino.quiz.statemachine.example.ProcessData processEvent(ProcessData data) throws ProcessException {
        initializeState(data);
        return processStateTransition(data);
    }
}
