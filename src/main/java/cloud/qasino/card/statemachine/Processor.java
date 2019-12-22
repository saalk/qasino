package cloud.qasino.card.statemachine;

//executes the business rules
//needed for this state transition step
public interface Processor {
    public ProcessData process(ProcessData data) throws ProcessException;
}
