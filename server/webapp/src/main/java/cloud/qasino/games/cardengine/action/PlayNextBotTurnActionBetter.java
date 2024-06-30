package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.action.PlayNextBotTurnAction;
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
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import cloud.qasino.games.pattern.strategy.NextMoveCalculator;
import cloud.qasino.games.response.view.SectionTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PlayNextBotTurnActionBetter implements Action<PlayNextBotTurnActionBetter.Dto, EventOutput.Result> {

    @Autowired
    TurnAndCardMoveService turnAndCardMoveService;
    @Autowired
    CardMoveRepository cardMoveRepository;
    @Autowired
    TurnRepository turnRepository;

    @Override
    public EventOutput.Result perform(PlayNextBotTurnActionBetter.Dto actionDto) {

        // Consecutive move in HIGHLOW is a given move from location STOCK to location HAND with face UP
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        // POST - turnEvent NEXT for player in highlow game
        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            setBadRequestErrorMessage(actionDto, "Game type", String.valueOf(actionDto.getQasinoGame().getType()));
            return EventOutput.Result.FAILURE;
        }
        // prepare local data
        Game activeGame = actionDto.getQasinoGame();
        Turn activeTurn = actionDto.getActiveTurn();
        Player activePlayer = actionDto.getTurnPlayer();
        Move activeMove = Move.PASS;

        // new part is not used yet
        Move strategyMove = NextMoveCalculator.next(activeGame, activePlayer, activeTurn);
        log.info("PlayNextBotTurnAction StrategyMove {}", strategyMove.getLabel());

//        isTurnEqualToTurnsToWin(actionDto, activeGame, activeTurn);
//        if (actionDto.getSuppliedTurnEvent() == TurnEvent.END_GAME) {
//            return EventOutput.Result.SUCCESS;
//        }

        List<CardMove> cardMoves = cardMoveRepository.findByPlayerIdOrderBySequenceAsc(activePlayer.getPlayerId());
        // TODO calculate moves since DEAL
        // TODO get the last card value

        // Update Turn + cardMoves
        // - DUMB = 2 guess moves than pass
        // - SMART = two smart moves than pass
        // - AVERAGE = one smart and one guess move than pass

        int turn = activeTurn.getCurrentMoveNumber();
        switch (actionDto.getTurnPlayer().getAiLevel()) {
            case DUMB -> {
                if (turn >= 3) {
                    activeMove = Move.PASS;
                } else {
                    if (turnAndCardMoveService.getValueLastCardMove(cardMoves) < 6) {
                        activeMove = Move.LOWER;
                    } else {
                        activeMove = Move.HIGHER;
                    }
                }
            }

            case AVERAGE -> {
                if (turn >= 3) {
                    activeMove = Move.PASS;
                } else if (turn == 2) {
                    boolean lower = Math.random() < 0.5;
                    if (lower) {
                        activeMove = Move.LOWER;
                    } else {
                        activeMove = Move.HIGHER;
                    }
                } else {
                    // smart move
                    if (turnAndCardMoveService.getValueLastCardMove(cardMoves) < 6) {
                        activeMove = Move.HIGHER;
                    } else {
                        activeMove = Move.LOWER;
                    }
                }
            }
            case SMART -> {
                if (turn >= 3) {
                    activeMove = Move.PASS;
                } else {
                    // smart move
                    if (turnAndCardMoveService.getValueLastCardMove(cardMoves) < 6) {
                        activeMove = Move.HIGHER;
                    } else {
                        activeMove = Move.LOWER;
                    }
                }
            }
            case HUMAN -> {
                setConflictErrorMessage(actionDto, "TurnEvent", actionDto.getSuppliedTurnEvent().getLabel());
            }
        }

        List<Card> cardsDealt = new ArrayList<>();
        List<CardMove> cardsMoved = new ArrayList<>();

        switch (activeMove) {
            case HIGHER -> {
                // update round +1 start with turn 0
                activeTurn.setCurrentMoveNumber(activeTurn.getCurrentMoveNumber() + 1);
                activeTurn.setCurrentSeatNumber(activePlayer.getSeat());
                turnRepository.save(activeTurn);

                if (turnAndCardMoveService.getValueLastCardMove(cardMoves) < 6) {
                    activeMove = Move.HIGHER;
                } else {
                    activeMove = Move.HIGHER;
                }

                cardsDealt = turnAndCardMoveService.dealNCardsFromStockToActivePlayerForGame(
                        actionDto.getQasinoGame(),
                        face,
                        fromLocation,
                        toLocation,
                        howMany);
                cardsMoved = turnAndCardMoveService.storeCardMovesForTurn(
                        activeTurn,
                        cardsDealt,
                        activeMove,
                        toLocation);
            }
            case LOWER -> {
                activeTurn.setCurrentMoveNumber(activeTurn.getCurrentMoveNumber() + 1);
                activeTurn.setCurrentSeatNumber(activePlayer.getSeat());
                turnRepository.save(activeTurn);

                cardsDealt = turnAndCardMoveService.dealNCardsFromStockToActivePlayerForGame(
                        actionDto.getQasinoGame(),
                        face,
                        fromLocation,
                        toLocation,
                        howMany);
                cardsMoved = turnAndCardMoveService.storeCardMovesForTurn(
                        activeTurn,
                        cardsDealt,
                        activeMove,
                        toLocation);
            }
            case PASS -> {
                isRoundEqualToRoundsToWin(actionDto, activeGame, activeTurn);
                if (actionDto.getSuppliedTurnEvent() == TurnEvent.END_GAME) {
                    return EventOutput.Result.SUCCESS;
                }
                activeMove = Move.DEAL;
                // TODO only one round for now do not start with first player again
                if (actionDto.getNextPlayer() == null) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                    return EventOutput.Result.SUCCESS;
                }
                // update round +1 start with turn 0
                activeTurn.setCurrentRoundNumber(activeTurn.getCurrentRoundNumber() + 1);
                activeTurn.setCurrentSeatNumber(activePlayer.getSeat());
                activeTurn.setCurrentMoveNumber(1);
                turnRepository.save(activeTurn);

                cardsDealt = turnAndCardMoveService.dealNCardsFromStockToActivePlayerForGame(
                        actionDto.getQasinoGame(),
                        face,
                        fromLocation,
                        toLocation,
                        howMany);
                cardsMoved = turnAndCardMoveService.storeCardMovesForTurn(
                        activeTurn,
                        cardsDealt,
                        activeMove,
                        toLocation);
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
        actionDto.setAllCardMovesForTheGame(turnAndCardMoveService.getCardMovesForGame(actionDto.getQasinoGame())); // can be null

        log.info("PlayNextBotTurnAction activeMove {}", activeMove.getLabel());

        return cardsDealt.isEmpty() ? EventOutput.Result.FAILURE : EventOutput.Result.SUCCESS;
    }

    private static void isRoundEqualToRoundsToWin(Dto actionDto, Game activeGame, Turn activeTurn) {
        switch (Style.fromLabelWithDefault(activeGame.getStyle()).getRoundsToWin()) {
            case ONE_ROUND -> {
                if (activeTurn.getCurrentRoundNumber() == 1) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
            case TWO_ROUNDS -> {
                if (activeTurn.getCurrentRoundNumber() == 2) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
            case THREE_ROUNDS -> {
                if (activeTurn.getCurrentRoundNumber() == 3) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
        }
    }

    private static void isTurnEqualToTurnsToWin(PlayNextBotTurnAction.Dto actionDto, Game activeGame, Turn activeTurn) {
        switch (Style.fromLabelWithDefault(activeGame.getStyle()).getTurnsToWin()) {
            case ONE_WINS -> {
                if (activeTurn.getCurrentMoveNumber() == 1) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
            case TWO_IN_A_ROW_WINS -> {
                if (activeTurn.getCurrentMoveNumber() == 2) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
            case THREE_IN_A_ROW_WINS -> {
                if (activeTurn.getCurrentMoveNumber() == 3) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                }
            }
        }
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
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        // Getters
        Game getQasinoGame();
        SectionTable getTable();
        Turn getActiveTurn();
        Player getTurnPlayer();
        Player getNextPlayer();

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
