package cloud.qasino.quiz.event.interfaces;

import cloud.qasino.quiz.action.interfaces.Action;
import cloud.qasino.quiz.action.interfaces.ActionOutput;
import cloud.qasino.quiz.event.EventOutput;
import cloud.qasino.quiz.event.interfaces.AbstractFlowDTO;

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
