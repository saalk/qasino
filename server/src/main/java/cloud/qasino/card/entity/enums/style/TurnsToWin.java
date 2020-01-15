package cloud.qasino.card.entity.enums.style;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum TurnsToWin {
    
    NO_LIMIT("n"),
    ONE_WINS("1"),
    TWO_IN_A_ROW_WINS("2"),
    THREE_IN_A_ROW_WINS("3");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String, TurnsToWin> lookup
            = new HashMap<>();
    static {
        for(TurnsToWin turnsToWin : EnumSet.allOf(TurnsToWin.class))
            lookup.put(turnsToWin.getLabel(), turnsToWin);
    }
    
    String label;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    TurnsToWin(String label) {
        this.label = label;
    }
    
    public static TurnsToWin fromLabel(String label) {
        try {
	        return lookup.get(label.toUpperCase());
        } catch (Exception e){
            return TurnsToWin.NO_LIMIT;
        }
    }

    public static TurnsToWin fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

}
