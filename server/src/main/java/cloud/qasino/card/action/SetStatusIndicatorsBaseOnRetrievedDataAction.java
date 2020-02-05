package cloud.qasino.card.action;

import cloud.qasino.card.action.interfaces.Action;
import cloud.qasino.card.entity.*;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.statemachine.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SetStatusIndicatorsBaseOnRetrievedDataAction implements Action<SetStatusIndicatorsBaseOnRetrievedDataAction.SetStatusIndicatorsBaseOnRetrievedDataDTO, EventOutput.Result> {


    @Override
    public EventOutput.Result perform(SetStatusIndicatorsBaseOnRetrievedDataDTO actionDto) {

        log.debug("Action: SetStatusIndicatorsBaseOnRetrievedDataAction");

        actionDto.setLoggedOn(false);
        actionDto.setBalanceNotZero(false);
        actionDto.setGamePlayable(false);
        actionDto.setLeaguePresent(false);
        actionDto.setFriendsPresent(false);

        if (!(actionDto.getGameUser() == null)) {
            actionDto.setLoggedOn(true);
            if (actionDto.getGameUser().getBalance() > 0) {
                actionDto.setBalanceNotZero(true);
            }
        }
        if (!(actionDto.getQasinoGame() == null)) {
            if (
                    actionDto.getQasinoGame().getState() == GameState.PLAYING ||
                            actionDto.getQasinoGame().getState() == GameState.PREPARED) {
                actionDto.setGamePlayable(true);
            }
        }
        if (!(actionDto.getQasinoGameLeague() == null)) {
            actionDto.setLeaguePresent(true);
        }
        // todo implement friends
        actionDto.setFriendsPresent(false);

        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(SetStatusIndicatorsBaseOnRetrievedDataDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Entity not found for key" + id);
        actionDto.setUriAndHeaders();
    }

    public interface SetStatusIndicatorsBaseOnRetrievedDataDTO {

        // @formatter:off
        // Getters
        User getGameUser();
        Player getInvitedPlayer();
        Player getAcceptedPlayer();
        Player getTurnPlayer();

        List<Game> getNewGamesForUser();
        List<Game> getStartedGamesForUser();
        List<Game> getFinishedGamesForUser();

        Game getQasinoGame();
        League getQasinoGameLeague();
        List<Player> getQasinoGamePlayers();
        Turn getQasinoGameTurn();
        List<Card> getQasinoGameCards();
        List<CardMove> getQasinoGameCardMoves();

        void setLoggedOn(boolean bool);
        void setBalanceNotZero(boolean bool);
        void setGamePlayable(boolean bool);
        void setLeaguePresent(boolean bool);
        void setFriendsPresent(boolean bool);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String key);
        void setErrorValue(String value);
        void setErrorMessage(String key);
        void setUriAndHeaders();
        // @formatter:on
    }
}
