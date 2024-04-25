package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

@Slf4j
@Component
public class IsGameConsistentForGameTrigger implements Action<IsGameConsistentForGameTrigger.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("GameTrigger");
        actionDto.setErrorValue(actionDto.getSuppliedGameTrigger().getLabel());

        if (actionDto.getQasinoGamePlayers() == null) {
            log.info("!players");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage("Action [" + actionDto.getSuppliedGameTrigger() + "] invalid - game has no players");
            return EventOutput.Result.FAILURE;
        }
        if (actionDto.getQasinoGame().getState() != GameState.PREPARED) {
            log.info("!state");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage("Action [" + actionDto.getSuppliedGameTrigger() + "] invalid - game is not in prepared state");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    public interface Dto {

        // @formatter:off
        // Getters
        Visitor getQasinoGamePlayers();
        GameTrigger getSuppliedGameTrigger();
        Game getQasinoGame();

        // error setters and getters
        void setHttpStatus(int status);
        int getHttpStatus();
        void setErrorKey(String errorKey);
        String getErrorKey();
        void setErrorValue(String errorValue);
        String getErrorValue();
        void setErrorMessage(String message);
        // @formatter:on
    }
}
