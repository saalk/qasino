package applyextra.commons.orchestration;

import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.AbstractFlowDTO;
import applyextra.commons.event.EventOutput;
import applyextra.commons.state.CreditCardsStateMachine;

public abstract class StatelessCheck<T extends AbstractFlowDTO> extends AbstractEvent {
    @SuppressWarnings("unchecked")
    @Override
    protected EventOutput execution(final Object... eventOutput) {
        final boolean success = check((T) eventOutput[0]);
        if (success) {
            return new EventOutput(EventOutput.Result.SUCCESS, CreditCardsStateMachine.Trigger.OK);
        } else {
            return new EventOutput(EventOutput.Result.FAILURE, CreditCardsStateMachine.Trigger.NOT_OK);
        }
    }

    protected abstract boolean check(final T flowDto);
}
