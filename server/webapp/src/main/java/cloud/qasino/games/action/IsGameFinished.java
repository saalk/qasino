package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.TurnTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IsGameFinished implements Action<IsGameFinished.Dto, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (actionDto.getSuppliedTurnTrigger().equals(TurnTrigger.END_GAME)) {
            return EventOutput.Result.SUCCESS;
        }
        return EventOutput.Result.FAILURE;
    }

    private void setErrorMessageBadRequest(Dto actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

    }

    public interface Dto {
        // @formatter:off
        // Getters
        TurnTrigger getSuppliedTurnTrigger();
        // Setters
        void setQasinoVisitor(Visitor visitor);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
