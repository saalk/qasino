package cloud.qasino.games.orchestration.interfaces;

import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.statemachine.event.interfaces.AbstractEvent;
import cloud.qasino.games.statemachine.event.interfaces.AbstractFlowDTO;

public abstract class StatelessCheck<T extends AbstractFlowDTO> extends AbstractEvent {
    @SuppressWarnings("unchecked")
    @Override
    protected EventOutput execution(final Object... eventOutput) {
        final boolean success = check((T) eventOutput[0]);
        if (success) {
            return new EventOutput(EventOutput.Result.SUCCESS, null, null);
        } else {
            return new EventOutput(EventOutput.Result.FAILURE, null, null);
        }
    }

    protected abstract boolean check(final T flowDto);
    protected String getGameEvent(final T flowDto) {
        return flowDto.getCurrentGameEvent().toString();
    };
    protected String getTurnEvent(final T flowDto) {
        return flowDto.getCurrentGameEvent().toString();
    };
}
