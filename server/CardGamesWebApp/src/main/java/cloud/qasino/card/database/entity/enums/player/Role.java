package cloud.qasino.card.database.entity.enums.player;

import cloud.qasino.card.database.entity.enums.LabeledEnum;
import lombok.Getter;

import javax.persistence.Column;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Role implements LabeledEnum {

    @Column(name = "role", length = 25)
    INITIATOR("initiator"),
    INVITED("invited"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    BOT("bot"),
    ERROR("error");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, Role> lookup
            = new HashMap<>();
    static {
        for(Role role : EnumSet.allOf(Role.class))
            lookup.put(role.getLabel(), role);
    }
    public static final Map<String, Role> roleMapNoError
            = new HashMap<>();
    static {
        for(Role role : EnumSet.allOf(Role.class))
            if (!role.getLabel().toLowerCase().equals("error"))
                roleMapNoError.put(role.getLabel(), role);
    }

    private String label;

    Role() {
        this.label = "error";
    }

    Role(String label) {
        this();
        this.label = label;
    }

    public static Role fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static Role fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static Role fromLabelWithDefault(String label) {
        Role role = fromLabel(label);
        if (role == null) return Role.ERROR;
        return role;
    }

    public static Role fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

    public static Set<Role> roleListActivePlayer = EnumSet.of(INITIATOR, BOT, ACCEPTED);

}
