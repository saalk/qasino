package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.GamingTable;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Slf4j
@Component
public class IsGamingTableConsistentForPlayEventAction implements Action<IsGamingTableConsistentForPlayEventAction.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("PlayEvent");
        actionDto.setErrorValue(actionDto.getSuppliedPlayEvent().getLabel());

        boolean noError = true;
        switch (actionDto.getSuppliedPlayEvent()) {
            case LOWER -> {
                noError = gamingTableShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = gamingTableShouldHaveNextPlayer(actionDto);
            }
            case HIGHER -> {
                noError = gamingTableShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = gamingTableShouldHaveNextPlayer(actionDto);
            }
            case PASS -> {
                noError = gamingTableShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = gamingTableShouldHaveNextPlayer(actionDto);
            }
            case BOT -> {
                noError = gamingTableShouldHaveActiveBotPlayer(actionDto);
                if (noError) noError = gamingTableShouldHaveNextPlayer(actionDto);
            }
            case DEAL -> {
                noError = gamingTableShouldHaveCurrentMoveNumberNotZero(actionDto);
                if (noError) noError = gamingTableShouldHaveActivePlayer(actionDto);
            }
            case SPLIT -> {
            }
        }
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;

    }

    private boolean gamingTableShouldHaveCurrentMoveNumberNotZero(Dto actionDto) {
        if (actionDto.getActiveGamingTable().getCurrentMoveNumber() <= 0) {
            log.warn("!moveNumber");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] invalid - gamingTable has incorrect number of " + actionDto.getActiveGamingTable().getCurrentMoveNumber()
            );
            return false;
        }
        return true;
    }
    private boolean gamingTableShouldHaveNextPlayer(Dto actionDto) {
        if (actionDto.getNextPlayer() == null) {
            log.warn("!nextplayer");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] invalid - gamingTable has no next player ");
            return false;
        }
        return true;
    }
    private boolean gamingTableShouldHaveActivePlayer(Dto actionDto) {
        if (actionDto.getActiveGamingTable().getActivePlayer() == null) {
            log.warn("!initiator");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] invalid - gamingTable has no active player ");
            return false;
        }
        return true;
    }
    private boolean gamingTableShouldHaveActiveHumanPlayer(Dto actionDto) {
        if (!actionDto.getGamingTablePlayer().isHuman()) {
            log.warn("!human");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] inconsistent - this gamingTable event is not for human player ");
            return false;
        }
        return true;
    }
    private boolean gamingTableShouldHaveActiveBotPlayer(Dto actionDto) {
        if (actionDto.getGamingTablePlayer().isHuman()) {
            log.warn("!human");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] inconsistent - this gamingTable event is not for bot player ");
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
        PlayEvent getSuppliedPlayEvent();

        // Getters
        GamingTable getActiveGamingTable();
        Player getGamingTablePlayer();
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
