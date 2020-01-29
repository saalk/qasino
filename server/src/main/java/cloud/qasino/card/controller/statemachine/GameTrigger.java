package cloud.qasino.card.controller.statemachine;

import lombok.Getter;

import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum GameTrigger {

    // new - initiated by user
    NEW,        // may not have initial bets
    INVITE,
    ACCEPT,     // with initial bet above game minimal ante
    PREPARE,    // do some updates and validate if playable
    PLAY,       // validate that initial bets are stated
    // ended
    LEAVE,      // User left - via GameController

    // ended - initiated by system
    OK,         // result ok
    NOT_OK,     // result nok
    WINNER,     // we have a winner

    // error
    ABANDON,    // game is abandonned
    CRASH;      // error 500

    public static Set<GameTrigger> cardGamesTriggerNew = of(NEW, INVITE, ACCEPT, PLAY);
    public static Set<GameTrigger> cardGamesTriggerPlaying = of(PLAY);
    public static Set<GameTrigger> cardGamesTriggerEnding = of(WINNER, LEAVE);
    public static Set<GameTrigger> cardGamesTriggerError = of(ABANDON, CRASH);

}
