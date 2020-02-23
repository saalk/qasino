package applyextra.commons.response;

import lombok.Getter;

@Getter
public final class RuntimeExceptionResponse {

    private final int errorCode;
    private final int httpStatusCode;
    private final Long timestamp;

    public RuntimeExceptionResponse(final int errorCode, final int httpStatusCode) {
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
        this.timestamp = System.currentTimeMillis();
    }
}
