package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class IsGameFinished implements Action<IsGameFinished.Dto, EventOutput.Result> {

    @Resource
    GameRepository gameRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (actionDto.getSuppliedTurnEvent().equals(TurnEvent.END_GAME)) {
            actionDto.getQasinoGame().setState(GameState.FINISHED);
            gameRepository.save(actionDto.getQasinoGame());
            return EventOutput.Result.SUCCESS;
        }
        return EventOutput.Result.FAILURE;
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("Action [" + id + "] invalid");
    }

    public interface Dto {
        // @formatter:off
        // Getters
        TurnEvent getSuppliedTurnEvent();
        Game getQasinoGame();
        // Setters
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
