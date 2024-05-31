package cloud.qasino.games.database.entity.enums.game.style;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum DeckConfiguration {

    ALL_THREE_JOKERS("3", "Normal deck with 3 jokers"),
    ALL_TWO_JOKERS("2", "Normal deck with 2 jokers"),
    ALL_ONE_JOKER("1", "Normal deck with 1 jokers"),
    ALL_NO_JOKER("n", "Normal deck without jokers"),
    RANDOM_SUIT_THREE_JOKERS("u", "Random suit only with 3 jokers"),
    RANDOM_SUIT_TWO_JOKERS("t", "Random suit only with 2 jokers"),
    RANDOM_SUIT_ONE_JOKER("s", "Random suit only with 1 jokers"),
    RANDOM_SUIT_NO_JOKER("r", "Random suit only without jokers");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, DeckConfiguration> lookup
            = new HashMap<>();
    static {
        for (DeckConfiguration deckConfiguration : EnumSet.allOf(DeckConfiguration.class))
            lookup.put(deckConfiguration.getLabel(), deckConfiguration);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    DeckConfiguration(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static DeckConfiguration fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static DeckConfiguration fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static DeckConfiguration fromLabelWithDefault(String label) {
        DeckConfiguration deckConfiguration = fromLabel(label);
        if (deckConfiguration == null) return DeckConfiguration.RANDOM_SUIT_TWO_JOKERS;
        return deckConfiguration;
    }

    public static DeckConfiguration fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }


}
