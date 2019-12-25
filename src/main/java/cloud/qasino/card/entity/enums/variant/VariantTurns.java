package cloud.qasino.card.entity.enums.variant;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum VariantTurns {
    
    NO_LIMIT("N"),
    ONE_WINS("1"),
    TWO_IN_A_ROW_WINS("2"),
    THREE_IN_A_ROW_WINS("3");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String,VariantTurns> lookup
            = new HashMap<>();
    static {
        for(VariantTurns variantTurns : EnumSet.allOf(VariantTurns.class))
            lookup.put(variantTurns.getLabel(), variantTurns);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    VariantTurns(String label) {
        this.label = label;
    }
    
    public static VariantTurns fromLabel(String label) {
        try {
	        return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return VariantTurns.NO_LIMIT;
        }
    }
    
    
}
