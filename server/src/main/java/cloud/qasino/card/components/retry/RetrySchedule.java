package cloud.qasino.card.components.retry;

public interface RetrySchedule {
    boolean mustRetryNow(RetryRecord data);
}
