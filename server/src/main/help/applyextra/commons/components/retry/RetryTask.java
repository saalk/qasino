package applyextra.commons.components.retry;

/**
 * Created by CL94WQ on 13-07-16.
 */
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
