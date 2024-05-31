package cloud.qasino.games.statemachine.event;

import cloud.qasino.games.statemachine.event.interfaces.Event;
import lombok.Getter;

import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum TurnEvent implements Event {

    // highlow
    HIGHER("higher"),
    LOWER("lower"),

    // blackjack,
    DEAL("deal"),
    SPLIT("split"),
    STAND("stand"),

    // generic,
    NEXT("next"), // only for bot player
    PASS("pass"),
    LEAVE("leave"),

    // technical internal events
    ERROR("error"),
    DETERMINE_WINNER("determine_winner"),
    END_GAME("end_game"),; // system events

    public static final List<TurnEvent> blackJackPossibleHumanTurn = Arrays.asList(DEAL, SPLIT, LEAVE);
    public static final List<TurnEvent> blackJackPossibleBotTurn = List.of(NEXT);
    public static final List<TurnEvent> highLowPossibleHumanTurns = Arrays.asList(HIGHER, LOWER, PASS, LEAVE);
    public static final List<TurnEvent> highLowPossibleBotTurns = List.of(NEXT);
    public static final List<TurnEvent> systemTurn = Arrays.asList(DETERMINE_WINNER, END_GAME);

    public static final Map<String, TurnEvent> lookup
            = new HashMap<>();

    static {
        for (TurnEvent turnEvent : EnumSet.allOf(TurnEvent.class))
            lookup.put(turnEvent.getLabel(), turnEvent);
    }

    public static TurnEvent fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    @Transient
    private String label;

    TurnEvent() {
        this.label = "error";
    }

    TurnEvent(String label) {
        this();
        this.label = label;
    }
    public static TurnEvent fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static TurnEvent fromLabelWithDefault(String label) {
        TurnEvent turnEvent = fromLabel(label);
        if (turnEvent == null) return TurnEvent.ERROR;
        return turnEvent;
    }

    public static TurnEvent fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
}
