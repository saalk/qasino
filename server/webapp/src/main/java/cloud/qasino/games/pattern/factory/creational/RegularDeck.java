package cloud.qasino.games.pattern.factory.creational;

import cloud.qasino.games.pattern.factory.Deck;

import java.util.Collections;

import static cloud.qasino.games.database.entity.enums.card.PlayingCard.createDeckWithXJokers;

public class RegularDeck extends Deck {

    @Override
    public void create(int jokers) {
        playingCards = createDeckWithXJokers(jokers);
    }
    @Override
    public void shuffle() {
        if (playingCards == null) return;
        Collections.shuffle(playingCards);
    }

}
