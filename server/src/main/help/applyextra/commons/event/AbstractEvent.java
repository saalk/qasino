package applyextra.commons.event;

import applyextra.commons.orchestration.Action;
import applyextra.commons.orchestration.ActionOutput;

/**
 * Use {@link Action} for future implementations.
 * @param <INPUT>
 * @param <OUTPUT>
 */
@Deprecated
public abstract class AbstractEvent<INPUT extends AbstractFlowDTO, OUTPUT> implements Action<INPUT, ActionOutput<OUTPUT>> {

    protected abstract EventOutput execution(Object... eventOutput);

    public EventOutput fireEvent(Object... eventOutput) {
        return execution(eventOutput);
    }

    public ActionOutput<OUTPUT> perform(INPUT input) {
        return (ActionOutput<OUTPUT>) execution(input);
    }
}
