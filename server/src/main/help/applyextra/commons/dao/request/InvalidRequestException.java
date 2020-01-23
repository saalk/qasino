package applyextra.commons.dao.request;

/**
 * Created by cl94wq on 22-02-16.
 */
public class InvalidRequestException extends RuntimeException {
	public InvalidRequestException(String message) {
		super(message);
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
