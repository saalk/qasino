package cloud.qasino.games.pattern.strategy;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.pattern.math.MathUtil;
import cloud.qasino.games.pattern.strategy.algorithm.AverageMove;
import cloud.qasino.games.pattern.strategy.algorithm.DumbMove;
import cloud.qasino.games.pattern.strategy.algorithm.SmartMove;
import cloud.qasino.games.pattern.stream.StreamUtil;

import java.util.List;
import java.util.Optional;


public class NextMoveCalculator {

    public static Move next(Game game, Player player, Turn turn) {

        List<Card> sortedCardsInStock = StreamUtil.sortCardsOnSequenceWithStream(game.getCards(), Location.STOCK);
        Optional<Card> lastCardPlayed = StreamUtil.findFirstCardInSortedListForLocation(sortedCardsInStock, Location.STOCK);

        int totalValueCardsInStock = StreamUtil.countCardValuesOnRankAndSuit(sortedCardsInStock);
        int totalValueDeck = StreamUtil.countCardValuesOnRankAndSuit(game.getCards());
        double averageCardsLeftInStockValue = MathUtil.roundToNDigits(totalValueCardsInStock / (double) sortedCardsInStock.size(), 1);
        double averageValueDeck = MathUtil.roundToNDigits(totalValueDeck / (double) game.getCards().size(), 1);

        enum NextMove {DumbMove, AverageMove, SmartMove}
        NextMove next = NextMove.AverageMove;
        ;
        switch (player.getAiLevel()) {
            case DUMB -> {
                if (turn.getCurrentMoveNumber() < 2) {
                    next = NextMove.DumbMove;
                } else {
                    next = NextMove.AverageMove;
                }
            }
            case AVERAGE -> {
                if (turn.getCurrentMoveNumber() < 2) {
                    next = NextMove.AverageMove;
                } else {
                    next = NextMove.SmartMove;
                }
            }
            case SMART -> {
                next = NextMove.SmartMove;
            }
        }

        switch (next) {
            case DumbMove -> {
                return new AverageMove().calculate(game);
            }
            case AverageMove -> {
                return new AverageMove().calculate(game);
            }
            case SmartMove -> {
                return new AverageMove().calculate(game);
            }
        }
        return null;

    }

    ;
}