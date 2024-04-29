package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.service.PlayService;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.TurnTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class PlayNextTurnAndCardMovesForHuman implements Action<PlayNextTurnAndCardMovesForHuman.Dto, EventOutput.Result> {

    @Autowired
    PlayService playService;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // POST - turntrigger HIGER|LOWER|PASS for player in highlow game
        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            setErrorMessageBadRequestError(actionDto,"Game.type", String.valueOf(actionDto.getQasinoGame().getType()));
            return EventOutput.Result.FAILURE;
        }
        // prepare local data
        Turn activeTurn = actionDto.getActiveTurn();
        Player activePlayer = actionDto.getTurnPlayer();
        Move activeMove = null;

        // Update Turn + cardMoves can be DEAL, HIGHER, LOWER, PASS, STOP for Human
        switch (actionDto.getSuppliedTurnTrigger()) {
            case HIGHER -> {
                activeMove = Move.HIGHER;
                activeTurn = playService.dealCardToPlayer(
                        actionDto.getQasinoGame(),
                        activeTurn,
                        activePlayer,
                        activeMove,
                        Face.UP,
                        1);
            }
            case LOWER -> {
                activeMove = Move.LOWER;
                activeTurn = playService.dealCardToPlayer(
                        actionDto.getQasinoGame(),
                        activeTurn,
                        activePlayer,
                        activeMove,
                        Face.UP,
                        1);
            }
            case PASS -> {
                activeMove = Move.DEAL;
                // TODO only one round for now do not start with first player again
                if (actionDto.getNextPlayer() == null) {
                    actionDto.setSuppliedTurnTrigger(TurnTrigger.END_GAME);
                    return EventOutput.Result.SUCCESS;
                }
                // update round +1 start with turn 0
                activeTurn.setCurrentRoundNumber(activeTurn.getCurrentRoundNumber()+1);
                activeTurn.setCurrentRoundNumber(1);
                activeTurn = playService.dealCardToPlayer(
                        actionDto.getQasinoGame(),
                        activeTurn,
                        actionDto.getNextPlayer(),
                        activeMove,
                        Face.UP,
                        1);
            }
            default -> {
                // ony STOP left TODO implement some logic here
                actionDto.setSuppliedTurnTrigger(TurnTrigger.END_GAME);
                return EventOutput.Result.SUCCESS;
            }
        }
        if (actionDto.getActiveTurn() == null ) {
            actionDto.setSuppliedTurnTrigger(TurnTrigger.END_GAME);
        }
        actionDto.setActiveTurn(activeTurn); // can be null
        actionDto.setAllCardMovesForTheGame(playService.getAllCardMovesForTheGame(actionDto.getQasinoGame())); // can be null

        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageConflictError(Dto actionDto, String id,
                                                String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Trigger [" + actionDto.getSuppliedTurnTrigger() + "] invalid for gameState");
    }

    private void setErrorMessageBadRequestError(Dto actionDto, String id,
                                                String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid - only highlow implemented");
    }

    public interface Dto {

        // @formatter:off
        // Getters
        Game getQasinoGame();
        SectionTable getTable();
        Turn getActiveTurn();
        Player getTurnPlayer();
        Player getNextPlayer();
        TurnTrigger getSuppliedTurnTrigger();
        void setSuppliedTurnTrigger(TurnTrigger turnTrigger);
        // Setters
        void setQasinoGame(Game game);
        void setActiveTurn(Turn turn);
        void setAllCardMovesForTheGame(List<CardMove> cardMoves);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
