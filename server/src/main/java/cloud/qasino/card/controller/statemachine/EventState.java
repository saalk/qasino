package cloud.qasino.card.controller.statemachine;

import cloud.qasino.card.entity.enums.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <H1>CardGame</H1> A selection of card gameDtos that can be selected to play. <p> More gameDtos will be
 * added in future.
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum EventState implements LabeledEnum {

    // new
    NEW("new","New Turn started"),
    DEALT ("dealt","Card dealt to player"),

    HIGHER ("higher","Player decided higher"),
    LOWER ("lower","Player decided lower"),

    ERROR ("lower","Player decided lower");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static Map<String, EventState> lookup
            = new HashMap<>();
    static {
        for(EventState gameState : EnumSet.allOf(EventState.class))
            lookup.put(gameState.getLabel(), gameState);
    }
    public static final Map<String, EventState> gameStates
            = new HashMap<>();
    static {
        for(EventState gameState : EnumSet.allOf(EventState.class))
            gameStates.put(gameState.getLabel(), gameState);
    }

    private String label;
    private String description;

    public static EventState fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static EventState fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static EventState fromLabelWithDefault(String label) {
        EventState gameState = fromLabel(label);
        if (gameState == null) return EventState.ERROR;
        return gameState;
    }

    public static EventState fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

    public static Set<EventState> cardGameHighLow = EnumSet.of(NEW, HIGHER, LOWER);
    public static String[] cardGamesHighLowValues = new String[]{NEW.name(),
            HIGHER.name(), LOWER.name()};

    public static Set<EventState> cardGamesBlackJack = EnumSet.of(NEW, DEALT);
    public static String[] cardGamesBlackJackValues = new String[]{NEW.name(), DEALT.name()};

    public static Set<EventState> cardGamesError = EnumSet.of(ERROR);
    public static String[] cardGamesErrorValues = new String[]{ERROR.name()};

}
