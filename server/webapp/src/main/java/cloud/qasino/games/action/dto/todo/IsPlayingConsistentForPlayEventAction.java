package cloud.qasino.games.action.dto.todo;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Slf4j
@Component
public class IsPlayingConsistentForPlayEventAction implements Action<IsPlayingConsistentForPlayEventAction.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("PlayEvent");
        actionDto.setErrorValue(actionDto.getSuppliedPlayEvent().getLabel());

        boolean noError = true;
        switch (actionDto.getSuppliedPlayEvent()) {
            case LOWER -> {
                noError = playingShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = playingShouldHaveNextPlayer(actionDto);
            }
            case HIGHER -> {
                noError = playingShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = playingShouldHaveNextPlayer(actionDto);
            }
            case PASS -> {
                noError = playingShouldHaveActiveHumanPlayer(actionDto);
                if (noError) noError = playingShouldHaveNextPlayer(actionDto);
            }
            case BOT -> {
                noError = playingShouldHaveActiveBotPlayer(actionDto);
                if (noError) noError = playingShouldHaveNextPlayer(actionDto);
            }
            case DEAL -> {
                noError = playingShouldHaveCurrentMoveNumberNotZero(actionDto);
                if (noError) noError = playingShouldHavePlayer(actionDto);
            }
            case SPLIT -> {
            }
        }
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;

    }

    private boolean playingShouldHaveCurrentMoveNumberNotZero(Dto actionDto) {
        if (actionDto.getActivePlaying().getCurrentMoveNumber() <= 0) {
            log.warn("!moveNumber");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] invalid - playing has incorrect number of " + actionDto.getActivePlaying().getCurrentMoveNumber()
            );
            return false;
        }
        return true;
    }
    private boolean playingShouldHaveNextPlayer(Dto actionDto) {
        if (actionDto.getNextPlayer() == null) {
            log.warn("!nextplayer");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] invalid - playing has no next player ");
            return false;
        }
        return true;
    }
    private boolean playingShouldHavePlayer(Dto actionDto) {
        if (actionDto.getActivePlaying().getPlayer() == null) {
            log.warn("!initiator");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] invalid - playing has no active player ");
            return false;
        }
        return true;
    }
    private boolean playingShouldHaveActiveHumanPlayer(Dto actionDto) {
        if (!actionDto.getPlayingPlayer().isHuman()) {
            log.warn("!human");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] inconsistent - this playing event is not for human player ");
            return false;
        }
        return true;
    }
    private boolean playingShouldHaveActiveBotPlayer(Dto actionDto) {
        if (actionDto.getPlayingPlayer().isHuman()) {
            log.warn("!human");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedPlayEvent() +
                    "] inconsistent - this playing event is not for bot player ");
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
        Playing getActivePlaying();
        Player getPlayingPlayer();
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
