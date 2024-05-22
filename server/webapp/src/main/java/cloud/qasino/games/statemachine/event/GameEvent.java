package cloud.qasino.games.statemachine.event;

import cloud.qasino.games.statemachine.event.interfaces.Event;
import jakarta.persistence.Transient;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.EnumSet.of;

@Getter
public enum GameEvent implements Event {

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

    public static final Map<String, GameEvent> lookup
            = new HashMap<>();
    public static final Map<String, GameEvent> gameEventsPossible
            = new HashMap<>();

    public static final Set<GameEvent> SETUP_GAME_EVENTS = of(START, INVITE, ACCEPT, VALIDATE);
    public static final Set<GameEvent> PLAYING_GAME_EVENTS = of(SHUFFLE);
    public static final Set<GameEvent> finishedGamesTriggers = of(WINNER, STOP);
    public static final Set<GameEvent> ERROR_GAME_EVENTS = of(ABANDON, ERROR);

    public static final Set<GameEvent> ALL_GAME_EVENTS = EnumSet.of(START, INVITE, ACCEPT, VALIDATE, SHUFFLE, WINNER,
            STOP, ABANDON, ERROR);

    static {
        for (GameEvent gameEvent : EnumSet.allOf(GameEvent.class))
            lookup.put(gameEvent.getLabel(), gameEvent);
    }

    static {
        for (GameEvent gameEvent : EnumSet.allOf(GameEvent.class))
            if (!(gameEvent == GameEvent.ERROR
                    || gameEvent == GameEvent.ABANDON
                    || gameEvent == GameEvent.INVITE
                    || gameEvent == GameEvent.ACCEPT
                    || gameEvent == GameEvent.WINNER))
                gameEventsPossible.put(gameEvent.getLabel(), gameEvent);
    }

    @Transient
    private String label;

    GameEvent() {
        this.label = "error";
    }

    GameEvent(String label) {
        this();
        this.label = label;
    }

    public static GameEvent fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static GameEvent fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static GameEvent fromLabelWithDefault(String label) {
        GameEvent gameEvent = fromLabel(label);
        if (gameEvent == null) return GameEvent.ERROR;
        return gameEvent;
    }

    public static GameEvent fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }


}
