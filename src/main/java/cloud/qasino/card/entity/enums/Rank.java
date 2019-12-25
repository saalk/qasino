package cloud.qasino.card.entity.enums;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * <H1>Rank</H1> Progressive value for a card. <p> There are thirteen ranks of each of the four
 * French suits. The rank 'Joker' is the 14th one.
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */

@Getter
public enum Rank implements LabeledEnum {

    /**
     * Because enum are constants, the names of an enum type's fields are in uppercase letters.
     */

    @Column(name = "rank", length = 10, nullable = false)
    ACE("A"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"),
    NINE("9"), TEN("10"), JACK("J"), QUEEN("Q"), KING("K"), JOKER("R");

    /**
     * Make a :
     * - a static HashMap lookup with key value pairs -> key= code/name, value= the ENUM
     * - a private field code/name and a method getCode/Name()
     * - a static fromLabel(code/name) that returns the ENUM based on the lookup key
     * -> the static fromLabel could better be called byLetter, byValue to distinguish from @Getter
     *
     * Now you can us a method fromLabel() that return with the ENUM based on a int/name
     * eg. "A" -> RANK.ACE
     *
     * HashMap:
     * - static hashMap.put(key, value)
     * - value = hashMap.fromLabel(key)
     */

    private static final Map<String,Rank> lookup
            = new HashMap<>();
    static {
        for(Rank rank : EnumSet.allOf(Rank.class))
            lookup.put(rank.getLabel(), rank);
    }

    @Transient
    private String label;

    Rank(){}

    Rank(String label) {
        this();
        this.label = label;
    }


    public static Rank fromLabel(String label) {
        return lookup.get(label);
    }

    /**
     * Usually the Face cards (K,Q,J) are worth 13,12,11 points, each Aces are worth 1. But the
     * selected card game type determines the playing value.
     * <p>
     * Values for {@link GameType}:
     * 1. Vote if equal cards are a loss or correct guess (usually loss since only high low counts).
     * 2. No jokers.
     * 3. Ace is worth 1
     */
    public int getValue(GameType inputGameType) {
        int value = 0;
        switch (inputGameType) {
            case HIGHLOW:
                // name() return the enum name like ACE or KING
                switch (fromLabel(this.label)) {
                    case JOKER:
                        value = 0;
                        break;
                    case ACE:
                        value = 1;
                        break;
                    case KING:
                        value = 13;
                        break;
                    case QUEEN:
                        value = 12;
                        break;
                    case JACK:
                        value = 11;
                        break;
                    default:
                        // toString() returns the enum  A ,2 ,3 etc
                        value = Integer.parseInt(label.toString());
                        break;
                }
            default:
                break;

        }
        return value;
    }
}

