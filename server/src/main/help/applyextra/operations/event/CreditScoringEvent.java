package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class CreditScoringEvent extends AbstractEvent {

    @Override
    protected EventOutput execution(final Object... eventOutput) {
        //TODO:
        return null;
    }

    public Object perform(final Object o) {
        //TODO:
        return null;
    }

    public interface CreditScoringEventDTO {
        //TODO:
    }
}
