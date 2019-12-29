package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum InsureanceCost {
    
    NO("N"),
    QUARTER_ANTE("Q"),
    HALF_ANTE("H");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String, InsureanceCost> lookup
            = new HashMap<>();
    static {
        for(InsureanceCost insureanceCost : EnumSet.allOf(InsureanceCost.class))
            lookup.put(insureanceCost.getLabel(), insureanceCost);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    InsureanceCost(String label) {
        this.label = label;
    }
    
    public static InsureanceCost fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return InsureanceCost.NO;
        }
    }
    
    
}
