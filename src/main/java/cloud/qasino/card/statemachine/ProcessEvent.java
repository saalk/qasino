package cloud.qasino.card.statemachine;

//Enum implements this interface
public interface ProcessEvent {
    public abstract Class<? extends Processor> nextStepProcessor(ProcessEvent event);
    public abstract ProcessState nextState(ProcessEvent event);
}
