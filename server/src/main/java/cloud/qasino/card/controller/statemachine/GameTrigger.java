package cloud.qasino.card.controller.statemachine;

import lombok.Getter;

import java.util.Set;

import static java.util.EnumSet.*;

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
    NEW, // may not have initial bets
    INVITE,
    ACCEPT, // with initial bet above game minimal ante
    PREPARE, // do some updates and validate if playable
    PLAY, // validate that initial bets are stated

    // started - EventResource calls StateMachine
    DEAL, HIGHER, LOWER, PASS,

    // ended
    WINNER,    // - internally by StateMachine
    LEAVE,   // User left - via GameController

    // error
    ABANDON, // - internally by batch job
    CRASH;   // - internally by StateMachine

    public static Set<GameTrigger> cardGamesTriggerNew = of(NEW, INVITE, ACCEPT, PLAY);
    public static Set<GameTrigger> cardGamesTriggerPlaying = of(DEAL, HIGHER, LOWER, PASS);
    public static Set<GameTrigger> cardGamesTriggerEnding = of(WINNER, LEAVE);
    public static Set<GameTrigger> cardGamesTriggerError = of(ABANDON, CRASH);

}
