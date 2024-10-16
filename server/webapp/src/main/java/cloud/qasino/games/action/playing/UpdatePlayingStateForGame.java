package cloud.qasino.games.action.playing;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdatePlayingStateForGame extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Resource GameService gameService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (qasino.getPlaying() == null || qasino.getPlaying().getCurrentPlayer() == null) return EventOutput.Result.SUCCESS;
        switch (qasino.getParams().getSuppliedPlayEvent()) {
            case PASS -> // next player
                    qasino.setGame(gameService.updatePlayingStateForGame(qasino.getParams(), qasino.getPlaying().getNextPlayer()));
            case DEAL, HIGHER, LOWER, BOT -> // existing player
                    qasino.setGame(gameService.updatePlayingStateForGame(qasino.getParams(), qasino.getPlaying().getCurrentPlayer()));
        }
        if (qasino.getGame().getPlayers().isEmpty()) {
            throw new MyNPException("UpdatePlayingStateForGame", "error [" + qasino.getGame()+ "]");
        }
        return EventOutput.Result.SUCCESS;
    }
}
