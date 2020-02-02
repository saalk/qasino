package cloud.qasino.card.event.interfaces;

import cloud.qasino.card.action.interfaces.Action;
import cloud.qasino.card.action.interfaces.ActionOutput;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.event.interfaces.AbstractFlowDTO;

/**
 * Use {@link Action} for future implementations.
 *
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
