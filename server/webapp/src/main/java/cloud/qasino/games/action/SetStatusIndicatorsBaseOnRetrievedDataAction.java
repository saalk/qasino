package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.database.entity.enums.game.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SetStatusIndicatorsBaseOnRetrievedDataAction implements Action<SetStatusIndicatorsBaseOnRetrievedDataAction.SetStatusIndicatorsBaseOnRetrievedDataDTO, EventOutput.Result> {


    @Override
    public EventOutput.Result perform(SetStatusIndicatorsBaseOnRetrievedDataDTO actionDto) {

        log.debug("Action: SetStatusIndicatorsBaseOnRetrievedDataAction");

        actionDto.setShowVisitorPage(false);
        actionDto.setShowGameConfigurator(false);
        actionDto.setShowGamePlay(false);
        actionDto.setShowLeagues(false);
        actionDto.setShowPendingGames(false);

        if (!(actionDto.getQasinoVisitor() == null)) {
            actionDto.setShowVisitorPage(true);
            if (actionDto.getQasinoVisitor().getBalance() > 0) {
                actionDto.setShowGameConfigurator(true);
            }
        }
        if (!(actionDto.getQasinoGame() == null)) {
            if (
                    actionDto.getQasinoGame().getState() == GameState.INITIALIZED ||
                            actionDto.getQasinoGame().getState() == GameState.PREPARED) {
                actionDto.setShowGamePlay(true);
            }
        }
        if (!(actionDto.getQasinoGameLeague() == null)) {
            actionDto.setShowLeagues(true);
        }
        // todo implement friends
        actionDto.setShowPendingGames(false);

        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageCrash(SetStatusIndicatorsBaseOnRetrievedDataDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Entity not found for key" + id);
    }

    public interface SetStatusIndicatorsBaseOnRetrievedDataDTO {

        // @formatter:off
        // Getters
        Visitor getQasinoVisitor();
        Player getInvitedPlayer();
        Player getAcceptedPlayer();
        Player getTurnPlayer();

        List<Game> getNewGamesForVisitor();
        List<Game> getStartedGamesForVisitor();
        List<Game> getFinishedGamesForVisitor();

        Game getQasinoGame();
        League getQasinoGameLeague();
        List<Player> getQasinoGamePlayers();
        Turn getActiveTurn();
        List<Card> getCardsInTheGameSorted();
        List<CardMove> getAllCardMovesForTheGame();

        void setShowVisitorPage(boolean bool);
        void setShowGameConfigurator(boolean bool);
        void setShowGamePlay(boolean bool);
        void setShowPendingGames(boolean bool);
        void setShowLeagues(boolean bool);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
