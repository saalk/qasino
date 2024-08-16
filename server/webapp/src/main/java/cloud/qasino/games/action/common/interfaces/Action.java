package cloud.qasino.games.action.common.interfaces;

public interface Action<INPUT, OUTPUT> {
    OUTPUT perform(INPUT input);
}
