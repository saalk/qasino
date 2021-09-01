package cloud.qasino.quiz.entity.enums.quiz;

import cloud.qasino.quiz.entity.enums.quiz.playingquiz.Rank;
import cloud.qasino.quiz.entity.enums.quiz.playingquiz.Suit;
import cloud.qasino.quiz.entity.enums.game.Type;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class PlayingQuiz {

    public static final List<PlayingQuiz> prototypeDeck = new ArrayList<>();
    protected static final PlayingQuiz joker = new PlayingQuiz(Rank.JOKER, Suit.JOKERS);

    static {
        for (Suit suit : Suit.values()) {
            if (suit != Suit.JOKERS) {
                for (Rank rank : Rank.values()) {
                    if (rank != Rank.JOKER) {
                        prototypeDeck.add(new PlayingQuiz(rank, suit));
                    }
                }
            }
        }
    }

    // 13 progressing ranks 2 to 10, jack, queen, king, ace.
    private String quizId;
    private Rank rank;
    private Suit suit;
    private int value;
    private String thumbnailPath;

    public PlayingQuiz() {
    }

    public PlayingQuiz(Rank rank, Suit suit) {
        this();
        if (rank == null || suit == null)
            throw new NullPointerException(rank + ", " + suit);
        this.rank = rank;
        this.suit = suit;

        final StringBuilder builder = new StringBuilder();
        this.quizId = builder.append(rank.getLabel()).append(suit.getLabel()).toString();
        this.value = calculateValueWithDefaultHighlow(rank, null);
        // todo: set thumbnailPath
    }

    public static List<PlayingQuiz> newDeck(int addJokers) {
        List<PlayingQuiz> newDeck = new ArrayList<>(); // static so init all the time
        for (int i = 0; i < addJokers; i++) {
            newDeck.add(joker);
        }
        newDeck.addAll(prototypeDeck);
        return newDeck;
    }

    public static boolean isValidQuizId(String quizId) {
        if (quizId == null
                || quizId.isEmpty())
            return false;

        for (PlayingQuiz playingQuiz : prototypeDeck) {
            if (playingQuiz.quizId.equals(quizId)) return true;
        }
        return false;
    }

    public boolean setPlayingQuizFromQuizId(String quizId) {

        if (quizId == null
                || quizId.isEmpty()
                || !isValidQuizId(quizId))
            return false;
        for (PlayingQuiz playingQuiz : prototypeDeck) {
            if (playingQuiz.quizId.equals(quizId)) {
                this.quizId = quizId;
                this.rank = playingQuiz.rank;
                this.suit = playingQuiz.suit;
                this.value = calculateValueWithDefaultHighlow(rank, null);
                return true;
            }
        }
        return false;
    }

    public boolean isJoker() {
        String jokerQuiz = quizId;
        return jokerQuiz.equals("RJ");
    }

    private int calculateValueWithDefaultHighlow(Rank rank, Type type) {

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
