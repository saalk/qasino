package cloud.qasino.games.pattern.statemachine.event;

import cloud.qasino.games.pattern.statemachine.event.interfaces.Event;
import jakarta.persistence.Transient;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum PlayEvent implements Event {

    // highlow
    HIGHER("higher"),
    LOWER("lower"),
    PASS("pass"), // give round to next player
    BOT("bot"), // only for bot player

    // blackjack,
    DEAL("deal"),
    SPLIT("split"),
    STAND("stand"),

    // generic,
    LEAVE("leave"), // leave as a invited player - rest can continue

    // technical internal events
    ERROR("error"),
    DETERMINE_WINNER("determine_winner"),
    END_GAME("end_game"),
    ; // system events

    public static final List<PlayEvent> blackJackPossibleHumanGamingTable = Arrays.asList(DEAL, SPLIT);
    public static final List<PlayEvent> blackJackPossibleBotGamingTable = List.of(BOT);
    public static final List<PlayEvent> highLowPossibleHumanGamingTables = Arrays.asList(HIGHER, LOWER, PASS);
    public static final List<PlayEvent> highLowPossibleBotGamingTables = List.of(BOT);
    public static final List<PlayEvent> systemGamingTable = Arrays.asList(DETERMINE_WINNER, END_GAME);

    public static final Map<String, PlayEvent> lookup
            = new HashMap<>();

    static {
        for (PlayEvent playEvent : EnumSet.allOf(PlayEvent.class))
            lookup.put(playEvent.getLabel(), playEvent);
    }

    public static PlayEvent fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    @Transient
    private String label;

    PlayEvent() {
        this.label = "error";
    }

    PlayEvent(String label) {
        this();
        this.label = label;
    }

    public static PlayEvent fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static PlayEvent fromLabelWithDefault(String label) {
        PlayEvent playEvent = fromLabel(label);
        if (playEvent == null) return PlayEvent.ERROR;
        return playEvent;
    }

    public static PlayEvent fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
}
