package cloud.qasino.card.database.entity.enums.player;

import cloud.qasino.card.database.entity.enums.LabeledEnum;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum AiLevel implements LabeledEnum {

    @Column(name = "aiLevel", length = 10, nullable = false)
    DUMB("dumb"), AVERAGE("average"), SMART("smart"), HUMAN("human"), ERROR("error");
    
    /**
     * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
     * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
     * factory methods for creating an instance like creating groups from enums.
     * Here it is used to group all enums.
     */
    public static Set<AiLevel> aiLevels = EnumSet.of(DUMB, AVERAGE, SMART, HUMAN, ERROR);
    
    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String,AiLevel> lookup
            = new HashMap<>();
    static {
        for(AiLevel aiLevel : EnumSet.allOf(AiLevel.class))
            lookup.put(aiLevel.getLabel(), aiLevel);
    }
    public static final Map<String, AiLevel> aiLevelMapNoError
            = new HashMap<>();
    static {
        for(AiLevel aiLevel : EnumSet.allOf(AiLevel.class))
            if (!aiLevel.getLabel().toLowerCase().equals("error"))
                aiLevelMapNoError.put(aiLevel.getLabel(), aiLevel);
    }

    @Transient
    private String label;
    
    AiLevel(){
        this.label = "error";
    }
    AiLevel(String label) {
        this();
        this.label = label;
    }

    public static AiLevel fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static AiLevel fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static AiLevel fromLabelWithDefault(String label) {
        AiLevel aiLevel = fromLabel(label);
        if (aiLevel == null) return AiLevel.ERROR;
        return aiLevel;
    }

    public static AiLevel fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
}
