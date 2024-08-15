package cloud.qasino.games.action.playing;

import cloud.qasino.games.action.dto.ActionDto;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdatePlayingStateForGame extends ActionDto<EventOutput.Result> {

    // @formatter:off
    @Autowired GameRepository gameRepository;
    @Autowired private GameService gameService;

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (qasino.getPlaying() == null || qasino.getPlaying().getCurrentPlayer() == null) return EventOutput.Result.SUCCESS;
        switch (qasino.getParams().getSuppliedPlayEvent()) {
            case PASS -> {
                // next player
                qasino.setGame(gameService.updatePlayingStateForGame(qasino.getPlaying().getNextPlayer(), qasino.getGame().getGameId()));
            }
            case DEAL, HIGHER, LOWER, BOT -> {
                // existing player
                qasino.setGame(gameService.updatePlayingStateForGame(qasino.getPlaying().getCurrentPlayer(), qasino.getGame().getGameId()));
            }
        }
        return EventOutput.Result.SUCCESS;
    }
}
