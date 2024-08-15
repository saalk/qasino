package cloud.qasino.games.action.game;

import cloud.qasino.games.action.dto.ActionDto;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
