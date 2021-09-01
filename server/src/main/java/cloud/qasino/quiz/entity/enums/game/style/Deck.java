package cloud.qasino.quiz.entity.enums.game.style;

import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum Deck {

    ALL_THREE_JOKER("3", "Complete with three jokers"),
    ALL_TWO_JOKER("2", "Complete with two jokers"),
    ALL_ONE_JOKER("1", "Complete with one joker"),
    ALL_NO_JOKERS("a", "No jokers"),
    RANDOM_SUIT("r", "Randomw suit");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, Deck> lookup
            = new HashMap<>();

    static {
        for (Deck deck : EnumSet.allOf(Deck.class))
            lookup.put(deck.getLabel(), deck);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    Deck(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static Deck fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static Deck fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static Deck fromLabelWithDefault(String label) {
        Deck deck = fromLabel(label);
        if (deck == null) return Deck.ALL_THREE_JOKER;
        return deck;
    }

    public static Deck fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }


}
