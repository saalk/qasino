package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class IsTurnConsistentForTurnEvent implements Action<IsTurnConsistentForTurnEvent.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("TurnEvent");
        actionDto.setErrorValue(actionDto.getSuppliedTurnEvent().getLabel());

        boolean noError = true;
        switch (actionDto.getSuppliedTurnEvent()) {
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
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedTurnEvent() +
                    "] invalid - turn has incorrect number of " + actionDto.getActiveTurn().getCurrentTurnNumber()
            );
            return false;
        }
        return true;
    }

    private boolean turnShouldHaveActivePlayer(Dto actionDto) {
        if (actionDto.getActiveTurn().getActivePlayerId() == 0) {
            log.info("!initiator");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedTurnEvent() +
                    "] invalid - turn has no active player ");
            return false;
        }
        return true;
    }

    void setUnprocessableErrorMessage(Dto actionDto, String reason) {
//        actionDto.setErrorKey(id); - already set
//        actionDto.setErrorValue(value); - already set
        actionDto.setUnprocessableErrorMessage(reason);
    }

    public interface Dto {

        // @formatter:off
        // Getters
        TurnEvent getSuppliedTurnEvent();
        Turn getActiveTurn();

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
