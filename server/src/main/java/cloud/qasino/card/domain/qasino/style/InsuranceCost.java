package cloud.qasino.card.domain.qasino.style;

import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum InsuranceCost {
    
    NO("n","No insurance"),
    TENTH_ANTE("t","10% of the bet"),
    QUARTER_ANTE("q","25% of the bet"),
    HALF_ANTE("h","50% of the bet");
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, InsuranceCost> lookup
            = new HashMap<>();
    static {
        for(InsuranceCost insuranceCost : EnumSet.allOf(InsuranceCost.class))
            lookup.put(insuranceCost.getLabel(), insuranceCost);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    InsuranceCost(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static InsuranceCost fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static InsuranceCost fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static InsuranceCost fromLabelWithDefault(String label) {
        InsuranceCost insuranceCost = fromLabel(label);
        if (insuranceCost == null) return InsuranceCost.TENTH_ANTE;
        return insuranceCost;
    }

    public static InsuranceCost fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }


}
