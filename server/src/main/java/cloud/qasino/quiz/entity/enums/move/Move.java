package cloud.qasino.quiz.entity.enums.move;

import cloud.qasino.quiz.entity.enums.LabeledEnum;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Move implements LabeledEnum {

    @Column(name = "quizMove", length = 25, nullable = false)

    // shuffle the pack into a stock
    SHUFFLE("shuffle"),
    // setting up the layout areas - stock and hands
    SETTING_UP("setup"),

    // deal another quiz
    DEAL("deal"),

    // in hartenjagen
    PLAY("play"),
    DISQUIZ("disquiz"),

    // in blackjack

    // no more quizzes
    STAND("stand"),
    // no more quizzes
    DOUBLE("double"),

    // in higher/lower
    HIGHER("higher"),
    LOWER("lower"),

    // make no bid any more
    PASS("pass"),

    NEXT("next"),
    ERROR("error");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value Move.DUMB
     */
    public static final Map<String, Move> lookup
            = new HashMap<>();
    public static final Map<String, Move> moveMapNoError
            = new HashMap<>();
    /**
     * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
     * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
     * factory methods for creating an instance like creating groups from enums.
     * Here it is used to group all enums.
     */
    public static Set<Move> moves = EnumSet.of(DEAL, HIGHER, LOWER, PASS, NEXT, ERROR);

    static {
        for (Move move : EnumSet.allOf(Move.class))
            lookup.put(move.getLabel(), move);
    }

    static {
        for (Move move : EnumSet.allOf(Move.class))
            if (!move.getLabel().toLowerCase().equals("error"))
                moveMapNoError.put(move.getLabel(), move);
    }


    @Transient
    private String label;

    Move() {
        this.label = "error";
    }

    Move(String label) {
        this();
        this.label = label;
    }

    public static Move fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static Move fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static Move fromLabelWithDefault(String label) {
        Move move = fromLabel(label);
        if (move == null) return Move.ERROR;
        return move;
    }

    public static Move fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

}
