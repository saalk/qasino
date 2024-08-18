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
            gameService.updateStateForGame(qasino.getParams(), GameState.INITIATOR_MOVE);
            qasino.getGame().setState(GameState.FINISHED);
            return EventOutput.Result.SUCCESS;
        }
        return EventOutput.Result.FAILURE;
    }

}
