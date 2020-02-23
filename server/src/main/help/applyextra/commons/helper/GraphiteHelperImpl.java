package applyextra.commons.helper;

import lombok.Getter;
import nl.ing.riaf.core.metrics.MetricsManager;
import nl.ing.riaf.core.time.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;

public final class GraphiteHelperImpl implements GraphiteHelper {

    @Autowired
    @Getter
    private MetricsManager metricsManager;

    @Autowired
    @Getter
    private TimeProvider timeProvider;

    @Override
    public final long startTiming() {
        return timeProvider.currentTimeMillis();
    }

    @Override
    public final void stopTiming(final String category, final String name, final long start) {
        metricsManager.timer(category, name, timeDifference(start));
    }

    @Override
    public final void requestCounter(final String category) {
        metricsManager.callsCounter(category, "", "count", 1);
    }

    @Override
    public final void responseCounter(final String category, final boolean success) {
        if (success) {
            metricsManager.successCounter(category, "calls", true);
        } else {
            metricsManager.failCounter(category, "calls", false);
        }
    }

    @Override
    public final void responseCounter(final String category, final boolean success, final int retries) {
        if (retries < 1) {
            responseCounter(category, success);
        } else {
            metricsManager.retryCounter(category, "calls", retries, success);
        }
    }

    @Override
    public final void customCounter(final String category, final String name, final int delta) {
        metricsManager.incCounter(category, name, "counter", delta);
    }

    @Override
    public final long timeDifference(final Long startingTime) {
        if (startingTime == null){
            return timeProvider.currentTimeMillis();
        } else {
            return timeProvider.currentTimeMillis()-startingTime;
        }
    }

}
