package cloud.qasino.card.domain.qasino.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

/**
 * <H1>CardGame</H1> A selection of card gameDtos that can be selected to play. <p> More gameDtos will be
 * added in future.
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */
@Getter
public enum GameTrigger {

    // new - GameController
    NEW,
    INVITE,
    ACCEPT,
    PREPARE,

    // started - EventController calls StateMachine
    DEAL, HIGHER, LOWER, PASS,

    // ended
    WINNER,    // - internally by StateMachine
    LEAVE,   // User left - via GameController

    // error
    ABANDON, // - internally by batch job
    CRASH;   // - internally by StateMachine

    public static Set<GameTrigger> cardGamesTriggerNew = EnumSet.of(NEW, INVITE, ACCEPT, PREPARE);
    public static Set<GameTrigger> cardGamesTriggerPlaying = EnumSet.of(DEAL, HIGHER, LOWER, PASS);
    public static Set<GameTrigger> cardGamesTriggerEnding = EnumSet.of(WINNER, LEAVE);
    public static Set<GameTrigger> cardGamesTriggerError = EnumSet.of(ABANDON, CRASH);

}
