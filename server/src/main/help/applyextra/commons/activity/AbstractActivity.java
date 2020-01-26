package applyextra.commons.activity;

import applyextra.commons.event.AbstractEvent;

/**
 * Creation date: 26-8-15 Creator: Arnout
 * Design Patterns: Chain of responsibility (fireChainedEvent)
 *
 * @deprecated use {@link applyextra.commons.event.AbstractEvent} instead
 */
@Deprecated
public abstract class AbstractActivity<T> extends AbstractEvent {

    protected abstract ActivityOutput<T> execution(Object... activityInput);

    public ActivityOutput<T> fireActivity(Object... activityInput) {
        return execution(activityInput);
    }
}
