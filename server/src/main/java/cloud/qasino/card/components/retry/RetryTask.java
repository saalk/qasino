package cloud.qasino.card.components.retry;

public interface RetryTask<INPUT> {

    /**
     * Returns true if the retry succeeded
     *
     * @param input input to retry
     * @return true if the retry succeeded
     */
    boolean retry(final INPUT input);

    void giveUp(final INPUT input);
}
