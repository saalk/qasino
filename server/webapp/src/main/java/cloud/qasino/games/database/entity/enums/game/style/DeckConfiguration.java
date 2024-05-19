package cloud.qasino.games.database.entity.enums.game.style;

import lombok.Getter;

import jakarta.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum DeckConfiguration {

    ALL_THREE_JOKER("3", "Complete with three jokers"),
    ALL_TWO_JOKER("2", "Complete with two jokers"),
    ALL_ONE_JOKER("1", "Complete with one joker"),
    ALL_NO_JOKERS("a", "No jokers"),
    RANDOM_SUIT("r", "Randomw suit");

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
        if (deckConfiguration == null) return DeckConfiguration.ALL_THREE_JOKER;
        return deckConfiguration;
    }

    public static DeckConfiguration fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }


}
