package cloud.qasino.card.entity.enums.variant;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum VariantBetting {
    
    REGULAR("R"),
    DOUBLE_OR_NOTHING("D");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String,VariantBetting> lookup
            = new HashMap<>();
    static {
        for(VariantBetting variantBetting : EnumSet.allOf(VariantBetting.class))
            lookup.put(variantBetting.getLabel(), variantBetting);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    VariantBetting(String label) {
        this.label = label;
    }
    
    public static VariantBetting fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return VariantBetting.REGULAR;
        }
    }
    
    
}
