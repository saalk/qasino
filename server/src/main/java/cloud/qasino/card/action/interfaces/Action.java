package cloud.qasino.card.action.interfaces;

public interface Action<INPUT, OUTPUT> {
    OUTPUT perform(INPUT input);
}
