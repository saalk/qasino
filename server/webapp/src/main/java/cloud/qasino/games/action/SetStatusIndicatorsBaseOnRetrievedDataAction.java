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

//        actionDto.setShowVisitorPage(false);
//        actionDto.setShowGameSetupPage(false);
//        actionDto.setShowGamePlayPage(false);
//        actionDto.setShowLeaguesPage(false);
//        actionDto.setShowGameInvitationsPage(false);
//
//        if (!(actionDto.getQasinoVisitor() == null)) {
//            actionDto.setShowVisitorPage(true);
//            if (actionDto.getQasinoVisitor().getBalance() > 0) {
//                actionDto.setShowGameSetupPage(true);
//            }
//        }
//        if (!(actionDto.getQasinoGame() == null)) {
//            if (
//                    actionDto.getQasinoGame().getState() == GameState.INITIALIZED ||
//                            actionDto.getQasinoGame().getState() == GameState.PREPARED) {
//                actionDto.setShowGamePlayPage(true);
//            }
//        }
//        if (!(actionDto.getQasinoGameLeague() == null)) {
//            actionDto.setShowLeaguesPage(true);
//        }
//        // todo implement friends
//        actionDto.setShowGameInvitationsPage(false);

        return EventOutput.Result.SUCCESS;
    }

    public interface SetStatusIndicatorsBaseOnRetrievedDataDTO {

        // @formatter:off
        // Getters
        Visitor getQasinoVisitor();
        Player getInvitedPlayer();
        Player getAcceptedPlayer();
        Player getTurnPlayer();

        Game getQasinoGame();
        League getQasinoGameLeague();
        List<Player> getQasinoGamePlayers();
        Turn getActiveTurn();
        List<Card> getCardsInTheGameSorted();
        List<CardMove> getAllCardMovesForTheGame();

        void setShowVisitorPage(boolean bool);
        void setShowGameSetupPage(boolean bool);
        void setShowGamePlayPage(boolean bool);
        void setShowGameInvitationsPage(boolean bool);
        void setShowLeaguesPage(boolean bool);

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
