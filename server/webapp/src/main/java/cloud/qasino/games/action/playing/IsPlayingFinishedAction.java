package cloud.qasino.games.action.playing;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Slf4j
@Component
public class IsPlayingFinishedAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Resource GameService gameService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (qasino.getParams().getSuppliedPlayEvent().equals(PlayEvent.PASS)) {
            gameService.updateStateForGame(GameState.INITIATOR_MOVE, qasino.getParams().getSuppliedGameId());
            qasino.getGame().setState(GameState.FINISHED);
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
