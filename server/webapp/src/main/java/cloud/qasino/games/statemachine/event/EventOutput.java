package cloud.qasino.games.statemachine.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventOutput {

    //@formatter:off

    protected Result result;
    protected GameEvent gameEvent;
    protected TurnEvent turnEvent;

    public static EventOutput success(GameEvent gameEvent,TurnEvent turnEvent) {
        return new EventOutput(Result.SUCCESS, gameEvent,turnEvent);
    }
    public static EventOutput failure(GameEvent gameEvent,TurnEvent turnEvent) {
        return new EventOutput(Result.FAILURE, gameEvent,turnEvent);
    }
    public boolean isFailure() {
        return result == Result.FAILURE;
    }
    public boolean isSuccess() {
        return result == Result.SUCCESS;
    }

    public enum Result {SUCCESS(), FAILURE()}
}
