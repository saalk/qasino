package cloud.qasino.games.pattern.factory;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;

import java.util.ArrayList;
import java.util.List;

import static cloud.qasino.games.database.entity.enums.card.PlayingCard.normalCardDeckNoJoker;

public class DeckFactory {

    public List<PlayingCard> createDeck(Game preparedGame, int jokers) {

        // default is nrrn22 => pos 3 = DeckConfiguration.RANDOM_SUIT_NO_JOKER
        DeckConfiguration configuration = Style.fromLabelWithDefault(preparedGame.getStyle()).getDeckConfiguration();
        switch (configuration) {

            case ALL_THREE_JOKERS, ALL_TWO_JOKERS, ALL_ONE_JOKER, ALL_NO_JOKER -> {
                return new RegularDeck(jokers).deck;
            }
            case RANDOM_SUIT_THREE_JOKERS, RANDOM_SUIT_TWO_JOKERS, RANDOM_SUIT_ONE_JOKER, RANDOM_SUIT_NO_JOKER -> {
                return new RandomSuitOnlyDeck(jokers).deck;
            }

        }
        return new RegularDeck(jokers).deck;
    }

    List<PlayingCard> createDeckWithXJokers(int addJokers) {
        List<PlayingCard> newDeck = new ArrayList<>(); // static so init all the time
        for (int i = 0; i < addJokers; i++) {
//            newDeck.add(joker);
        }
        newDeck.addAll(normalCardDeckNoJoker);
        return newDeck;
    }

}
