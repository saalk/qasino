package cloud.qasino.games.action.game;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cloud.qasino.games.database.entity.enums.game.Style.fromLabelWithDefault;

@Slf4j
@Component
public class UpdateStyleForGame extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Resource GameService gameService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        // update Game
        Style style = fromLabelWithDefault(qasino.getGame().getStyle());
        if (qasino.getCreation().getSuppliedAnteToWin() != null) {
            style.setAnteToWin(qasino.getCreation().getSuppliedAnteToWin());
        }
        if (qasino.getCreation().getSuppliedBettingStrategy() != null) {
            style.setBettingStrategy(qasino.getCreation().getSuppliedBettingStrategy());
        }
        if (qasino.getCreation().getSuppliedDeckConfiguration() != null) {
            style.setDeckConfiguration(qasino.getCreation().getSuppliedDeckConfiguration());
        }
        if (qasino.getCreation().getSuppliedOneTimeInsurance() != null) {
            style.setOneTimeInsurance(qasino.getCreation().getSuppliedOneTimeInsurance());
        }
        if (qasino.getCreation().getSuppliedRoundsToWin() != null) {
            style.setRoundsToWin(qasino.getCreation().getSuppliedRoundsToWin());
        }
        if (qasino.getCreation().getSuppliedTurnsToWin() != null) {
            style.setTurnsToWin(qasino.getCreation().getSuppliedTurnsToWin());
        }
        qasino.setGame(gameService.updateStyleForGame(qasino.getParams(), style));
        return EventOutput.Result.SUCCESS;
    }

    private void setBadRequestErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setBadRequestErrorMessage("Supplied value for leagueName is empty");
    }

    private void setConflictErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setConflictErrorMessage("leagueName [" + value + "] not available any more");
    }
}
