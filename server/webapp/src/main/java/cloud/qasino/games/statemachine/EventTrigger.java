package cloud.qasino.games.statemachine;

import lombok.Getter;

import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum EventTrigger {

    // started - TurnResource calls StateMachine
    START, DEAL, DRAW, SPLIT, HIGHER, LOWER, STOP, // player events
    CRASH, WINNER, NO_CARDS_LEFT; // system events

    public static Set<EventTrigger> eventsBlackJack = of(DEAL, SPLIT, STOP);
    public static Set<EventTrigger> eventsHighLow = of(START, HIGHER, LOWER, STOP);
    public static Set<EventTrigger> eventsSystem = of(CRASH, WINNER, NO_CARDS_LEFT);

}
