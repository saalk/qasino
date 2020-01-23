package applyextra.commons.orchestration;

public interface Action<INPUT, OUTPUT> {
    OUTPUT perform(INPUT input);
}
