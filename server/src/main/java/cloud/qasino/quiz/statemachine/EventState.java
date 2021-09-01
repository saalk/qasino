package cloud.qasino.quiz.statemachine;

import cloud.qasino.quiz.entity.enums.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum EventState implements LabeledEnum {

    // new
    NEW("new", "New Turn started"),
    DEALT("dealt", "PlayingQuiz dealt to player"),

    DREW("drew", "Player drew a playingquiz"),
    DOUBLE("double", "Player doubled his playingquiz"),
    HIGHER("higher", "Player decided higher"),
    LOWER("lower", "Player decided lower"),

    ERROR("lower", "Player decided lower"),
    FINISHED("finished", "Game finished"),
    INIT("lower", "Game has not events yes");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static Map<String, EventState> lookup
            = new HashMap<>();

    static {
        for (EventState gameState : EnumSet.allOf(EventState.class))
            lookup.put(gameState.getLabel(), gameState);
    }

    public static final Map<String, EventState> gameStates
            = new HashMap<>();

    static {
        for (EventState gameState : EnumSet.allOf(EventState.class))
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

    public static Set<EventState> quizGameHighLow = EnumSet.of(INIT, NEW, HIGHER, LOWER);
    public static String[] quizGamesHighLowValues = new String[]{INIT.name(), NEW.name(),
            HIGHER.name(), LOWER.name()};

    public static Set<EventState> quizGamesBlackJack = EnumSet.of(INIT, NEW, DEALT);
    public static String[] quizGamesBlackJackValues = new String[]{INIT.name(), NEW.name(), DEALT.name()};

    public static Set<EventState> quizGamesError = EnumSet.of(ERROR);
    public static String[] quizGamesErrorValues = new String[]{ERROR.name()};

}
