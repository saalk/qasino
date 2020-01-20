package cloud.qasino.card.domain.qasino.style;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum MaxAnte {
// todo determine what to do here : bet vs ante ration?
    NORMAL("n", "Normal"),
    HIGHEST_WINS("h", "Highest wins");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, MaxAnte> lookup
            = new HashMap<>();
    static {
        for(MaxAnte maxAnte : EnumSet.allOf(MaxAnte.class))
            lookup.put(maxAnte.getLabel(), maxAnte);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    MaxAnte(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static MaxAnte fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static MaxAnte fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static MaxAnte fromLabelWithDefault(String label) {
        MaxAnte maxAnte = fromLabel(label);
        if (maxAnte == null) return MaxAnte.HIGHEST_WINS;
        return maxAnte;
    }

    public static MaxAnte fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
    
}
