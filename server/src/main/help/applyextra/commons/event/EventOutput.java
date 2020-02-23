package applyextra.commons.event;

import lombok.Getter;
import applyextra.commons.orchestration.ActionOutput;

import static applyextra.commons.state.CreditCardsStateMachine.Trigger;

@Getter
public class EventOutput implements ActionOutput<EventOutput.Result> {
    protected Result result;
    protected Trigger trigger;

	public EventOutput(Result iResult, Trigger trigger) {
		this.result = iResult;
        this.trigger = trigger;
	}

	public EventOutput(Result iResult) {
		this(iResult, null);
	}

	public enum Result {
		SUCCESS(), FAILURE(), PASS()
	}

	public boolean isFailure() {
		return result == Result.FAILURE;
	}

	public boolean isSuccess() {
		return result == Result.SUCCESS;
	}
	
	public boolean isPass() {
		return result == Result.PASS;
	}

    public static EventOutput success() {
        return new EventOutput(Result.SUCCESS);
    }

    public static EventOutput failure() {
        return new EventOutput(Result.FAILURE);
    }
    
    public static EventOutput pass() {
        return new EventOutput(Result.PASS);
    }
}
