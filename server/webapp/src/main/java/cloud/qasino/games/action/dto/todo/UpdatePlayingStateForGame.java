package cloud.qasino.games.action.dto.todo;

import cloud.qasino.games.action.dto.ActionDto;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.database.service.GameServiceOld;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
