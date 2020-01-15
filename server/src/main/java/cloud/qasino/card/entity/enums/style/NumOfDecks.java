package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum NumOfDecks {
    
    ALL_CARDS("a"),
    ALL_NO_JOKERS("n"),
    ONLY_HEARTS("h"),
    ONLY_SPADES("s"),
    ONLY_DIAMONDS("d"),
    ONLY_CLUBS("c"),
    RANDOM_SUIT("r");
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
    public static NumOfDecks fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }


}
