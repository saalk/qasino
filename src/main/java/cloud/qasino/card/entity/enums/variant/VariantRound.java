package cloud.qasino.card.entity.enums.variant;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum VariantRound {
    
    NO_LIMIT("N"),
    ONE_ROUND("1");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String,VariantRound> lookup
            = new HashMap<>();
    static {
        for(VariantRound variantRound : EnumSet.allOf(VariantRound.class))
            lookup.put(variantRound.getLabel(), variantRound);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    VariantRound(String label) {
        this.label = label;
    }
    
    public static VariantRound fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return VariantRound.NO_LIMIT;
        }
    }
    
    
}
