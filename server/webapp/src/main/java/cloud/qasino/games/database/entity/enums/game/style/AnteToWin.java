package cloud.qasino.games.database.entity.enums.game.style;

import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum AnteToWin {

// todo determine what to do here : bet vs ante ration?
    NA("n", "not applicable"),
    TIMES_3_WINS("3", "Triple your ante and win direct"),
    TIMES_5_WINS("5", "5 Times your ante wins direct"),
    ERROR("e", "error");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, AnteToWin> lookup
            = new HashMap<>();
    static {
        for(AnteToWin anteToWin : EnumSet.allOf(AnteToWin.class))
            lookup.put(anteToWin.getLabel(), anteToWin);
    }

    public static final Map<String, AnteToWin> AnteToWinMapNoError
            = new HashMap<>();
    static {
        for(AnteToWin anteToWin : EnumSet.allOf(AnteToWin.class))
            if (!anteToWin.getLabel().toLowerCase().equals("error"))
                AnteToWinMapNoError.put(anteToWin.getLabel(), anteToWin);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    AnteToWin(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static AnteToWin fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static AnteToWin fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static AnteToWin fromLabelWithDefault(String label) {
        AnteToWin anteToWin = fromLabel(label);
        if (anteToWin == null) return AnteToWin.ERROR;
        return anteToWin;
    }

    public static AnteToWin fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
    
}
