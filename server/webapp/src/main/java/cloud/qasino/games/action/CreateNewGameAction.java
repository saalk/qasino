package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.service.PlayService;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CreateNewGameAction implements Action<CreateNewGameAction.Dto, EventOutput.Result> {

    @Autowired
    PlayService playService;
    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setQasinoGame(playService.setupNewGameWithPlayers(
                actionDto.getSuppliedType().getLabel(),
                actionDto.getQasinoVisitor(),
                actionDto.getQasinoGameLeague(),
                actionDto.getSuppliedAiLevel(),
                actionDto.getSuppliedStyle(),
                String.valueOf(actionDto.getSuppliedAnte()),
                actionDto.getSuppliedAvatar())
        );
        actionDto.setSuppliedGameId(actionDto.getQasinoGame().getGameId());
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
        // Getters
        League getQasinoGameLeague();
        int getSuppliedAnte();
        void setSuppliedGameId(long id);
        Avatar getSuppliedAvatar();
        AiLevel getSuppliedAiLevel();
        Type getSuppliedType();
        String getSuppliedStyle();
        Visitor getQasinoVisitor();

        GameTrigger getSuppliedGameTrigger();
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
