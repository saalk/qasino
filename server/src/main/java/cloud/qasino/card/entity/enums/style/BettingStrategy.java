package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum BettingStrategy {
    
    REGULAR("r", "Regular"),
    DOUBLE_OR_NOTHING("d","Double or nothing");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, BettingStrategy> lookup
            = new HashMap<>();
    static {
        for(BettingStrategy bettingStrategy : EnumSet.allOf(BettingStrategy.class))
            lookup.put(bettingStrategy.getLabel(), bettingStrategy);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    BettingStrategy(String label, String description) {
        this.label = label;
        this.description = description;
    }
    
    public static BettingStrategy fromLabel(String label) {
            return lookup.get(label.toLowerCase());
    }

    public static BettingStrategy fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static BettingStrategy fromLabelWithDefault(String label) {
        BettingStrategy bettingStrategy = fromLabel(label);
        if (bettingStrategy == null) return BettingStrategy.DOUBLE_OR_NOTHING;
        return bettingStrategy;
    }

    public static BettingStrategy fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
    
}
