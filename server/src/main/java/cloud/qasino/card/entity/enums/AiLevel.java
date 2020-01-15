package cloud.qasino.card.entity.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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
        this.label = AiLevel.fromLabel(label).getLabel();
    }
    
    public static AiLevel fromLabel(String inputLabel) {
        String label = StringUtils.lowerCase(inputLabel);
        return (lookup.get(label) != null) ? lookup.get(label) : ERROR;
    }
}
