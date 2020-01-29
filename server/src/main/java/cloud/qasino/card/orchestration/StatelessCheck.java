package cloud.qasino.card.orchestration;

import cloud.qasino.card.controller.statemachine.GameTrigger;
import cloud.qasino.card.dto.event.AbstractEvent;
import cloud.qasino.card.dto.event.AbstractFlowDTO;
import cloud.qasino.card.dto.event.EventOutput;

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
