package cloud.qasino.quiz.statemachine.example;

import cloud.qasino.quiz.statemachine.example.ProcessState;
import cloud.qasino.quiz.statemachine.example.Processor;

//Enum implements this interface
public interface ProcessEvent {
    public abstract Class<? extends Processor> nextStepProcessor(ProcessEvent event);
    public abstract ProcessState nextState(ProcessEvent event);
}
