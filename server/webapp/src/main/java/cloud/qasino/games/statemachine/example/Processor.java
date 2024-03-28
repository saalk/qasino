package cloud.qasino.games.statemachine.example;

//executes the business rules
//needed for this state transition step
public interface Processor {
    public ProcessData process(ProcessData data) throws ProcessException;
}
