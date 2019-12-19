package nl.knikit.card.entity.enums;

import lombok.Getter;
import nl.knikit.card.entity.Player;
import nl.knikit.card.entity.enums.enumlabel.LabeledEnum;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <H1>AIlevel</H1> Artificial Intelligence level for simulation human-like intelligence to
 * {@link Player}s that are bots. AiLevel is an enum
 * since the levels are a predefined list of values.
 * AIlevels that can be used are
 * <ul>
 * <li> {@link #SMART}
 * <li> {@link #MEDIUM}
 * <li> {@link #DUMB}
 * <li> {@link #HUMAN}
 * <li> {@link #NONE}
 * </ul>
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */

@Getter
public enum AiLevel implements LabeledEnum {

    @Column(name = "AI_LEVEL", length = 10, nullable = false)
    DUMB("Dumb"), MEDIUM("Medium"), SMART("Smart"), HUMAN("Human"), NONE("None");
    
    /**
     * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
     * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
     * factory methods for creating an instance like creating groups from enums.
     * Here it is used to group all enums.
     */
    public static Set<AiLevel> aiLevels = EnumSet.of(DUMB, MEDIUM, SMART, HUMAN, NONE);
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<String,AiLevel> lookup
            = new HashMap<>();
    static {
        for(AiLevel aiLevel : EnumSet.allOf(AiLevel.class))
            lookup.put(aiLevel.getLabel(), aiLevel);
    }
    @Transient
    private String label;
    
    AiLevel(){

    }
    AiLevel(String label) {
        this();
        this.label = label;
    }
    
    public static AiLevel fromLabel(String label) {
        return lookup.get(label);
    }
}
