package cloud.qasino.games.action.game;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Slf4j
@Component
public class StopGameAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Resource GameService gameService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (qasino.getParams().getSuppliedGameEvent().equals(GameEvent.STOP)) {
            gameService.updateStateForGame(GameState.QUIT, qasino.getParams().getSuppliedGameId());
            qasino.getGame().setState(GameState.QUIT);
            return EventOutput.Result.SUCCESS;
        }
        return EventOutput.Result.FAILURE;
    }

    private void setBadRequestErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setBadRequestErrorMessage("Supplied value for leagueName is empty");
    }
    private void setConflictErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setConflictErrorMessage("leagueName [" + value + "] not available any more");
    }

}
