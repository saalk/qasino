package applyextra.core.exception;

/**
 * RIAF Runtime exception used for serious unrecoverable errors instead of runtime exception
 */
public class RIAFRuntimeException extends RuntimeException {

	/**
	 * Serial version UID for this class.
	 */
	private static final long serialVersionUID = -1874333927736038179L;

	/**
	 * Default construction of the RIAFRuntimeException.
	 */
	public RIAFRuntimeException() {
		super();
	}
	
	/**
	 * Constructor of the RIAFRuntimeException with a message
	 * 
	 * @param message String with message to be passed
	 */
	public RIAFRuntimeException(final String message) {
		super(message);
	}
	
	/**
	 * Overridden constructor, which will take <code>Throwable</code> as
	 * parameter.
	 * 
	 * @param cause
	 *            BaseException, which is being wrapped in this constructor.
	 */
	public RIAFRuntimeException(final Throwable cause) {
		super(cause);
	}
	
	/**
	 * Overridden constructor, which will take message and
	 * <code>Throwable</code> as parameter.
	 * 
	 * @param message
	 *            message to be displayed to user or to be logged by
	 *            application.
	 * @param cause
	 *            BaseException, which is being wrapped in this constructor.
	 */
	public RIAFRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}	
}
