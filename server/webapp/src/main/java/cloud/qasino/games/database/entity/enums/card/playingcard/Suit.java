package cloud.qasino.games.database.entity.enums.card.playingcard;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <H1>Suit</H1>
 * <p>
 * Suit.ENUM("String")
 * - Suit.CLUBS.name() or
 * - Suit.CLUBS.label or
 * - Suit.CLUBS.getLabel()
 * -> gets the "C"
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Suit implements LabeledEnum {

    @Column(name = "suit", length = 10, nullable = false)
    CLUBS("C"),
    DIAMONDS("D"),
    HEARTS("H"),
    SPADES("S"),
    JOKERS("R");

    private static final Random SUIT = new Random();
    private static final Suit[] suits = values();
    // declare a method inside an enum class body that is static
    // and returns an enum value. This method will call nextInt()
    // from a Random object
    public static Suit randomSuit()  {
        // Inside randomDirection(), we call the method nextInt() with an
        // integer argument. The nextInt() method returns a random number
        // to access the directions array; therefore, we need to make sure
        // the integer is not out of the bounds of the array by passing a
        // bound argument to nextInt(). The bound argument is the total
        // number of directions, which will not exceed the size of the array.
        return suits[SUIT.nextInt(suits.length)];
    }

    public static final Map<String, Suit> suitMapNoError = new HashMap<>();
    private static final Map<String, Suit> lookup = new HashMap<>();
    static {
        for (Suit suit : EnumSet.allOf(Suit.class))
            lookup.put(suit.getLabel(), suit);
    }
    static {
        for (Suit suit : EnumSet.allOf(Suit.class))
            if (!suit.getLabel().toLowerCase().equals("error"))
                suitMapNoError.put(suit.getLabel(), suit);
    }

    @Transient
    private String label;

    public static Suit fromLabel(String inputLabel) {
        String label = StringUtils.upperCase(inputLabel);
        try {
            Suit.lookup.get(label);
        } catch (Exception e) {
            return null;
        }
        return Suit.lookup.get(label);
    }

}
