package cloud.qasino.games.pattern.statemachine.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventOutput {

    //@formatter:off

    protected Result result;
    protected GameEvent gameEvent;
    protected PlayEvent playEvent;

    public static EventOutput success(GameEvent gameEvent,PlayEvent playEvent) {
        return new EventOutput(Result.SUCCESS, gameEvent,playEvent);
    }
    public static EventOutput failure(GameEvent gameEvent,PlayEvent playEvent) {
        return new EventOutput(Result.FAILURE, gameEvent,playEvent);
    }
    public boolean isFailure() {
        return result == Result.FAILURE;
    }
    public boolean isSuccess() {
        return result == Result.SUCCESS;
    }

    public enum Result {SUCCESS(), FAILURE()}
}
