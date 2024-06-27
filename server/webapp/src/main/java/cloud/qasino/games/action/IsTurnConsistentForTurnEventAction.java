package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Slf4j
@Component
public class IsTurnConsistentForTurnEventAction implements Action<IsTurnConsistentForTurnEventAction.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("TurnEvent");
        actionDto.setErrorValue(actionDto.getSuppliedTurnEvent().getLabel());

        boolean noError = true;
        switch (actionDto.getSuppliedTurnEvent()) {
            case LOWER -> {
                noError = turnShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = turnShouldHaveNextPlayer(actionDto);
            }
            case HIGHER -> {
                noError = turnShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = turnShouldHaveNextPlayer(actionDto);
            }
            case PASS -> {
                noError = turnShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = turnShouldHaveNextPlayer(actionDto);
            }
            case BOT -> {
                noError = turnShouldHaveActiveBotPlayer(actionDto);
                if (noError) noError = turnShouldHaveNextPlayer(actionDto);
            }
            case DEAL -> {
                noError = turnShouldHaveCurrentMoveNumberNotZero(actionDto);
                if (noError) noError = turnShouldHaveActivePlayer(actionDto);
            }
            case SPLIT -> {
            }
        }
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;

    }

    private boolean turnShouldHaveCurrentMoveNumberNotZero(Dto actionDto) {
        if (actionDto.getActiveTurn().getCurrentMoveNumber() <= 0) {
            log.warn("!moveNumber");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedTurnEvent() +
                    "] invalid - turn has incorrect number of " + actionDto.getActiveTurn().getCurrentMoveNumber()
            );
            return false;
        }
        return true;
    }
    private boolean turnShouldHaveNextPlayer(Dto actionDto) {
        if (actionDto.getNextPlayer() == null) {
            log.warn("!nextplayer");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedTurnEvent() +
                    "] invalid - turn has no next player ");
            return false;
        }
        return true;
    }
    private boolean turnShouldHaveActivePlayer(Dto actionDto) {
        if (actionDto.getActiveTurn().getActivePlayer() == null) {
            log.warn("!initiator");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedTurnEvent() +
                    "] invalid - turn has no active player ");
            return false;
        }
        return true;
    }
    private boolean turnShouldHaveActiveHumanPlayer(Dto actionDto) {
        if (!actionDto.getTurnPlayer().isHuman()) {
            log.warn("!human");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedTurnEvent() +
                    "] inconsistent - this turn event is not for human player ");
            return false;
        }
        return true;
    }
    private boolean turnShouldHaveActiveBotPlayer(Dto actionDto) {
        if (actionDto.getTurnPlayer().isHuman()) {
            log.warn("!human");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedTurnEvent() +
                    "] inconsistent - this turn event is not for bot player ");
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
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        // Getters
        Turn getActiveTurn();
        Player getTurnPlayer();
        Player getNextPlayer();

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
