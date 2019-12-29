package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum NumOfDecks {
    
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
    private static final Map<String, NumOfDecks> lookup
            = new HashMap<>();
    static {
        for(NumOfDecks numOfDecks : EnumSet.allOf(NumOfDecks.class))
            lookup.put(numOfDecks.getLabel(), numOfDecks);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    NumOfDecks(String label) {
        this.label = label;
    }
    
    public static NumOfDecks fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return NumOfDecks.ALL_CARDS;
        }
    }
    
    
}
