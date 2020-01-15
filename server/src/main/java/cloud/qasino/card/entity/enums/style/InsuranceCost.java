package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum InsuranceCost {
    
    NO("n"),
    QUARTER_ANTE("q"),
    HALF_ANTE("h");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String, InsuranceCost> lookup
            = new HashMap<>();
    static {
        for(InsuranceCost insuranceCost : EnumSet.allOf(InsuranceCost.class))
            lookup.put(insuranceCost.getLabel(), insuranceCost);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    InsuranceCost(String label) {
        this.label = label;
    }
    
    public static InsuranceCost fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return InsuranceCost.NO;
        }
    }

    public static InsuranceCost fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }


}
