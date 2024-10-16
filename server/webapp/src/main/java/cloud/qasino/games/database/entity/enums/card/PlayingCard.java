package cloud.qasino.games.database.entity.enums.card;

import cloud.qasino.games.database.entity.enums.card.playingcard.Rank;
import cloud.qasino.games.database.entity.enums.card.playingcard.Suit;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.exception.MyNPException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.right;

@Data
@Slf4j
public class PlayingCard {

    // @formatter:off

    // Static variables and blocks go first in the class
    public static final List<PlayingCard> normalCardDeckNoJoker = new ArrayList<>();
    public static final List<PlayingCard> clubsDeckNoJoker = new ArrayList<>();
    public static final List<PlayingCard> heartsDeckNoJoker = new ArrayList<>();
    public static final List<PlayingCard> diamondsDeckNoJoker = new ArrayList<>();
    public static final List<PlayingCard> spadesDeckNoJoker = new ArrayList<>();
    public static final List<PlayingCard> normalCardDeckOneJoker = new ArrayList<>();
    static {
        for (Suit suit : Suit.values()) {
            if (suit != Suit.JOKERS) {
                for (Rank rank : Rank.values()) {
                    if (rank != Rank.JOKER) {
                        normalCardDeckNoJoker.add(new PlayingCard(rank, suit));
                        normalCardDeckOneJoker.add(new PlayingCard(rank, suit));
                        switch (suit) {
                            case CLUBS -> clubsDeckNoJoker.add(new PlayingCard(rank, suit));
                            case DIAMONDS -> diamondsDeckNoJoker.add(new PlayingCard(rank, suit));
                            case HEARTS -> heartsDeckNoJoker.add(new PlayingCard(rank, suit));
                            case SPADES -> spadesDeckNoJoker.add(new PlayingCard(rank, suit));
                        }
                    }
                }
            } else {
                normalCardDeckOneJoker.add(new PlayingCard(Rank.JOKER, suit));
            }
        }
    }
    protected static final PlayingCard joker = new PlayingCard(Rank.JOKER, Suit.JOKERS);

    // Then Instance variables and blocks in the class
    private String rankAndSuit;
    private Rank rank;
    private Suit suit;
    private int value;
    private String thumbnailPath;

    // Then Constructors in the class
    public PlayingCard(String rankAndSuit) {
        if (!isValid2LetterCardId(rankAndSuit)) throw new RuntimeException();
        this.rankAndSuit = rankAndSuit;
        this.rank = Rank.fromLabel(left(rankAndSuit, 1));
        this.suit = Suit.fromLabel(right(rankAndSuit, 1));
        this.value = calculateValueWithDefaultHighlowFromRank(rank, null);
    }
    public PlayingCard(Rank rank, Suit suit) {
        if (rank == null || suit == null)
            throw new MyNPException("PlayingCard","rank [" + rank + "] suit [" + suit + "]");
        this.rank = rank;
        this.suit = suit;
        this.rankAndSuit = rank.getLabel()+suit.getLabel();
        this.value = calculateValueWithDefaultHighlowFromRank(rank, null);
        this.thumbnailPath = "static/images/playingcard/svg/" +
                this.rank.getLabel().toLowerCase() +
                "-" +
                this.suit.getLabel().toLowerCase() +
                ".svg";
    }

    // Then Static methods - they can be called without creating an instance
    public static List<PlayingCard> createDeckWithXJokers(int addJokers) {
        List<PlayingCard> newDeck = new ArrayList<>(); // static so init all the time
        for (int i = 0; i < addJokers; i++) {
            newDeck.add(joker);
        }
        newDeck.addAll(normalCardDeckNoJoker);
        return newDeck;
    }
    public static List<PlayingCard> createDeckForRandomSuitWithXJokers(int addJokers) {
        Suit randomSuit = Suit.randomSuit();
        log.info("randomSuit is {} ", randomSuit);

        return createDeckForSuitWithXJokers(randomSuit, addJokers);
    }
    public static List<PlayingCard> createDeckForSuitWithXJokers(Suit suit, int addJokers) {
        List<PlayingCard> newDeck = new ArrayList<>(); // static so init all the time
        switch (suit) {
            case CLUBS -> newDeck.addAll(clubsDeckNoJoker);
            case DIAMONDS -> newDeck.addAll(diamondsDeckNoJoker);
            case HEARTS -> newDeck.addAll(heartsDeckNoJoker);
            case SPADES -> newDeck.addAll(spadesDeckNoJoker);
            default ->
                    throw new MyNPException("createDeckForSuitWithXJokers","suit [" + suit + "]");
        }
        for (int i = 0; i < addJokers; i++) {
            newDeck.add(joker);
        }
        return newDeck;
    }
    public static boolean isValid2LetterCardId(String rankAndSuit) {
        if (rankAndSuit == null
                || rankAndSuit.isEmpty())
            throw new MyNPException("isValid2LetterCardId","rankAndSuit [" + rankAndSuit + "]");
        for (PlayingCard playingCard : normalCardDeckOneJoker) {
            if (playingCard.rankAndSuit.equals(rankAndSuit)) return true;
        }
        return false;
    }
    public static PlayingCard getPlayingCardFromCardId(String card) {
        if (card == null || card.isEmpty())
            throw new MyNPException("getPlayingCardFromCardId", "card [" + card + "]");
        if (!isValid2LetterCardId(card))
            throw new MyBusinessException("getPlayingCardFromCardId", "this playing card is not valid [" + card + "]");
        for (PlayingCard playingCard : normalCardDeckOneJoker) {
            if (playingCard.rankAndSuit.equals(card)) {
                return playingCard;
            }
        }
        throw new MyBusinessException("this playing card is unknown [" + card + "]");
    }
    public static boolean isJoker(String cardId) {
        if (cardId == null
                || cardId.isEmpty())
            throw new MyNPException("isJoker","card [" + cardId + "]");
        return "JR".equals(cardId);
    }
    public static int calculateValueWithDefaultHighlow(String cardId, Type type) {
        if (cardId == null
                || cardId.isEmpty()
                || !isValid2LetterCardId(cardId))
            throw new MyNPException("getPlayingCardFromCardId","card [" + cardId + "]");
        PlayingCard playingCard = getPlayingCardFromCardId(cardId);
        return calculateValueWithDefaultHighlowFromRank(playingCard.rank, type);
    }
    public static int calculateValueWithDefaultHighlowFromRank(Rank rank, Type type) {
        Type localType = type == null ? Type.HIGHLOW : type;
        switch (localType) {
            case HIGHLOW -> {
                return switch (rank) {case JOKER->0;case ACE->1;case KING->13;case QUEEN->12;case JACK->11;default->
                        // 2 until 10
                        Integer.parseInt(rank.getLabel());};
            }
            case BLACKJACK -> {
                return switch (rank) {case JOKER->0;case ACE->1; // or 11
                    case KING->10;case QUEEN->10;case JACK->10;default->
                        // 2 until 10
                        Integer.parseInt(rank.getLabel());};
            }
            case ERROR -> throw new MyBusinessException("This type is in error [" + type + "]");
            default ->
                    throw new MyBusinessException("This type is not forseen [" + type + "]");
        }
    }

}
