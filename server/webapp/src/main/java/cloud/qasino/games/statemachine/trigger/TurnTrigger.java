package cloud.qasino.games.statemachine.trigger;

import lombok.Getter;

import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum TurnTrigger {

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

    public static final Set<TurnTrigger> blackJackTurn = of(DEAL, SPLIT, LEAVE);
    public static final Set<TurnTrigger> highLowTurn = of(HIGHER, LOWER, PASS, LEAVE, NEXT);
    public static final Set<TurnTrigger> systemTurn = of(DETERMINE_WINNER, END_GAME);

    public static final Map<String, TurnTrigger> lookup
            = new HashMap<>();
    public static final Map<String, TurnTrigger> turnTriggerMapNoError
            = new HashMap<>();

    static {
        for (TurnTrigger turnTrigger : EnumSet.allOf(TurnTrigger.class))
            lookup.put(turnTrigger.getLabel(), turnTrigger);
    }

    static {
        for (TurnTrigger turnTrigger : EnumSet.allOf(TurnTrigger.class))
            if (!turnTrigger.getLabel().equalsIgnoreCase("error"))
                turnTriggerMapNoError.put(turnTrigger.getLabel(), turnTrigger);
    }

    public static TurnTrigger fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    @Transient
    private String label;

    TurnTrigger() {
        this.label = "error";
    }

    TurnTrigger(String label) {
        this();
        this.label = label;
    }
    public static TurnTrigger fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static TurnTrigger fromLabelWithDefault(String label) {
        TurnTrigger turnTrigger = fromLabel(label);
        if (turnTrigger == null) return TurnTrigger.ERROR;
        return turnTrigger;
    }

    public static TurnTrigger fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
}
