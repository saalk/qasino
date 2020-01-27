package cloud.qasino.card.orchestration;

public interface Action<INPUT, OUTPUT> {
    OUTPUT perform(INPUT input);
}
