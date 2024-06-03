package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cloud.qasino.games.database.entity.enums.game.Style.fromLabelWithDefault;

@Slf4j
@Component
public class UpdateStyleForGame implements Action<UpdateStyleForGame.Dto, EventOutput.Result> {

    @Autowired
    GameRepository gameRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // update Game
        Style style = fromLabelWithDefault(actionDto.getQasinoGame().getStyle());
        if (actionDto.getSuppliedAnteToWin()!=null) {
            style.setAnteToWin(actionDto.getSuppliedAnteToWin());
        }
        if (actionDto.getSuppliedBettingStrategy()!=null) {
            style.setBettingStrategy(actionDto.getSuppliedBettingStrategy());
        }
        if (actionDto.getSuppliedDeckConfiguration()!=null) {
            style.setDeckConfiguration(actionDto.getSuppliedDeckConfiguration());
        }
        if (actionDto.getSuppliedOneTimeInsurance()!=null) {
            style.setOneTimeInsurance(actionDto.getSuppliedOneTimeInsurance());
        }
        if (actionDto.getSuppliedRoundsToWin() != null) {
            style.setRoundsToWin(actionDto.getSuppliedRoundsToWin());
        }
        if (actionDto.getSuppliedTurnsToWin()!=null) {
            style.setTurnsToWin(actionDto.getSuppliedTurnsToWin());
        }
        style.setLabel(style.updateLabelFromEnums());

        actionDto.getQasinoGame().setStyle(style.getLabel());
        actionDto.setQasinoGame(gameRepository.save(actionDto.getQasinoGame()));

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
        AnteToWin getSuppliedAnteToWin();
        BettingStrategy getSuppliedBettingStrategy();
        DeckConfiguration getSuppliedDeckConfiguration();
        OneTimeInsurance getSuppliedOneTimeInsurance();
        RoundsToWin getSuppliedRoundsToWin();
        TurnsToWin getSuppliedTurnsToWin();


        League getQasinoGameLeague();
        int getSuppliedAnte();
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
