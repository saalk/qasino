package cloud.qasino.card.components.retry;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SimpleRetrySchedule implements RetrySchedule {

    private long intervalInMs;
    private int maxNrofRetries;

    public SimpleRetrySchedule(int maxNrofRetries, int interval, TimeUnit timeUnit) {
        this.maxNrofRetries = maxNrofRetries;
        intervalInMs = timeUnit.toMillis(interval);
    }

    @Override
    public boolean mustRetryNow(RetryRecord data) {
        log.debug(data.getNrOfRetriesAttempted() + " < " + maxNrofRetries + " ? " + (data.getNrOfRetriesAttempted() <
                maxNrofRetries));
        if (data.getNrOfRetriesAttempted() < maxNrofRetries) {
            Date nextAttemptTimestamp = new Date(data.getLastTryTimestamp().getTime() + intervalInMs);
            Date now = new Date();
            log.debug(now + " after " + nextAttemptTimestamp + " ? " + now.after(nextAttemptTimestamp));
            return now.after(nextAttemptTimestamp);
        }
        return false;
    }
}
