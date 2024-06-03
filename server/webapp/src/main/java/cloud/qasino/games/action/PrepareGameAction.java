package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.action.util.ActionUtils;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.statemachine.event.GameEvent;
import cloud.qasino.games.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrepareGameAction implements Action<PrepareGameAction.Dto, EventOutput.Result> {

    @Autowired
    GameService gameService;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // update Game
        actionDto.setQasinoGame(gameService.prepareExistingGame(
                actionDto.getQasinoGame(),
                actionDto.getQasinoGameLeague(),
                actionDto.getSuppliedStyle(),
                actionDto.getSuppliedAnte()));
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
        League getQasinoGameLeague();
        long getSuppliedLeagueId();
        int getSuppliedAnte();
        Avatar getSuppliedAvatar();
        AiLevel getSuppliedAiLevel();
        Type getSuppliedType();
        String getSuppliedStyle();
        Visitor getQasinoVisitor();

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
