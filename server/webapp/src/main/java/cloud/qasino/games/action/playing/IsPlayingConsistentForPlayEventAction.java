package cloud.qasino.games.action.playing;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IsPlayingConsistentForPlayEventAction extends GenericLookupsAction<EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        boolean noError = true;
        switch (qasino.getParams().getSuppliedPlayEvent()) {
            case LOWER -> {
                noError = playingShouldHaveActiveHumanPlayer(qasino);
                if (noError) noError = playingShouldHaveNextPlayer(qasino);
            }
            case HIGHER -> {
                noError = playingShouldHaveActiveHumanPlayer(qasino);
                if (noError) noError = playingShouldHaveNextPlayer(qasino);
            }
            case PASS -> {
                noError = playingShouldHaveActiveHumanPlayer(qasino);
                if (noError) noError = playingShouldHaveNextPlayer(qasino);
            }
            case BOT -> {
                noError = playingShouldHaveActiveBotPlayer(qasino);
                if (noError) noError = playingShouldHaveNextPlayer(qasino);
            }
            case DEAL -> {
                noError = playingShouldHaveCurrentMoveNumberNotZero(qasino);
                if (noError) noError = playingShouldHavePlayer(qasino);
            }
            case SPLIT -> {
            }
        }
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;

    }

    private boolean playingShouldHaveCurrentMoveNumberNotZero(Qasino qasino) {
        if (qasino.getPlaying().getCurrentMoveNumber() <= 0) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "PlayEvent",
                    qasino.getParams().getSuppliedPlayEvent().getLabel(), "Action [" + qasino.getParams().getSuppliedPlayEvent() +
                            "] invalid - playing has incorrect number of " + qasino.getPlaying().getCurrentMoveNumber()
            );
            return false;
        }
        return true;
    }

    private boolean playingShouldHaveNextPlayer(Qasino qasino) {
        if (qasino.getPlaying().getNextPlayer() == null) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "PlayEvent",
                    qasino.getParams().getSuppliedPlayEvent().getLabel(), "Action [" + qasino.getParams().getSuppliedPlayEvent() +
                            "] invalid - playing has no next player ");
            return false;
        }
        return true;
    }

    private boolean playingShouldHavePlayer(Qasino qasino) {
        if (qasino.getPlaying().getCurrentPlayer() == null) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "PlayEvent",
                    qasino.getParams().getSuppliedPlayEvent().getLabel(), "Action [" + qasino.getParams().getSuppliedPlayEvent() +
                            "] invalid - playing has no active player ");
            return false;
        }
        return true;
    }

    private boolean playingShouldHaveActiveHumanPlayer(Qasino qasino) {
        if (!qasino.getPlaying().getCurrentPlayer().isHuman()) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "PlayEvent",
                    qasino.getParams().getSuppliedPlayEvent().getLabel(), "Action [" + qasino.getParams().getSuppliedPlayEvent() +
                            "] inconsistent - this playing event is not for human player ");
            return false;
        }
        return true;
    }

    private boolean playingShouldHaveActiveBotPlayer(Qasino qasino) {
        if (qasino.getPlaying().getCurrentPlayer().isHuman()) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "PlayEvent",
                    qasino.getParams().getSuppliedPlayEvent().getLabel(),
                    "Action [" + qasino.getParams().getSuppliedPlayEvent() +
                            "] inconsistent - this playing event is not for bot player ");
            return false;
        }
        return true;
    }
}
