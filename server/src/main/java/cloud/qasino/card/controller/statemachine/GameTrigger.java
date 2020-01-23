package cloud.qasino.card.controller.statemachine;

import lombok.Getter;

import java.util.Set;

import static java.util.EnumSet.*;

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
