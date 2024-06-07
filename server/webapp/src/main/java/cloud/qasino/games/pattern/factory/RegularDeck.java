package cloud.qasino.games.pattern.factory;

import cloud.qasino.games.database.entity.enums.card.PlayingCard;

import java.util.Collections;
import java.util.List;

import static cloud.qasino.games.database.entity.enums.card.PlayingCard.createDeckWithXJokers;

public class RegularDeck {

    List<PlayingCard> deck;

    RegularDeck(int jokers) {
        deck = createDeckWithXJokers(jokers);
        Collections.shuffle(deck);
    }
}
