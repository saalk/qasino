package cloud.qasino.games.database.entity.enums.card;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.Getter;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Location implements LabeledEnum {

    @Column(name = "location", length = 25, nullable = false)

    // before a deck cards are shuffled
    PACK("pack"),
    // A pile of cards, face down, which are left over after setting up the rest of the game
    STOCK("stock"),
    // Cards are placed directly on top of each other, disallowing the player to see any card other than the top
    STACK("stack"),

    // cards in the hand of a player
    HAND("hand"),

    // The Foundations: one or more piles on which a whole suit or sequence must be built up.
    PILE_1("pile_1"),
    PILE_2("pile_2"),

    // The Tableau: one or more piles that make up the main table.
    TABLEAU_1("tableau_1"),

    // Cards from the stock pile that have no use
    WASTEPILE("wastepile"),
    ERROR("error");

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
    public static Set<Location> locations = EnumSet.of(PACK, STOCK,
            STACK, HAND, PILE_1, PILE_2, TABLEAU_1, WASTEPILE. ERROR);

    static {
        for (Location location : EnumSet.allOf(Location.class))
            lookup.put(location.getLabel(), location);
    }

    public static final Map<String, Location> locationMapNoError
            = new HashMap<>();
    static {
        for(Location location : EnumSet.allOf(Location.class))
            if (!location.getLabel().equalsIgnoreCase("error"))
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
