package cloud.qasino.card.domain.qasino;

import cloud.qasino.card.entity.enums.playingcard.Card;
import cloud.qasino.card.entity.enums.playingcard.card.Rank;
import cloud.qasino.card.entity.enums.playingcard.card.Suit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void callNewDeckShouldBecomeCorrectNumberOfCards() {

        List<Card> testDeckNoJokers = Card.newDeck(0);
        List<Card> testDeckOneJokers = Card.newDeck(1);
        List<Card> testDeckTwoJokers = Card.newDeck(2);

        // assert statements
        assertEquals(52, testDeckNoJokers.size());
        assertEquals(53, testDeckOneJokers.size());
        assertEquals(54, testDeckTwoJokers.size());
        assertEquals(52, Card.prototypeDeck.size());

    }

    @Test
    void callIsValidCardShouldGiveCorrectBoolean() {

        boolean valid = Card.isValidCardId("AS");
        boolean inValid = Card.isValidCardId("XX");

        // assert statements
        assertTrue(valid);
        assertFalse(inValid);

    }

    @Test
    void callSetCardFromCardIdShouldSetCorrectCard() {

        Card valid = new Card();
        boolean validCard = valid.setCardFromCardId("AS");

        Card inValid = new Card();
        boolean inValidCard = valid.setCardFromCardId("XX");

        // assert statements
        assertTrue(validCard);
        assertEquals("AS",valid.getCardId());
        assertEquals(Rank.ACE,valid.getRank());
        assertEquals(Suit.SPADES,valid.getSuit());
        assertEquals(1, valid.getValue());

        assertTrue(!inValidCard);

    }

}