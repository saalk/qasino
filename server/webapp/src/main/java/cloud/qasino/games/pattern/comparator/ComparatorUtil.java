package cloud.qasino.games.pattern.comparator;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.dto.model.CardDto;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.PlayerDto;

import java.util.Comparator;

public class ComparatorUtil {

    // COMPARATOR static comparing() method
    // Since Java 8, there are several static methods added to the Comparator interface
    // that can take lambda expressions to create a Comparator object.
    // We can use its comparing() method to construct a Comparator for a certain field
    //
    // USAGE IN A STREAM sorted()
    // List<Card> sortedCardList = cardList.stream()
    //                .sorted(cardSequenceComparator)
    //                .collect(Collectors.toList());

    //

    // USAGE IN COLLECTIONS compare(a, b) method
    // class CardComparator implements java.util.Comparator<Card> {
    //    @Override
    //    public int compare(Card a, Card b) {
    //        return a.getSequence() - b.getSequence();
    //    }
    // }
    //
    // Usage Collections.sort(cardList, new CardComparator());

    // @formatter:off
    // This Comparator can compare the sequence and can be passed to a compare function
    public static Comparator<CardDto> cardSequenceComparator() {
        return Comparator.comparing(CardDto::getSequence);
    }
    public static Comparator<CardMove> cardMoveSequenceComparator() {
        return Comparator.comparing(CardMove::getSequence);
    }
    public static Comparator<Player> playerSeatComparator() {
        return Comparator.comparing(Player::getSeat);
    }
    public static Comparator<PlayerDto> playerDtoSeatComparator() {
        return Comparator.comparing(PlayerDto::getSeat);
    }
    public static Comparator<GameDto> gameAnteComparator() {
        return Comparator.comparing(GameDto::getAnte);
    }

}
