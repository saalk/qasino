package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.service.PlayService;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PlayNextBotTurnAction implements Action<PlayNextBotTurnAction.Dto, EventOutput.Result> {

    @Autowired
    PlayService playService;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // POST - turnEvent NEXT for player in highlow game
        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            setBadRequestErrorMessage(actionDto, "Game type", String.valueOf(actionDto.getQasinoGame().getType()));
            return EventOutput.Result.FAILURE;
        }
        // prepare local data
        Turn activeTurn = actionDto.getActiveTurn();
        Player activePlayer = actionDto.getTurnPlayer();
        Move activeMove = Move.PASS;

        List<CardMove> cardMoves = playService.getCardMovesForPlayer(activePlayer);
        // TODO calculate moves since DEAL
        // TODO get the last card value

        // Update Turn + cardMoves
        // - DUMB = 2 guess moves than pass
        // - SMART = two smart moves than pass
        // - AVERAGE = one smart and one guess move than pass

        int turn = activeTurn.getCurrentTurnNumber();
        switch (actionDto.getTurnPlayer().getAiLevel()) {
            case DUMB -> {
                if (turn >= 3) {
                    activeMove = Move.PASS;
                } else {
                    boolean lower = Math.random() < 0.5;
                    if (lower) {
                        activeMove = Move.LOWER;
                    } else {
                        activeMove = Move.HIGHER;
                    }
                }
            }
            case AVERAGE -> {
                if (turn >= 3) {
                    activeMove = Move.PASS;
                } else if(turn == 2) {
                    boolean lower = Math.random() < 0.5;
                    if (lower) {
                        activeMove = Move.LOWER;
                    } else {
                        activeMove = Move.HIGHER;
                    }
                } else {
                    // smart move
                    if (playService.getValueLastCardMove(cardMoves) < 6) {
                        activeMove = Move.HIGHER;
                    } else {
                        activeMove = Move.HIGHER;
                    }
                }
            }
            case SMART -> {
                if (turn >= 3) {
                    activeMove = Move.PASS;
                } else {
                    // smart move
                    if (playService.getValueLastCardMove(cardMoves) < 6) {
                        activeMove = Move.HIGHER;
                    } else {
                        activeMove = Move.HIGHER;
                    }
                }
            }
            case HUMAN -> {
                setConflictErrorMessage(actionDto, "TurnEvent", actionDto.getSuppliedTurnEvent().getLabel());
            }
        }

        switch (activeMove) {
            case HIGHER -> {
                // update round +1 start with turn 0
                activeTurn.setCurrentTurnNumber(activeTurn.getCurrentTurnNumber() + 1);
                activeTurn = playService.dealCardToPlayer(
                        actionDto.getQasinoGame(),
                        activeTurn,
                        activePlayer,
                        activeMove,
                        Face.UP,
                        1);
            }
            case LOWER -> {
                activeTurn.setCurrentTurnNumber(activeTurn.getCurrentTurnNumber() + 1);
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
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                    return EventOutput.Result.SUCCESS;
                }
                // update round +1 start with turn 0
                activeTurn.setCurrentRoundNumber(activeTurn.getCurrentRoundNumber() + 1);
                activeTurn.setCurrentTurnNumber(1);
                activeTurn = playService.dealCardToPlayer(
                        actionDto.getQasinoGame(),
                        activeTurn,
                        actionDto.getNextPlayer(),
                        activeMove,
                        Face.UP,
                        1);
                actionDto.setTurnPlayer(actionDto.getNextPlayer());
                actionDto.setActiveTurn(activeTurn);
                actionDto.setSuppliedTurnPlayerId(actionDto.getTurnPlayer().getPlayerId());
                //  TODO rely on load again to get new next player

            }
            default -> {
                // ony STOP left TODO implement some logic here
                actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                return EventOutput.Result.SUCCESS;
            }
        }
        if (actionDto.getActiveTurn() == null) {
            actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
        }
        actionDto.setActiveTurn(activeTurn); // can be null
        actionDto.setAllCardMovesForTheGame(playService.getCardMovesForGame(actionDto.getQasinoGame())); // can be null

        return EventOutput.Result.SUCCESS;
    }

    private void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("Trigger [" + actionDto.getSuppliedTurnEvent() + "] invalid for bot player");
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("Action [" + id + "] invalid - only highlow implemented");
    }

    public interface Dto {

        // @formatter:off
        // Getters
        Game getQasinoGame();
        SectionTable getTable();
        Turn getActiveTurn();
        Player getTurnPlayer();
        Player getNextPlayer();
        TurnEvent getSuppliedTurnEvent();

        // Setters
        void setTurnPlayer(Player turnPlayer);
        void setNextPlayer(Player nextPlayer);
        void setSuppliedTurnPlayerId( long turnPlayer);
        void setSuppliedTurnEvent(TurnEvent turnEvent);
        void setQasinoGame(Game game);
        void setActiveTurn(Turn turn);
        void setAllCardMovesForTheGame(List<CardMove> cardMoves);

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
