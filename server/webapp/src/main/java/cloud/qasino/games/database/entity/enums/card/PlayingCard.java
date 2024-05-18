package cloud.qasino.games.database.entity.enums.card;

import cloud.qasino.games.database.entity.enums.card.playingcard.Rank;
import cloud.qasino.games.database.entity.enums.card.playingcard.Suit;
import cloud.qasino.games.database.entity.enums.game.Type;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class PlayingCard {

    public static final List<PlayingCard> normalCardDeck = new ArrayList<>();
    protected static final PlayingCard joker = new PlayingCard(Rank.JOKER, Suit.JOKERS);

    static {
        for (Suit suit : Suit.values()) {
            if (suit != Suit.JOKERS) {
                for (Rank rank : Rank.values()) {
                    if (rank != Rank.JOKER) {
                        normalCardDeck.add(new PlayingCard(rank, suit));
                    }
                }
            }
        }
    }

    // 13 progressing ranks 2 to 10, jack, queen, king, ace.
    private String rankAndSuit;
    private Rank rank;
    private Suit suit;
    private int value;
    private String thumbnailPath;

    public PlayingCard() {
    }

    public PlayingCard(Rank rank, Suit suit) {
        this();
        if (rank == null || suit == null)
            throw new NullPointerException(rank + ", " + suit);
        this.rank = rank;
        this.suit = suit;

        final StringBuilder builder = new StringBuilder();
        this.rankAndSuit = builder.append(rank.getLabel()).append(suit.getLabel()).toString();
        this.value = calculateValueWithDefaultHighlowFromRank(rank, null);
        // todo: set thumbnailPath
    }

    public static List<PlayingCard> newDeck(int addJokers) {
        List<PlayingCard> newDeck = new ArrayList<>(); // static so init all the time
        for (int i = 0; i < addJokers; i++) {
            newDeck.add(joker);
        }
        newDeck.addAll(normalCardDeck);
        return newDeck;
    }

    public static boolean isValidCardId(String rankAndSuit) {
        if (rankAndSuit == null
                || rankAndSuit.isEmpty())
            return false;

        for (PlayingCard playingCard : normalCardDeck) {
            if (playingCard.rankAndSuit.equals(rankAndSuit)) return true;
        }
        return false;
    }

    public static PlayingCard getPlayingCardFromCardId(String card) {

        if (card == null
                || card.isEmpty()
                || !isValidCardId(card))
            return null;
        for (PlayingCard playingCard : normalCardDeck) {
            if (playingCard.rankAndSuit.equals(card)) {
                return playingCard;
            }
        }
        return null;
    }

    public static boolean isJoker(String cardId) {
        String jokerCard = cardId;
        return jokerCard.equals("RJ");
    }

    public static int calculateValueWithDefaultHighlow(String cardId, Type type) {
        PlayingCard playingCard = getPlayingCardFromCardId(cardId);
        return calculateValueWithDefaultHighlowFromRank(playingCard.rank, type);
    }

    public static int calculateValueWithDefaultHighlowFromRank(Rank rank, Type type) {
        Type localType = type == null ? Type.HIGHLOW : type;
        switch (rank) {
            case JOKER:
                if (localType.equals(Type.HIGHLOW)) {
                    return 0;
                } else {
                    return 0;
                }
            case ACE:
                return 1;
            case KING:
                return 13;
            case QUEEN:
                return 12;
            case JACK:
                return 11;
            default:
                // 2 until 10
                return Integer.parseInt(rank.getLabel());
        }
    }

}
