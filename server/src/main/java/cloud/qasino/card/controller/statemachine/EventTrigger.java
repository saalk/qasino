package cloud.qasino.card.controller.statemachine;

import lombok.Getter;

import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum EventTrigger {

    // started - EventResource calls StateMachine
    START, DEAL, SPLIT, HIGHER, LOWER, STOP, // player events
    CRASH, WINNER, NO_CARDS_LEFT; // system events

    public static Set<EventTrigger> eventsBlackJack = of(DEAL, SPLIT, STOP);
    public static Set<EventTrigger> eventsHighLow = of(START, HIGHER, LOWER, STOP);
    public static Set<EventTrigger> eventsSystem = of(CRASH, WINNER, NO_CARDS_LEFT);

}
