package cloud.qasino.games.database.entity.enums.move;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.Getter;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Move implements LabeledEnum {

    @Column(name = "cardMove", length = 25, nullable = false)

    // generic cardmoves in a playing
    DEAL("deal"),
    PASS("pass"),
    NEXT("next"),
    STOP("stop"),

    // cardmoves in hartenjagen
    PLAY("play"),

    // cardmoves in blackjack
    STAND("stand"),
    DOUBLE("double"),

    // cardmoves in higher/lower
    HIGHER("higher"),
    LOWER("lower"),

    // a problem,
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
    public static Set<Move> highlowMoves = EnumSet.of(DEAL, HIGHER, LOWER, STOP);
    public static Set<Move> blackjackMoves = EnumSet.of(DEAL, STAND, DOUBLE, STOP);

    static {
        for (Move move : EnumSet.allOf(Move.class))
            lookup.put(move.getLabel(), move);
    }

    static {
        for (Move move : EnumSet.allOf(Move.class))
            if (!move.getLabel().equalsIgnoreCase("error"))
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
