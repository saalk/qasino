package applyextra.commons.activity;

import lombok.Getter;
import applyextra.commons.event.EventOutput;

/**
 * @deprecated use {@link applyextra.commons.event.EventOutput} instead
 */
@Deprecated
@Getter
public class ActivityOutput<T> extends EventOutput {

    final private T resultData;

    public ActivityOutput(Result iResult, T iResultData) {
        super(iResult);
		this.result = iResult;
		this.resultData = iResultData;
	}

	public ActivityOutput(Result iResult) {
		this(iResult, null);
	}
}
