package cloud.qasino.card.database.entity.enums.card.playingcard;

import cloud.qasino.card.database.entity.enums.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

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
    JOKERS("J");

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
