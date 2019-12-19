package nl.knikit.card.entity.enums.variant;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * <H1>GameVariant</H1> A selection of variants to a specific card gameDtos that can be selected to
 * play. <p> More variants will be added in future.
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */

@Getter
public enum VariantAnte {
    
    NORMAL("N"),
    HIGHEST_WINS("H");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String,VariantAnte> lookup
            = new HashMap<>();
    static {
        for(VariantAnte variantAnte : EnumSet.allOf(VariantAnte.class))
            lookup.put(variantAnte.getLabel(), variantAnte);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    VariantAnte(String label) {
        this.label = label;
    }
    
    public static VariantAnte fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return VariantAnte.NORMAL;
        }
    }
    
    
}
