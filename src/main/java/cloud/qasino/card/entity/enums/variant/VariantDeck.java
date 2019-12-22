package cloud.qasino.card.entity.enums.variant;

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
public enum VariantDeck {
    
    ALL_CARDS("A"),
    ONLY_HEARTS("H"),
    ONLY_SPADES("S"),
    ONLY_DIAMONDS("D"),
    ONLY_CLUBS("C"),
    RANDOM_SUIT("R");
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String,VariantDeck> lookup
            = new HashMap<>();
    static {
        for(VariantDeck variantDeck : EnumSet.allOf(VariantDeck.class))
            lookup.put(variantDeck.getLabel(), variantDeck);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    VariantDeck(String label) {
        this.label = label;
    }
    
    public static VariantDeck fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return VariantDeck.ALL_CARDS;
        }
    }
    
    
}
