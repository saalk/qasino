package cloud.qasino.games.statemachine.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventOutput {
    protected Result result;
    protected GameEvent trigger;

    public EventOutput(Result result) {
        this(result, null);
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