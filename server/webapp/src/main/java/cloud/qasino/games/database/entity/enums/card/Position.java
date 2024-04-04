package cloud.qasino.games.database.entity.enums.card;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO make ordered
@Getter
public enum Position implements LabeledEnum {

    // TODO ordered make sequential, what is manual ??
    @Column(name = "location", length = 25, nullable = false)
    SHUFFLED("shuffled"), ORDERED("ordered"), MANUAL("manual"), ERROR("error");

    public static final Map<String, Position> lookup = new HashMap<>();
    public static final Map<String, Position> positionMapNoError = new HashMap<>();
    public static Set<Position> locations = EnumSet.of(SHUFFLED, ORDERED, MANUAL, ERROR);

    static {
        for (Position location : EnumSet.allOf(Position.class))
            lookup.put(location.getLabel(), location);
    }

    static {
        for (Position position : EnumSet.allOf(Position.class))
            if (!position.getLabel().toLowerCase().equals("error"))
                positionMapNoError.put(position.getLabel(), position);
    }

    @Transient
    private String label;

    Position() {
        this.label = "error";
    }

    Position(String label) {
        this();
        this.label = label;
    }

    public static Position fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static Position fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static Position fromLabelWithDefault(String label) {
        Position position = fromLabel(label);
        if (position == null) return Position.ERROR;
        return position;
    }

    public static Position fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

}
