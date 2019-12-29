package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum MaxAnte {
    
    NORMAL("N"),
    HIGHEST_WINS("H");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String, MaxAnte> lookup
            = new HashMap<>();
    static {
        for(MaxAnte maxAnte : EnumSet.allOf(MaxAnte.class))
            lookup.put(maxAnte.getLabel(), maxAnte);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    MaxAnte(String label) {
        this.label = label;
    }
    
    public static MaxAnte fromLabel(String label) {
        try {
            return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return MaxAnte.NORMAL;
        }
    }
    
    
}
