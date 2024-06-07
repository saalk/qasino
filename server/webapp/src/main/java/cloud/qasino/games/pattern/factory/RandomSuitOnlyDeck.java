package cloud.qasino.games.pattern.factory;

import cloud.qasino.games.database.entity.enums.card.PlayingCard;

import java.util.Collections;
import java.util.List;

import static cloud.qasino.games.database.entity.enums.card.PlayingCard.createDeckForRandomSuitWithXJokers;

public class RandomSuitOnlyDeck {

    List<PlayingCard> deck;

    RandomSuitOnlyDeck(int jokers) {
        deck = createDeckForRandomSuitWithXJokers(jokers);
        Collections.shuffle(deck);
    }
}
