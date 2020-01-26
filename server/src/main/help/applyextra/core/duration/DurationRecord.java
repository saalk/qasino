package applyextra.core.duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The purpose of this class is to contain duration information
 */
public class DurationRecord {

	private static final Logger LOG = LoggerFactory.getLogger(DurationLog.class);
	private static final long MICROSEC_TO_SEC = 1000000; 
	
	private final String function;
	private final long startTime;

	/**
	 * Constructor
	 * @param function function called
	 */
	public DurationRecord(final String function) {
		if (LOG.isDebugEnabled()) {
			this.function = function;
			this.startTime = System.nanoTime();
		} else {
			this.function = null;
			this.startTime = 0;
		}
	}
	
	/**
	 * Logs the duration information if debug log is enabled
	 */
	public void stop() {
		if (LOG.isDebugEnabled()) {
			final long stopTime = System.nanoTime();
			final long duration = stopTime - startTime;
			final double durationInMilli = duration / (double) MICROSEC_TO_SEC;
			final StringBuilder builder = new StringBuilder();
			builder.append("PERFLOG Thread(");
			builder.append(Thread.currentThread().getId());
			builder.append(") function ");
			builder.append(function);
			builder.append(" took duration=");
			builder.append(durationInMilli);
			LOG.debug(builder.toString());
		}
	}

	/**
	 * Returns the function
	 * @return function
	 */
    public String getFunction() {
		return function;
	}

    /**
     * Returns the start time
     * @return start time
     */
	public long getStartTime() {
		return startTime;
	}


}
