package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StartGameForType implements Action<StartGameForType.Dto, EventOutput.Result> {

    @Autowired
    GameService gameService;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // update a Game : create Cards for game according to the style and shuffle them
        actionDto.setQasinoGame(gameService.addAndShuffleCardsForAGame(actionDto.getQasinoGame()));
        return EventOutput.Result.SUCCESS;
    }

    public interface Dto {

        // @formatter:off
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