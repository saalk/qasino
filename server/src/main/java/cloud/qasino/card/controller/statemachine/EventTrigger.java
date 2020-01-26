package cloud.qasino.card.controller.statemachine;

import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.Getter;

import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum EventTrigger {

    // started - EventResource calls StateMachine
    START, DEAL, SPLIT, HIGHER, LOWER, STOP;

    public static Set<EventTrigger> eventsBlackJack = of(DEAL, SPLIT, STOP);
    public static Set<EventTrigger> eventsHighLow = of(START, HIGHER, LOWER, STOP);

}
