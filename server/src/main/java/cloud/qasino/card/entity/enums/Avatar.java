package cloud.qasino.card.entity.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Getters, no setters needed
@Getter
public enum Avatar implements LabeledEnum {

    @Column(name = "avatar", length = 25)
    ELF("elf"), MAGICIAN("magician"), GOBLIN("goblin"), ROMAN("warrior"), ERROR("error");
    
    /**
     * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
     * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
     * factory methods for creating an instance like creating groups from enums.
     * Here it is used to group all enums.
     */
    public static Set<Avatar> avatars = EnumSet.of(ELF, MAGICIAN, GOBLIN, ROMAN, ERROR);

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

    Avatar() {}

    Avatar(String label) {
        this();
        this.label = Avatar.fromLabel(label).getLabel();
    }

    public static Avatar fromLabel(String inputLabel) {
        String label = StringUtils.lowerCase(inputLabel);
        return (lookup.get(label) != null) ? lookup.get(label) : ERROR;
    }
}
