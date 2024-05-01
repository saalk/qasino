package cloud.qasino.games.database.entity.enums.game.style;

import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum OneTimeInsurance {
    
    NO("n","No insurance"),
    TENTH_ANTE("t","10% of the bet"),
    QUARTER_ANTE("q","25% of the bet"),
    HALF_ANTE("h","50% of the bet");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, OneTimeInsurance> lookup
            = new HashMap<>();
    static {
        for(OneTimeInsurance oneTimeInsurance : EnumSet.allOf(OneTimeInsurance.class))
            lookup.put(oneTimeInsurance.getLabel(), oneTimeInsurance);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    OneTimeInsurance(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static OneTimeInsurance fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static OneTimeInsurance fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static OneTimeInsurance fromLabelWithDefault(String label) {
        OneTimeInsurance oneTimeInsurance = fromLabel(label);
        if (oneTimeInsurance == null) return OneTimeInsurance.TENTH_ANTE;
        return oneTimeInsurance;
    }

    public static OneTimeInsurance fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }


}
