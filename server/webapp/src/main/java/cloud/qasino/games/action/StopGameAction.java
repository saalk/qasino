package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.statemachine.event.GameEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class StopGameAction implements Action<StopGameAction.Dto, EventOutput.Result> {

    @Resource
    GameRepository gameRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (actionDto.getSuppliedGameEvent().equals(GameEvent.STOP)) {
            actionDto.getQasinoGame().setState(GameState.QUIT);
            gameRepository.save(actionDto.getQasinoGame());
            return EventOutput.Result.SUCCESS;
        }
        return EventOutput.Result.FAILURE;
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
        // Getters
        League getQasinoGameLeague();
        int getSuppliedAnte();
        Avatar getSuppliedAvatar();
        AiLevel getSuppliedAiLevel();
        Type getSuppliedType();
        String getSuppliedStyle();
        Visitor getQasinoVisitor();

        GameEvent getSuppliedGameEvent();
        Game getQasinoGame();

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