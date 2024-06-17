package cloud.qasino.games.pattern.strategy.algorithm;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.pattern.math.MathUtil;
import cloud.qasino.games.pattern.strategy.MoveMaker;
import cloud.qasino.games.pattern.strategy.NextMoveCalculator;
import cloud.qasino.games.pattern.stream.StreamUtil;

import java.util.List;
import java.util.Optional;

public class SmartMove extends NextMoveCalculator implements MoveMaker {
    @Override
    public Move calculate(Game game) {

        List<Card> sortedCardsInStock = StreamUtil.sortCardsOnSequenceWithStream(game.getCards(), Location.STOCK);
        Optional<Card> lastCardPlayed = StreamUtil.findFirstCardInSortedListForLocation(sortedCardsInStock, Location.STOCK);

        int totalValueCardsInStock = StreamUtil.countCardValuesOnRankAndSuit(sortedCardsInStock);
        int totalValueDeck = StreamUtil.countCardValuesOnRankAndSuit(game.getCards());
        double averageCardsLeftInStockValue = MathUtil.roundToNDigits(totalValueCardsInStock / (double) sortedCardsInStock.size(), 1);
        double averageValueDeck = MathUtil.roundToNDigits(totalValueDeck / (double) game.getCards().size(), 1);

        return Move.HIGHER;

    }
}
