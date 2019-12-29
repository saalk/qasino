package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum MaxRounds {
    
    NO_LIMIT("N"),
    ONE_ROUND("1");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String, MaxRounds> lookup
            = new HashMap<>();
    static {
        for(MaxRounds maxRounds : EnumSet.allOf(MaxRounds.class))
            lookup.put(maxRounds.getLabel(), maxRounds);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    MaxRounds(String label) {
        this.label = label;
    }
    
    public static MaxRounds fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return MaxRounds.NO_LIMIT;
        }
    }
    
    
}
