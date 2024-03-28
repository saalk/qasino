package cloud.qasino.games.statemachine;

import lombok.Getter;

import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum GameTrigger {

    // new - initiated by user
    NEW("new"),        // may not have initial bets
    INVITE("invite"),
    ACCEPT("accept"),     // with initial bet above game minimal ante
    PREPARE("prepare"),    // do some updates and validate if playable

    // playing
    PLAY("play"),       // validate that initial bets are stated

    // ended
    WINNER("winner"),     // we have a winner
    LEAVE("leave"),      // User left - via GameController

    // ended - initiated by system
    OK("ok"),         // result ok
    NOT_OK("not_ok"),     // result nok

    // error
    ABANDON("abandon"),    // game is abandonned
    CRASH("crash"),       // error 500
    ERROR("error");    // bad label or null supplied

    public static final Map<String, GameTrigger> lookup
            = new HashMap<>();
    public static final Map<String, GameTrigger> gameTriggerMapNoError
            = new HashMap<>();

    public static Set<GameTrigger> gamesTriggerNew = of(NEW, INVITE, ACCEPT, PREPARE);
    public static Set<GameTrigger> gamesTriggerPlaying = of(PLAY);
    public static Set<GameTrigger> gamesTriggerEnding = of(WINNER, LEAVE);
    public static Set<GameTrigger> gamesTriggerError = of(ABANDON, CRASH);

    public static Set<GameTrigger> gameTriggers = EnumSet.of(NEW, INVITE, ACCEPT, PREPARE, PLAY, WINNER,
            LEAVE, OK, NOT_OK, ABANDON, CRASH, ERROR);

    static {
        for (GameTrigger gameTrigger : EnumSet.allOf(GameTrigger.class))
            lookup.put(gameTrigger.getLabel(), gameTrigger);
    }

    static {
        for (GameTrigger gameTrigger : EnumSet.allOf(GameTrigger.class))
            if (!gameTrigger.getLabel().toLowerCase().equals("error"))
                gameTriggerMapNoError.put(gameTrigger.getLabel(), gameTrigger);
    }

    @Transient
    private String label;

    GameTrigger() {
        this.label = "error";
    }

    GameTrigger(String label) {
        this();
        this.label = label;
    }

    public static GameTrigger fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static GameTrigger fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static GameTrigger fromLabelWithDefault(String label) {
        GameTrigger gameTrigger = fromLabel(label);
        if (gameTrigger == null) return GameTrigger.ERROR;
        return gameTrigger;
    }

    public static GameTrigger fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }


}
