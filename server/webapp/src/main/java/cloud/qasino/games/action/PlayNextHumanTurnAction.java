package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.response.view.SectionTable;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PlayNextHumanTurnAction implements Action<PlayNextHumanTurnAction.Dto, EventOutput.Result> {

    @Autowired
    TurnAndCardMoveService turnAndCardMoveService;
    @Autowired
    private TurnRepository turnRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // Consecutive move in HIGHLOW is a given move from location STOCK to location HAND with face UP
        Move activeMove = null;
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        actionDto.setErrorKey("TurnEvent");
        actionDto.setErrorValue(actionDto.getSuppliedTurnEvent().getLabel());

        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            setBadRequestErrorMessage(actionDto, "Game type", String.valueOf(actionDto.getQasinoGame().getType()));
            return EventOutput.Result.FAILURE;
        }

        List<Card> cardsDealt = new ArrayList<>();
        List<CardMove> cardsMoved = new ArrayList<>();

        switch (actionDto.getSuppliedTurnEvent()) {
            case HIGHER -> {
                activeMove = Move.HIGHER;
                // update round +1 start with turn 0
                actionDto.getActiveTurn().setCurrentMoveNumber(actionDto.getActiveTurn().getCurrentMoveNumber() + 1);
                actionDto.getActiveTurn().setCurrentSeatNumber(actionDto.getTurnPlayer().getSeat());
                actionDto.setActiveTurn(turnRepository.save(actionDto.getActiveTurn()));

                cardsDealt = turnAndCardMoveService.dealNCardsFromStockToActivePlayerForGame(
                        actionDto.getQasinoGame(),
                        face,
                        fromLocation,
                        toLocation,
                        howMany);
                cardsMoved = turnAndCardMoveService.storeCardMovesForTurn(
                        actionDto.getActiveTurn(),
                        cardsDealt,
                        activeMove,
                        toLocation);
            }
            case LOWER -> {
                activeMove = Move.LOWER;
                actionDto.getActiveTurn().setCurrentMoveNumber(actionDto.getActiveTurn().getCurrentMoveNumber() + 1);
                actionDto.getActiveTurn().setCurrentSeatNumber(actionDto.getTurnPlayer().getSeat());
                actionDto.setActiveTurn(turnRepository.save(actionDto.getActiveTurn()));

                cardsDealt = turnAndCardMoveService.dealNCardsFromStockToActivePlayerForGame(
                        actionDto.getQasinoGame(),
                        face,
                        fromLocation,
                        toLocation,
                        howMany);
                cardsMoved = turnAndCardMoveService.storeCardMovesForTurn(
                        actionDto.getActiveTurn(),
                        cardsDealt,
                        activeMove,
                        toLocation);

            }
            case PASS -> {
                // evaluate rounds to win - usually 1
                isRoundEqualToRoundsToWin(actionDto, actionDto.getQasinoGame(), actionDto.getActiveTurn());
                if (actionDto.getNextPlayer() == null) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
                if (actionDto.getSuppliedTurnEvent() == TurnEvent.END_GAME) {
                    return EventOutput.Result.SUCCESS;
                }
                // update round +1 start with turn 1 and move to next player
                activeMove = Move.DEAL;
                actionDto.getActiveTurn().setCurrentRoundNumber(actionDto.getActiveTurn().getCurrentRoundNumber() + 1);
                actionDto.getActiveTurn().setCurrentSeatNumber(actionDto.getTurnPlayer().getSeat());
                actionDto.getActiveTurn().setCurrentMoveNumber(1);
                actionDto.getActiveTurn().setActivePlayer(actionDto.getNextPlayer());
                actionDto.setActiveTurn(turnRepository.save(actionDto.getActiveTurn()));

                cardsDealt = turnAndCardMoveService.dealNCardsFromStockToActivePlayerForGame(
                        actionDto.getQasinoGame(),
                        face,
                        fromLocation,
                        toLocation,
                        howMany);
                cardsMoved = turnAndCardMoveService.storeCardMovesForTurn(
                        actionDto.getActiveTurn(),
                        cardsDealt,
                        activeMove,
                        toLocation);

                actionDto.setTurnPlayer(actionDto.getNextPlayer());
//                actionDto.setActiveTurn(actionDto.getActiveTurn());
                actionDto.setSuppliedTurnPlayerId(actionDto.getTurnPlayer().getPlayerId());
            }
            default -> {
                // ony STOP left TODO implement some logic here
                setConflictErrorMessage(actionDto);
//                actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                return EventOutput.Result.FAILURE;
            }
        }
        if (actionDto.getActiveTurn() == null) {
            // no card left so end the game
            actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
        }
        actionDto.setActiveTurn(actionDto.getActiveTurn()); // can be null ???
        actionDto.setAllCardMovesForTheGame(turnAndCardMoveService.getCardMovesForGame(actionDto.getQasinoGame())); // can be null

        return EventOutput.Result.SUCCESS;
    }

    private static void isRoundEqualToRoundsToWin(Dto actionDto, Game activeGame, Turn activeTurn) {
        switch (Style.fromLabelWithDefault(activeGame.getStyle()).getRoundsToWin()){
            case ONE_ROUND -> {
                if (activeTurn.getCurrentRoundNumber() == 2) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
            case TWO_ROUNDS -> {
                if (activeTurn.getCurrentRoundNumber() == 3) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
            case THREE_ROUNDS -> {
                if (activeTurn.getCurrentRoundNumber() == 4) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
        }
    }
    private static void isTurnEqualToTurnsToWin(Dto actionDto, Game activeGame, Turn activeTurn) {
        switch (Style.fromLabelWithDefault(activeGame.getStyle()).getTurnsToWin()){
            case ONE_WINS -> {
                if (activeTurn.getCurrentMoveNumber() == 2) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
            case TWO_IN_A_ROW_WINS -> {
                if (activeTurn.getCurrentMoveNumber() == 3) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
            case THREE_IN_A_ROW_WINS -> {
                if (activeTurn.getCurrentMoveNumber() == 4) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
        }
    }

    private void setConflictErrorMessage(Dto actionDto) {
//        actionDto.setErrorKey(id);
//        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("Trigger [" + actionDto.getSuppliedTurnEvent() + "] invalid for gameState");
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("Action [" + id + "] invalid - only highlow implemented");
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        // Getters
        Game getQasinoGame();
        SectionTable getTable();
        Turn getActiveTurn();
        Player getTurnPlayer();
        Player getNextPlayer();
        List<Player> getQasinoGamePlayers();
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
