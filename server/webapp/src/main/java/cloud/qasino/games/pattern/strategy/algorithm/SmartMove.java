package cloud.qasino.games.pattern.strategy.algorithm;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.pattern.math.MathUtil;
import cloud.qasino.games.pattern.strategy.MovePredictor;
import cloud.qasino.games.pattern.strategy.NextMoveCalculator;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class SmartMove extends NextMoveCalculator implements MovePredictor {
    @Override
    public Move predictMove(Game game) {

        // fallback to random move when the game has no cards yet
        if (game == null || game.getCards() == null) {
            log.warn("SmartMove game.getCards is {}", game.getCards());
            Move move = new RandomMove().predictMove(game);
            log.warn("SmartMove fallback to randomMove {}", move);
            return move;
        }

        List<Card> sortedCardsInHand = StreamUtil.sortCardsOnSequenceWithStream(game.getCards(), Location.HAND);
        if (sortedCardsInHand.isEmpty()) {
            log.warn("SmartMove sortedCardsInHand is {}", sortedCardsInHand);
            Move move = new RandomMove().predictMove(game);
            log.warn("SmartMove fallback to randomMove {}", move);
            return move;
        }

//        Optional<Card> lastCardPlayed = StreamUtil.findLastCardInSortedList(sortedCardsInHand);
//
//        log.warn("SmartMove lastCardPlayed is {}", lastCardPlayed);
//        if (lastCardPlayed.isEmpty()) {
//            Move move = new RandomMove().predictMove(game);
//            log.warn("SmartMove fallback to randomMove {}", move);
//            return move;
//        }
        // fallback to random move when the game has no cards yet

        int totalValueCardsInStock = StreamUtil.countCardValuesOnRankAndSuit(sortedCardsInHand);
        int totalValueDeck = StreamUtil.countCardValuesOnRankAndSuit(game.getCards());
        double averageCardsLeftInStockValue = MathUtil.roundToNDigits(totalValueCardsInStock / (double) sortedCardsInHand.size(), 1);
        double averageValueDeck = MathUtil.roundToNDigits(totalValueDeck / (double) game.getCards().size(), 1);

        log.warn("SmartMove averageCardsLeftInStockValue is {}", averageCardsLeftInStockValue);
        log.warn("SmartMove averageValueDeck is {}", averageValueDeck);

        log.warn("SmartMove is {}", (averageCardsLeftInStockValue > averageValueDeck ? Move.LOWER : Move.HIGHER));

        return (averageCardsLeftInStockValue > averageValueDeck ? Move.LOWER : Move.HIGHER);
    }
}
