package cloud.qasino.games.action.game;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartGameForTypeAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Autowired GameService gameService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        // update a Game : create Cards for game according to the style and shuffle them
        qasino.setGame(gameService.addAndShuffleCardsForAGame(qasino.getParams()));
        return EventOutput.Result.SUCCESS;
    }

}
