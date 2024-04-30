package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.TurnTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class IsTurnConsistentForTurnTrigger implements Action<IsTurnConsistentForTurnTrigger.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("TurnTrigger");
        actionDto.setErrorValue(actionDto.getSuppliedTurnTrigger().getLabel());
        boolean noError = true;
        switch (actionDto.getSuppliedTurnTrigger()) {
            case LOWER -> {
            }
            case HIGHER -> {
            }
            case PASS -> {
            }
            case NEXT -> {
                break;
            }
            case DEAL -> {
                noError = turnShouldHaveCurrentMoveNumberNotZero(actionDto);
                noError = turnShouldHaveActivePlayer(actionDto);
            }
            case SPLIT -> {
            }
        }
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;
    }

    private boolean turnShouldHaveCurrentMoveNumberNotZero(Dto actionDto) {
        if (actionDto.getActiveTurn().getCurrentTurnNumber() <= 0) {
            log.info("!moveNumber");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage(
                    "Action [" +
                            actionDto.getSuppliedTurnTrigger() +
                            "] invalid - turn has incorrect number of " +
                            actionDto.getActiveTurn().getCurrentTurnNumber()
            );
            return false;
        }
        return true;
    }

    private boolean turnShouldHaveActivePlayer(Dto actionDto) {
        if (actionDto.getActiveTurn().getActivePlayerId() == 0) {
            log.info("!initiator");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage(
                    "Action [" +
                            actionDto.getSuppliedTurnTrigger() +
                            "] invalid - turn has no active player "
            );
            return false;
        }
        return true;
    }

    public interface Dto {

        // @formatter:off
        // Getters
        TurnTrigger getSuppliedTurnTrigger();
        Turn getActiveTurn();

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
