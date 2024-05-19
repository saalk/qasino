package cloud.qasino.games.database.entity.enums.game.style;

import lombok.Getter;

import jakarta.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum TurnsToWin {

    NA("n", "Not applicable"),
    ONE_WINS("1", "One move wins"),
    TWO_IN_A_ROW_WINS("2", "Two in a row wins"),
    THREE_IN_A_ROW_WINS("3", "Three in a row wins");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, TurnsToWin> lookup
            = new HashMap<>();

    static {
        for (TurnsToWin turnsToWin : EnumSet.allOf(TurnsToWin.class))
            lookup.put(turnsToWin.getLabel(), turnsToWin);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    TurnsToWin(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static TurnsToWin fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static TurnsToWin fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static TurnsToWin fromLabelWithDefault(String label) {
        TurnsToWin turnsToWin = fromLabel(label);
        if (turnsToWin == null) return TurnsToWin.THREE_IN_A_ROW_WINS;
        return turnsToWin;
    }

    public static TurnsToWin fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

}
