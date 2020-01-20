package cloud.qasino.card.entity.enums.playingcard;

import cloud.qasino.card.entity.enums.LabeledEnum;
import cloud.qasino.card.entity.enums.event.Action;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Location implements LabeledEnum {

    @Column(name = "location", length = 25, nullable = false)
    HAND("hand"), PILE("pile"), TABLE("table"), DISCARD("discard"), ERROR("error");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value Location.DUMB
     */
    public static final Map<String, Location> lookup
            = new HashMap<>();
    /**
     * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
     * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
     * factory methods for creating an instance like creating groups from enums.
     * Here it is used to group all enums.
     */
    public static Set<Location> locations = EnumSet.of(HAND, PILE,
            TABLE, DISCARD);

    static {
        for (Location location : EnumSet.allOf(Location.class))
            lookup.put(location.getLabel(), location);
    }

    public static final Map<String, Location> locationMapNoError
            = new HashMap<>();
    static {
        for(Location location : EnumSet.allOf(Location.class))
            if (!location.getLabel().toLowerCase().equals("error"))
                locationMapNoError.put(location.getLabel(), location);
    }

    @Transient
    private String label;

    Location() {
        this.label = "error";
    }

    Location(String label) {
        this();
        this.label = label;
    }

    public static Location fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static Location fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static Location fromLabelWithDefault(String label) {
        Location location = fromLabel(label);
        if (location == null) return Location.ERROR;
        return location;
    }

    public static Location fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

}
