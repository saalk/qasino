package cloud.qasino.card.components.retry;

/**
 * Created by CL94WQ on 13-07-16.
 */
public interface RetrySchedule {
    boolean mustRetryNow(RetryRecord data);
}
