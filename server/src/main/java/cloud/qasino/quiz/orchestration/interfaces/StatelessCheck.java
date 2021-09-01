package cloud.qasino.quiz.orchestration.interfaces;

import cloud.qasino.quiz.statemachine.GameTrigger;
import cloud.qasino.quiz.event.EventOutput;
import cloud.qasino.quiz.event.interfaces.AbstractEvent;
import cloud.qasino.quiz.event.interfaces.AbstractFlowDTO;

public abstract class StatelessCheck<T extends AbstractFlowDTO> extends AbstractEvent {
    @SuppressWarnings("unchecked")
    @Override
    protected EventOutput execution(final Object... eventOutput) {
        final boolean success = check((T) eventOutput[0]);
        if (success) {
            return new EventOutput(EventOutput.Result.SUCCESS, GameTrigger.OK);
        } else {
            return new EventOutput(EventOutput.Result.FAILURE, GameTrigger.NOT_OK);
        }
    }

    protected abstract boolean check(final T flowDto);
}
