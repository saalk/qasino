package cloud.qasino.card.entity.enums.game;

import cloud.qasino.card.configuration.ContextType;
import cloud.qasino.card.entity.enums.LabeledEnum;
import lombok.Getter;

import javax.persistence.Column;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <H1>CardGame</H1> A selection of playingcard gameDtos that can be selected to play. <p> More gameDtos will be
 * added in future.
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */
@Getter
public enum Type implements LabeledEnum, ContextType {

    /**
     * HIGHLOW cardgame is a simple higher or lower guessing game. The dealer places one playingcard
     * face-down in front of the player, then another playingcard face-up for the players Hand. The player
     * guesses whether the value of the face-down playingcard is higher or lower. <p> The player places his
     * initial balance. The house matches that balance into the pot. When the player guesses, he wins or
     * loses the pot depending on the outcome of his guess. After that round, the player can pass
     * the balance to another player, or go double or nothing on the next balance depending on the specific
     * style of HIGHLOW.
     */
    @Column(name = "type", length = 25)
    HIGHLOW("highlow"), BLACKJACK("blackjack"), ERROR("error");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, Type> lookup
            = new HashMap<>();
    static {
        for(Type type : EnumSet.allOf(Type.class))
            lookup.put(type.getLabel(), type);
    }
    public static final Map<String, Type> typeMapNoError
            = new HashMap<>();
    static {
        for(Type type : EnumSet.allOf(Type.class))
            if (!type.getLabel().toLowerCase().equals("error"))
                typeMapNoError.put(type.getLabel(), type);
    }

    private String label;

    Type() {
        this.label = "error";
    }

    Type(String label) {
        this();
        this.label = label;
    }

    @Override
    public String getId() {
        return toString();
    }

    public static Type fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static Type fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static Type fromLabelWithDefault(String label) {
        Type type = fromLabel(label);
        if (type == null) return Type.ERROR;
        return type;
    }

    public static Type fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

    public static Set<Type> cardGamesListType = EnumSet.of(HIGHLOW);

}
