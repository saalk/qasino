package cloud.qasino.games.pattern.stream;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.pattern.comparator.ComparatorUtil;

import java.util.List;
import java.util.Optional;

public class StreamUtil {

    // filter() and sorted() - filter then sort a list asc
    // https://docs.oracle.com/javase%2F8%2Fdocs%2Fapi%2F%2F/java/util/stream/Stream.html#sorted--
    public static List<Card> sortCardsOnSequenceWithStream(List<Card> unsortedCardList, Location location) {
        if (location != null) {
            return unsortedCardList.
                    stream().
                    filter(stock -> stock.getLocation() == location).
                    sorted(ComparatorUtil.cardSequenceComparator()).
                    toList();
        } else {
            return unsortedCardList.
                    stream().
                    sorted(ComparatorUtil.cardSequenceComparator()).
                    toList();
        }
    }

    // mapToInt() then sum() on a value - count an element in a list
    // https://docs.oracle.com/javase%2F8%2Fdocs%2Fapi%2F%2F/java/util/stream/Stream.html#mapToInt-java.util.function.ToIntFunction-
    public static int countCardValuesOnRankAndSuit(List<Card> unsortedCardList) {
        return unsortedCardList.stream()
                .mapToInt(value -> PlayingCard.calculateValueWithDefaultHighlow(value.getRankSuit(), Type.HIGHLOW))
                .sum();
    }

    // filter() then findFirst() - find the first after filtering
    // https://docs.oracle.com/javase%2F8%2Fdocs%2Fapi%2F%2F/java/util/stream/Stream.html#sorted--
    public static Optional<Card> findLastCardInSortedList(List<Card> sortedCardList) {
        return sortedCardList.
                stream().
                sorted(ComparatorUtil.cardSequenceComparator().reversed()).
                findFirst(); // can be replaced with max() ?????
    }
}
