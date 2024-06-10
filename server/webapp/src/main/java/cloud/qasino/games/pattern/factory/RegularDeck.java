package cloud.qasino.games.pattern.factory;

import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import lombok.Data;

import java.util.Collections;
import java.util.List;

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
