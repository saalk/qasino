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
public class PrepareGameAction extends GenericLookupsAction<EventOutput.Result> {

    @Autowired
    GameService gameService;

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        // update Game
        qasino.setGame(gameService.prepareExistingGame(
                qasino.getGame(),
                qasino.getLeague(),
                qasino.getCreation().getSuppliedStyle(),
                qasino.getCreation().getSuppliedAnte()));
        return EventOutput.Result.SUCCESS;
    }

}
