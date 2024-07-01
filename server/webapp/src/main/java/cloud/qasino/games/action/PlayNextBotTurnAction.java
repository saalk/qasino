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
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.service.GameServiceOld;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.exception.MyNPException;
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
public class PlayNextBotTurnAction implements Action<PlayNextBotTurnAction.Dto, EventOutput.Result> {

    @Autowired
    TurnAndCardMoveService turnAndCardMoveService;
    @Autowired
    CardMoveRepository cardMoveRepository;
    @Autowired
    TurnRepository turnRepository;
    @Autowired
    private GameServiceOld gameServiceOld;


    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            throw new MyNPException("PlayNextBotTurnAction", "error [" + actionDto.getQasinoGame().getType() + "]");
        }

        // Next move in HIGHLOW = a given move from location STOCK to location HAND with face UP
        Move nextMove = null;
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        // Local fields
        Game game = actionDto.getQasinoGame();
        Player nextPlayer = gameServiceOld.findNextPlayerForGame(game);
        int totalSeats = game.getPlayers().size();
        Turn currentTurn = game.getTurn();
        int currentSeat = currentTurn.getCurrentSeatNumber();
        int currentRound = currentTurn.getCurrentRoundNumber();
        Player activePlayer = currentTurn.getActivePlayer();

        nextMove = NextMoveCalculator.next(game, activePlayer, currentTurn);
        log.info("PlayNextBotTurnAction StrategyMove {}", nextMove);

        // TODO DetermineNextRoundOrEndGame -> move to separate action
        if (nextMove == Move.PASS) {
            if (totalSeats == currentSeat) {
                if (isRoundEqualToRoundsToWin(Style.fromLabelWithDefault(game.getStyle()), currentRound)) {
                    actionDto.setSuppliedTurnEvent(TurnEvent.END_GAME);
                    return EventOutput.Result.SUCCESS;
                }
                currentTurn.setCurrentRoundNumber(currentRound + 1);
            }
        }

        // Update TURN - could be new to new Player
        updateCurrentTurn(nextMove, currentTurn, nextPlayer);
        Turn newTurn = turnRepository.save(currentTurn);
        game.setTurn(newTurn);

        // Deal CARDs (and update CARDMOVE)
        turnAndCardMoveService.dealNCardsFromStockToActivePlayerForGame(
                game,
                nextMove,
                fromLocation,
                toLocation,
                face,
                howMany);
        return EventOutput.Result.SUCCESS;

    }

    // @formatter:off
    private void updateCurrentTurn(Move move, Turn activeTurn, Player nextPlayer) {
        switch (move) {
            case HIGHER, LOWER -> {
                activeTurn.setCurrentSeatNumber(activeTurn.getActivePlayer().getSeat());
                activeTurn.setCurrentMoveNumber(activeTurn.getCurrentMoveNumber() + 1);
            }
            case DEAL -> {
                activeTurn.setCurrentSeatNumber(nextPlayer.getSeat());
                activeTurn.setCurrentMoveNumber(1);
                activeTurn.setActivePlayer(nextPlayer);
            }
            default -> throw new MyNPException("PlayNext", "updateActiveTurn [" + move + "]");
        }
    }
    private static boolean isRoundEqualToRoundsToWin(Style style, int round) {
        switch (style.getRoundsToWin()) {
            case ONE_ROUND -> {
                if (round == 2) {
                    return true;
                }
            }
            case TWO_ROUNDS -> {
                if (round == 3) {
                    return true;
                }
            }
            case THREE_ROUNDS -> {
                if (round == 4) {
                    return true;
                }
            }
            default -> throw new MyNPException("PlayNext", "isRoundEqualToRoundsToWin [" + style + "]");
        }
        return false;
    }
    private static boolean isMoveEqualToTurnsToWin(Style style, int move) {
        switch (style.getTurnsToWin()) {
            case ONE_WINS -> {
                if (move == 2) {
                    return true;
                }
            }
            case TWO_IN_A_ROW_WINS -> {
                if (move == 3) {
                    return true;
                }
            }
            case THREE_IN_A_ROW_WINS -> {
                if (move == 4) {
                    return true;
                }
            }
            default -> throw new MyNPException("PlayNext", "isMoveEqualToTurnsToWin [" + style + "]");
        }
        return false;
    }

    public interface Dto {
        Game getQasinoGame();
        void setSuppliedTurnEvent(TurnEvent turnEvent);
    }
}
