package cloud.qasino.games.pattern.statemachine.event.interfaces;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.action.interfaces.ActionOutput;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;

/**
 * Use {@link Action} for future implementations.
 *
 * @param <INPUT>
 * @param <OUTPUT>
 */
@Deprecated
public abstract class AbstractEvent<INPUT extends AbstractFlowDto, OUTPUT> implements Action<INPUT, ActionOutput<OUTPUT>> {

    protected abstract EventOutput execution(Object... eventOutput);

    public EventOutput fireEvent(Object... eventOutput) {
        return execution(eventOutput);
    }

    public ActionOutput<OUTPUT> perform(INPUT input) {
        return (ActionOutput<OUTPUT>) execution(input);
    }
}
