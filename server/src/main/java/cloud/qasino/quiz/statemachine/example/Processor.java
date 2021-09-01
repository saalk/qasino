package cloud.qasino.quiz.statemachine.example;

import cloud.qasino.quiz.statemachine.example.ProcessData;
import cloud.qasino.quiz.statemachine.example.ProcessException;

//executes the business rules
//needed for this state transition step
public interface Processor {
    public ProcessData process(ProcessData data) throws ProcessException;
}
