package cloud.qasino.quiz.action.interfaces;

public interface Action<INPUT, OUTPUT> {
    OUTPUT perform(INPUT input);
}
