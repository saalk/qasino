package cloud.qasino.games.pattern.statemachine.event.interfaces;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.action.common.interfaces.Action;
import cloud.qasino.games.action.common.interfaces.ActionOutput;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;

/**
 * Use {@link Action} for future implementations.
 *
 * @param <INPUT>
 * @param <OUTPUT>
 */
@Deprecated
public abstract class AbstractEvent<INPUT extends AbstractFlowDto, OUTPUT> extends GenericLookupsAction<INPUT> {

    protected abstract EventOutput execution(Object... eventOutput);

    public EventOutput fireEvent(Object... eventOutput) {
        return execution(eventOutput);
    }

    public ActionOutput<OUTPUT> perform(INPUT input) {
        return (ActionOutput<OUTPUT>) execution(input);
    }
}
