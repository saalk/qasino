package cloud.qasino.games.pattern.stream;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.comparator.ComparatorUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamUtil {

    // for sorted() use COMPARATOR UTIL
    // https://docs.oracle.com/javase%2F8%2Fdocs%2Fapi%2F%2F/java/util/stream/Stream.html#sorted--
    // mapToInt() then sum() on a value
    // https://docs.oracle.com/javase%2F8%2Fdocs%2Fapi%2F%2F/java/util/stream/Stream.html#mapToInt-java.util.function.ToIntFunction-
    // filter() then limit() - find the first X then collect() as limit returns another stream

    // sort
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
    public static List<CardMove> sortCardMovesOnSequenceWithStream(List<CardMove> unsortedCardMoveList, long player) {
        if (player > 0) {
            return unsortedCardMoveList.
                    stream().
                    filter(cardMove -> cardMove.getPlayerId() == player).
                    sorted(ComparatorUtil.cardMoveSequenceComparator()).
                    toList();
        } else {
            return unsortedCardMoveList.
                    stream().
                    sorted(ComparatorUtil.cardMoveSequenceComparator()).
                    toList();
        }
    }
    public static List<Player> sortPlayersOnSeatWithStream(List<Player> unsortedPlayerList) {
            return unsortedPlayerList.
                    stream().
                    sorted(ComparatorUtil.playerSeatComparator()).
                    toList();
    }

    // map and sum
    public static int countCardValuesOnRankAndSuit(List<Card> unsortedCardList) {
        return unsortedCardList.stream()
                .mapToInt(value -> PlayingCard.calculateValueWithDefaultHighlow(value.getRankSuit(), Type.HIGHLOW))
                .sum();
    }

    // find first or limit and collect
    public static Player findFirstPlayerBySeat(List<Player> players) {
        Optional<Player> player = players
                .stream()
                .filter(p -> p.getSeat() == 1)
                .findFirst();
        if (player.isEmpty()) {
            throw new MyNPException("findFirstPlayerBySeat", "players [" + Arrays.toString(players.toArray()) + "]");
        } else {
            return player.get();
        }
    }
    public static Optional<Card> findLastCardInSortedList(List<Card> sortedCardList) {
        return sortedCardList.
                stream().
                sorted(ComparatorUtil.cardSequenceComparator().reversed()).
                findFirst(); // can be replaced with max() ?????
    }
    public static List<Card> findFirstNCards(List<Card> sortedCardList, int howMany) {
        return sortedCardList.stream().limit(howMany).collect(Collectors.toList());
    }
}
