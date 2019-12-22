package cloud.qasino.card.event.common;

import cloud.qasino.card.statemachine.CardGameStateMachine;
import lombok.Getter;

@Getter
public class EventOutput {
	protected Result result;
    protected CardGameStateMachine.Trigger trigger;

	public EventOutput(Result iResult, CardGameStateMachine.Trigger trigger) {
		this.result = iResult;
        this.trigger = trigger;
	}

	public EventOutput(Result iResult) {
		this(iResult, null);
	}

	public enum Result {
		SUCCESS(), FAILURE()
	}

	public boolean isFailure() {
		return result == Result.FAILURE;
	}

	public boolean isSuccess() {
		return result == Result.SUCCESS;
	}

    public static EventOutput success() {
        return new EventOutput(Result.SUCCESS);
    }

    public static EventOutput failure() {
        return new EventOutput(Result.FAILURE);
    }
}
