package cloud.qasino.games.database.entity.enums.game.style;

import lombok.Getter;

import jakarta.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum RoundsToWin {

    NA ("n","Not applicable"),
    ONE_ROUND("1","One round finish"),
    TWO_ROUNDS("2","Two rounds finish"),
    THREE_ROUNDS("3","Three round finish");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, RoundsToWin> lookup
            = new HashMap<>();

    static {
        for (RoundsToWin roundsToWin : EnumSet.allOf(RoundsToWin.class))
            lookup.put(roundsToWin.getLabel(), roundsToWin);
    }
    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    RoundsToWin(String label, String description) {
        this.label = label;
        this.description = description;
    }
    public static RoundsToWin fromName(String name) {
        return RoundsToWin.valueOf(name.toUpperCase());
    }
    public static RoundsToWin fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static RoundsToWin fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static RoundsToWin fromLabelWithDefault(String label) {
        RoundsToWin roundsToWin = fromLabel(label);
        if (roundsToWin == null) return RoundsToWin.NA ;
        return roundsToWin;
    }

    public static RoundsToWin fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

}
