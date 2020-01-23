package applyextra.commons.activity;

import lombok.Getter;

@Getter
public class ActivityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String serviceName;
	private int errorCode;

    public ActivityException(final String serviceName, final String message) {
        super(message);
        this.serviceName = serviceName;
        this.errorCode = 0;
    }

    public ActivityException(final String serviceName, final String message, final Throwable cause) {
		super(message, cause);
		this.serviceName = serviceName;
        this.errorCode = 0;
    }

	public ActivityException(final String serviceName, final int errorCode, final String message, final Throwable cause) {
		super(message, cause);
		this.serviceName = serviceName;
		this.errorCode = errorCode;
	}
}
