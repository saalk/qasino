package cloud.qasino.games.orchestration.interfaces;

import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.interfaces.AbstractEvent;
import cloud.qasino.games.pattern.statemachine.event.interfaces.AbstractFlowDto;

public abstract class StatelessCheck<T extends AbstractFlowDto> extends AbstractEvent {
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
        return flowDto.getGameEvent().toString();
    }

    protected String getPlayEvent(final T flowDto) {
        return flowDto.getGameEvent().toString();
    }
}
