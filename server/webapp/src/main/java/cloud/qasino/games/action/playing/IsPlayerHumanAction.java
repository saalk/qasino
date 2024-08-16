package cloud.qasino.games.action.playing;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IsPlayerHumanAction extends GenericLookupsAction<EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (qasino.getPlaying().getCurrentPlayer().isHuman()) {
            return EventOutput.Result.SUCCESS;
        }
        return EventOutput.Result.FAILURE;
    }

    private void setBadRequestErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setBadRequestErrorMessage("Action [" + id + "] invalid");
    }
}
