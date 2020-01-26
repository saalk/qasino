package applyextra.commons.helper;

/**
 * This class provides a number of utility methods to quickly report common Graphite metrics.
 */
public interface GraphiteHelper {

    /**
     * Start a timing metric, using the time provided by the TimeProvider. The caller is responsible for storing the starting time
     * and using it in the {@link GraphiteHelperImpl#stopTiming(String, String, long)} as the starting time parameter.
     *
     * @return the time this method is called in milliseconds
     */
    long startTiming();

    /**
     * Stop the timing of a certain action, calculate the number of milliseconds and report it to Graphite with the provided
     * category and name.
     *
     * @param category the category part of the Graphite metrics key
     * @param name the name of what is measured
     * @param start the starting time of this metric, as provided by startTiming
     */
    void stopTiming(final String category, final String name, final long start);

    /**
     * Increase the request calls counter in the provided category.
     *
     * @param category the category part of the Graphite metrics key
     */
    void requestCounter(final String category);

    /**
     * Increase the response counter in the provided category.
     *
     * Success indicates if the request was responded to correctly, and might not necessarily mean that the response data can
     * actually be used.
     *
     * @param category the category part of the Graphite metrics key
     * @param success the request was received and handled successfully, or and error occurred
     */
    void responseCounter(final String category, final boolean success);

    /**
     * Increase the response counter in the provided category, including the number of retries. If the number of retries is less
     * than 1, the {@link GraphiteHelperImpl#responseCounter(String, boolean)} is called instead.
     *
     * Success indicates if the request was responded to correctly, and might not necessarily mean that the response data can
     * actually be used.
     *
     * @param category the category part of the Graphite metrics key
     * @param success the request was received and handled successfully, or and error occurred
     * @param retries the number of retries for the request call
     */
    void responseCounter(final String category, final boolean success, final int retries);

    /**
     * This updates or creates the counter with the specified category and name.
     *
     * If the counter doesn't exist, then the counter is created with a value of 0, and then increased by the value of delta
     * If the counter exists, then delta is added to the counter.
     *
     * Delta can be negative to decrease the counter.
     *
     * @param category the category part of the Graphite metrics key
     * @param name the name of what is measured
     * @param delta the difference
     */
    void customCounter(final String category, final String name, final int delta);

    /**
     * Calculate the time difference between now and the startingTime. If startingTime is null, then startingTime
     * of zero is assumed
     *
     * @param startingTime starting time of the time difference calculation
     * @return the difference in milliseconds
     */
    long timeDifference(final Long startingTime);

}
