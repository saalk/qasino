package cloud.qasino.games.action.dto;

import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoadPlayingDtoAction extends ActionDto<EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        boolean isPlayingFound = refreshOrFindPlayingForGame(qasino);
        if (!isPlayingFound) {
            // TODO make 404
//          return new EventOutput(EventOutput.Result.FAILURE, actionDto.getIds().getSuppliedGameEvent(), actionDto.getIds().getSuppliedPlayEvent());
            throw new MyNPException("40 isPlayingFound", "isPlayingFound [" + qasino.getParams().getSuppliedGameId() + "]");
        }
        return EventOutput.Result.SUCCESS;
    }
}


