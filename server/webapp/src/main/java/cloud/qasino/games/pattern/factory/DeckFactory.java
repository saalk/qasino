package cloud.qasino.games.pattern.factory;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.factory.creational.RandomSuitOnlyDeck;
import cloud.qasino.games.pattern.factory.creational.RegularDeck;

public class DeckFactory {

    public static Deck createShuffledDeck(Game preparedGame, int inputJokers) {

        // default is nrrn22 => pos 3 = DeckConfiguration.RANDOM_SUIT_NO_JOKER
        DeckConfiguration configuration = Style.fromLabelWithDefault(preparedGame.getStyle()).getDeckConfiguration();
        switch (configuration) {

            case ALL_THREE_JOKERS -> {
                RegularDeck deck = new RegularDeck();
                deck.create(3);
                deck.shuffle();
                return deck;
            }
            case ALL_TWO_JOKERS -> {
                RegularDeck deck = new RegularDeck();
                deck.create(2);
                deck.shuffle();
                return deck;
            }
            case ALL_ONE_JOKER -> {
                RegularDeck deck = new RegularDeck();
                deck.create(1);
                deck.shuffle();
                return deck;
            }
            case ALL_NO_JOKER -> {
                RegularDeck deck = new RegularDeck();
                deck.create(0);
                deck.shuffle();
                return deck;
            }
            case RANDOM_SUIT_THREE_JOKERS -> {
                RandomSuitOnlyDeck deck = new RandomSuitOnlyDeck();
                deck.create(3);
                deck.shuffle();
                return deck;
            }
            case RANDOM_SUIT_TWO_JOKERS -> {
                RandomSuitOnlyDeck deck = new RandomSuitOnlyDeck();
                deck.create(2);
                deck.shuffle();
                return deck;
            }
            case RANDOM_SUIT_ONE_JOKER -> {
                RandomSuitOnlyDeck deck = new RandomSuitOnlyDeck();
                deck.create(1);
                deck.shuffle();
                return deck;
            }
            case RANDOM_SUIT_NO_JOKER -> {
                RandomSuitOnlyDeck deck = new RandomSuitOnlyDeck();
                deck.create(0);
                deck.shuffle();
                return deck;
            }

        }
        throw new MyNPException("preparedGame","preparedGame [" + preparedGame + "]");
    }
}
