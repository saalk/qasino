package cloud.qasino.card.entity.enums;

import lombok.Getter;
import cloud.qasino.card.entity.enums.enumlabel.LabeledEnum;

import javax.persistence.Column;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <H1>Avatar</H1> What species is applicable
 * <p> There are 4 species to choose from
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */

// Getters, no setters needed
@Getter
public enum Avatar implements LabeledEnum {

    @Column(name = "AVATAR", length = 25)
    ELF("Elf"), MAGICIAN("Magician"), GOBLIN("Goblin"), ROMAN("Warrior");
    
    /**
     * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
     * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
     * factory methods for creating an instance like creating groups from enums.
     * Here it is used to group all enums.
     */
    public static Set<Avatar> avatars = EnumSet.of(ELF, MAGICIAN, GOBLIN, ROMAN);

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromName the Enum based on the name eg. key "Elf" -> value Avatar.ELF
     */
    private static final Map<String,Avatar> lookup
            = new HashMap<>();
    static {
        for(Avatar avatar : EnumSet.allOf(Avatar.class))
            lookup.put(avatar.getLabel(), avatar);
    }
    private String label;

    Avatar(String label) {
        this.label = label;
    }

    public static Avatar fromLabel(String label) {
        return lookup.get(label);
    }

}
