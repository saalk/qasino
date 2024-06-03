package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UpdatePlayingStateForGame implements Action<UpdatePlayingStateForGame.Dto, EventOutput.Result> {

    @Autowired
    GameRepository gameRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!(actionDto.getTurnPlayer().isHuman())) {
            if (actionDto.getQasinoGame().getState() != GameState.BOT_MOVE) {
                actionDto.getQasinoGame().setState(GameState.BOT_MOVE);
                actionDto.setQasinoGame(gameRepository.save(actionDto.getQasinoGame()));
            }
        }
        if ((actionDto.getTurnPlayer().isHuman())) {
            if (actionDto.getQasinoGame().getInitiator() == actionDto.getTurnPlayer().getPlayerId()) {
                if (actionDto.getQasinoGame().getState() != GameState.INITIATOR_MOVE) {
                    actionDto.getQasinoGame().setState(GameState.INITIATOR_MOVE);
                    actionDto.setQasinoGame(gameRepository.save(actionDto.getQasinoGame()));
                } else {
                    if (actionDto.getQasinoGame().getState() != GameState.INVITEE_MOVE) {
                        actionDto.getQasinoGame().setState(GameState.INVITEE_MOVE);
                        actionDto.setQasinoGame(gameRepository.save(actionDto.getQasinoGame()));
                    }
                }
            }

        }

        return EventOutput.Result.SUCCESS;
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("Supplied value for leagueName is empty");
    }

    private void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("leagueName [" + value + "] not available any more");
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        // Getters
        Visitor getQasinoVisitor();
        Game getQasinoGame();
        List<Player> getQasinoGamePlayers();
        Player getTurnPlayer();

        // Setter
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
