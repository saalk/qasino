package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.TurnTrigger;
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

        if (actionDto.getSuppliedTurnTrigger().equals(TurnTrigger.END_GAME)) {
            actionDto.getQasinoGame().setState(GameState.FINISHED);
            gameRepository.save(actionDto.getQasinoGame());
            return EventOutput.Result.SUCCESS;
        }
        return EventOutput.Result.FAILURE;
    }

    private void setErrorMessageBadRequest(Dto actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

    }

    public interface Dto {
        // @formatter:off
        // Getters
        TurnTrigger getSuppliedTurnTrigger();
        Game getQasinoGame();
        // Setters
        void setQasinoGame(Game game);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
