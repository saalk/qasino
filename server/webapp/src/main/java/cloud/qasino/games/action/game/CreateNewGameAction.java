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
public class CreateNewGameAction extends GenericLookupsAction<EventOutput.Result> {

    @Autowired
    GameService gameService;

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        qasino.setGame(gameService.setupNewGameWithPlayerInitiator(
                qasino.getCreation(),
                qasino.getParams().getSuppliedVisitorId()
        ));
        qasino.getParams().setSuppliedGameId(qasino.getGame().getGameId());
        return EventOutput.Result.SUCCESS;
    }
}
