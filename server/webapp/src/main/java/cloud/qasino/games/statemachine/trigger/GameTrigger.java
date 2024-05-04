package cloud.qasino.games.statemachine.trigger;

import lombok.Getter;

import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum GameTrigger {

    // SETUP - initiated by user
    START("start"),        // may not have initial bets
    INVITE("invite"),
    ACCEPT("accept"),     // with initial bet above game minimal ante
    VALIDATE("validate"),    // do some updates and validate if playable

    // PLAY
    SHUFFLE("shuffle"),       // add cards to the game
    TURN("turn"),       // move some cards

    // specific triggers are with the event trigger
    // FINISHED
    WINNER("winner"),     // end game and declare the winner
    STOP("stop"),      // stop the game, no winner or results

    // ERROR
    ABANDON("abandon"),    // game is abandonned
    ERROR("error");    // bad label or null supplied

    public static final Map<String, GameTrigger> lookup
            = new HashMap<>();
    public static final Map<String, GameTrigger> gameTriggerMapNoError
            = new HashMap<>();

    public static final Set<GameTrigger> setupGameTriggers = of(START, INVITE, ACCEPT, VALIDATE);
    public static final Set<GameTrigger> playingGameTriggers = of(SHUFFLE);
    public static final Set<GameTrigger> finishedGamesTriggers = of(WINNER, STOP);
    public static final Set<GameTrigger> errorGameTriggers = of(ABANDON, ERROR);

    public static final Set<GameTrigger> allGameTriggers = EnumSet.of(START, INVITE, ACCEPT, VALIDATE, SHUFFLE, WINNER,
            STOP,ABANDON, ERROR);

    static {
        for (GameTrigger gameTrigger : EnumSet.allOf(GameTrigger.class))
            lookup.put(gameTrigger.getLabel(), gameTrigger);
    }

    static {
        for (GameTrigger gameTrigger : EnumSet.allOf(GameTrigger.class))
            if (!gameTrigger.getLabel().equalsIgnoreCase("error"))
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
