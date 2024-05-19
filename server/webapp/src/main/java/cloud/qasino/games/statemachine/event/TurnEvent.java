package cloud.qasino.games.statemachine.event;

import cloud.qasino.games.statemachine.event.interfaces.Event;
import lombok.Getter;

import jakarta.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum TurnEvent implements Event {

    // highlow
    HIGHER("higher"),
    LOWER("lower"),
    PASS("pass"),
    NEXT("next"),

    // blackjack
    DEAL("deal"),
    SPLIT("split"),

    // generic
    LEAVE("leave"),

    // technical
    ERROR("error"),
    DETERMINE_WINNER("determine_winner"),
    END_GAME("end_game"),; // system events

    public static final Set<TurnEvent> blackJackTurn = of(DEAL, SPLIT, LEAVE);
    public static final Set<TurnEvent> highLowTurn = of(HIGHER, LOWER, PASS, LEAVE, NEXT);
    public static final Set<TurnEvent> systemTurn = of(DETERMINE_WINNER, END_GAME);

    public static final Map<String, TurnEvent> lookup
            = new HashMap<>();
    public static final Map<String, TurnEvent> turnEventMapNoError
            = new HashMap<>();

    static {
        for (TurnEvent turnEvent : EnumSet.allOf(TurnEvent.class))
            lookup.put(turnEvent.getLabel(), turnEvent);
    }

    static {
        for (TurnEvent turnEvent : EnumSet.allOf(TurnEvent.class))
            if (!turnEvent.getLabel().equalsIgnoreCase("error"))
                turnEventMapNoError.put(turnEvent.getLabel(), turnEvent);
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
