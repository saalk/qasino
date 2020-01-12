package cloud.qasino.card.entity.enums;

import lombok.Getter;

import javax.persistence.Column;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <H1>CardGame</H1> A selection of card gameDtos that can be selected to play. <p> More gameDtos will be
 * added in future.
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */
@Getter
public enum Type implements LabeledEnum {

    /**
     * HIGHLOW cardgame is a simple higher or lower guessing game. The dealer places one card
     * face-down in front of the player, then another card face-up for the players Hand. The player
     * guesses whether the value of the face-down card is higher or lower. <p> The player places his
     * initial balance. The house matches that balance into the pot. When the player guesses, he wins or
     * loses the pot depending on the outcome of his guess. After that round, the player can pass
     * the balance to another player, or go double or nothing on the next balance depending on the specific
     * style of HIGHLOW.
     */
    @Column(name = "type", length = 25)
    HIGHLOW("Hi-Lo"), BLACKJACK("Blackjack");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String, Type> lookup
            = new HashMap<>();
    static {
        for(Type type : EnumSet.allOf(Type.class))
            lookup.put(type.getLabel(), type);
    }

    private String label;

    Type(String label) {
        this.label = label;
    }

    public static Type fromLabel(String label) {
        Type type = null;
        type = lookup.get(label);
        return type;
    }

    public static Set<Type> cardGamesListType = EnumSet.of(HIGHLOW);

}
