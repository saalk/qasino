package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.cardengine.action.dto.ActionDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoadGameFromIdsDtoAction extends ActionDto<EventOutput.Result> {

    @Override
    public EventOutput.Result perform() {

        boolean isGameFound = refreshOrFindLatestGame();
        if (!isGameFound) {
            // TODO make 404
//          return new EventOutput(EventOutput.Result.FAILURE, actionDto.getIds().getSuppliedGameEvent(), actionDto.getIds().getSuppliedTurnEvent());
            throw new MyNPException("69 getVisitorSupplied", "visitorId [" + super.getIds().getSuppliedVisitorId() + "]");
        }
        return EventOutput.Result.SUCCESS;
    }
}
