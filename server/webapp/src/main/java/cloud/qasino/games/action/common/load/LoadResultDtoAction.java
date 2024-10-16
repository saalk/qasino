package cloud.qasino.games.action.common.load;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoadResultDtoAction extends GenericLookupsAction<EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        boolean isResultFound = refreshOrFindResultsForGame(qasino);
        if (!isResultFound) {
            // TODO make 404
//          return new EventOutput(EventOutput.Result.FAILURE, actionDto.getIds().getSuppliedGameEvent(), actionDto.getIds().getSuppliedPlayEvent());
            throw new MyNPException("39 getResultSupplied", "resultId [" + qasino.getParams().getSuppliedGameId() + "]");
        }
        return EventOutput.Result.SUCCESS;
    }
}
