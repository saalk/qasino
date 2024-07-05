package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
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
public class StartGameForTypeAction implements Action<StartGameForTypeAction.Dto, EventOutput.Result> {

    @Autowired
    GameServiceOld gameServiceOld;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // update a Game : create Cards for game according to the style and shuffle them
        actionDto.setQasinoGame(gameServiceOld.addAndShuffleCardsForGame(actionDto.getQasinoGame()));
        return EventOutput.Result.SUCCESS;
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        PlayEvent getSuppliedPlayEvent();

        // Getters
        List<Player> getQasinoGamePlayers();
        Game getQasinoGame();

        void setQasinoGame(Game game);

        // error setters
        // @formatter:off
        void setBadRequestErrorMessage(String problem);
        void setNotFoundErrorMessage(String problem);
        void setConflictErrorMessage(String reason);
        void setUnprocessableErrorMessage(String reason);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        // @formatter:on
    }
}
