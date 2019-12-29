package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum BettingStrategy {
    
    REGULAR("R"),
    DOUBLE_OR_NOTHING("D");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String, BettingStrategy> lookup
            = new HashMap<>();
    static {
        for(BettingStrategy bettingStrategy : EnumSet.allOf(BettingStrategy.class))
            lookup.put(bettingStrategy.getLabel(), bettingStrategy);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    BettingStrategy(String label) {
        this.label = label;
    }
    
    public static BettingStrategy fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return BettingStrategy.REGULAR;
        }
    }
    
    
}
