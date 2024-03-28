package cloud.qasino.games.action.interfaces;

public interface Action<INPUT, OUTPUT> {
    OUTPUT perform(INPUT input);
}
