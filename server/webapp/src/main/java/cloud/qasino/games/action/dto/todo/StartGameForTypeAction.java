package cloud.qasino.games.action.dto.todo;

import cloud.qasino.games.action.dto.ActionDto;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.database.service.GameServiceOld;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StartGameForTypeAction extends ActionDto<EventOutput.Result> {

    // @formatter:off
    @Autowired GameService gameService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        // update a Game : create Cards for game according to the style and shuffle them
        qasino.setGame(gameService.addAndShuffleCardsForAGame(qasino.getGame()));
        return EventOutput.Result.SUCCESS;
    }

}
