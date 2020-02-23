package cloud.qasino.card.entity.enums.player;

import cloud.qasino.card.entity.enums.LabeledEnum;
import lombok.Getter;

import javax.persistence.Column;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Getters, no setters needed
@Getter
public enum Avatar implements LabeledEnum {

    @Column(name = "avatar", length = 25)

    ELF("elf", "coin for no insurance"),
    MAGICIAN("magician", "six wins the game"),
    GOBLIN("goblin", "coin pays insurance as bonus"),
    ROMAN("warrior", "clubs for free move"),
    ERROR("error", "no powers");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromName the Enum based on the name eg. key "Elf" -> value Avatar.ELF
     */
    public static final Map<String, Avatar> lookup
            = new HashMap<>();
    public static final Map<String, Avatar> avatarMapNoError
            = new HashMap<>();
    /**
     * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
     * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
     * factory methods for creating an instance like creating groups from enums.
     * Here it is used to group all enums.
     */
    public static Set<Avatar> avatars = EnumSet.of(ELF, MAGICIAN, GOBLIN, ROMAN, ERROR);

    static {
        for (Avatar avatar : EnumSet.allOf(Avatar.class))
            lookup.put(avatar.getLabel(), avatar);
    }

    static {
        for (Avatar avatar : EnumSet.allOf(Avatar.class))
            if (!avatar.getLabel().toLowerCase().equals("error"))
                avatarMapNoError.put(avatar.getLabel(), avatar);
    }

    private String label;
    private String power;

    Avatar() {
        this.label = "error";
    }

    Avatar(String label, String power) {
        this();
        this.label = label;
        this.power = power;
    }

    public static Avatar fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static Avatar fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static Avatar fromLabelWithDefault(String label) {
        Avatar avatar = fromLabel(label);
        if (avatar == null) return Avatar.ERROR;
        return avatar;
    }

    public static Avatar fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
}
