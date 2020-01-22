package cloud.qasino.card.dto.event;

import cloud.qasino.card.controller.statemachine.GameTrigger;
import lombok.Getter;

@Getter
public class EventOutput {
    protected Result result;
    protected GameTrigger trigger;

    public EventOutput(Result iResult, GameTrigger trigger) {
        this.result = iResult;
        this.trigger = trigger;
    }

    public EventOutput(Result iResult) {
        this(iResult, null);
    }

    public static EventOutput success() {
        return new EventOutput(Result.SUCCESS);
    }

    public static EventOutput failure() {
        return new EventOutput(Result.FAILURE);
    }

    public boolean isFailure() {
        return result == Result.FAILURE;
    }

    public boolean isSuccess() {
        return result == Result.SUCCESS;
    }

    public enum Result {
        SUCCESS(), FAILURE()
    }
}
