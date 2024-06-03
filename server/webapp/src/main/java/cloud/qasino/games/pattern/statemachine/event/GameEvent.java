package cloud.qasino.games.pattern.statemachine.event;

import cloud.qasino.games.pattern.statemachine.event.interfaces.Event;
import jakarta.persistence.Transient;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.EnumSet.of;

@Getter
public enum GameEvent implements Event {

    //@formatter:off
    // CRUD - initiated by user
    SIGN_ON("sigoon Visitor"),
    GET_VISITOR("retrieve Visitor"),
    POST_VISITOR("create or update Visitor"),
    GET_GAME("retrieve Game"),
    GET_LEAGUE("retrieve League"),
    POST_LEAGUE("create or update League"),
    GET_PLAYER("retrieve Player"),
    POST_PLAYER("create or update Player"),

    // NEW & SETUP - initiated by user
    START("start a Game"),        // may not have initial bets
    INVITE("invite another Visitor"),
    ACCEPT("accept invitation for a Game"),     // with initial bet above game minimal ante

    // PREPARED - initiated by user
    VALIDATE("validate Game for playing"),    // do some updates and validate if playable
    SHUFFLE("shuffle and deal first Card"),       // add cards to the game

    // PLAYING initiated by user
    TURN("play Card(s)"),       // move some cards

    // STOP initiated by user
    STOP("stop the Game, no winner"),      // stop the game, no winner or results

    // specific triggers initiated by system
    WINNER("declare a winner"),     // end game and declare the winner
    ABANDON("abandoned Game"),    // game is abandonned
    ERROR("error Game");    // bad label or null supplied

    public static final Map<String, GameEvent> lookup
            = new HashMap<>();
    static {
        for (GameEvent gameEvent : EnumSet.allOf(GameEvent.class))
            lookup.put(String.valueOf(gameEvent).toLowerCase(), gameEvent);
    }

    public static final List<GameEvent> START_GAME_EVENTS = Arrays.asList(START);
    public static final List<GameEvent> SETUP_GAME_EVENTS = Arrays.asList(INVITE, ACCEPT, VALIDATE);
    public static final List<GameEvent> PREPARED_GAME_EVENTS = List.of(SHUFFLE);
    public static final List<GameEvent> PLAYING_GAME_EVENTS = List.of(TURN);
    public static final List<GameEvent> STOP_GAMES_EVENTS = Arrays.asList(STOP);
    public static final List<GameEvent> ERROR_GAME_EVENTS = Arrays.asList(WINNER, ABANDON, ERROR);
    public static final List<GameEvent> ALL_GAME_EVENTS =
            Arrays.asList(START, INVITE, ACCEPT, VALIDATE, SHUFFLE, TURN, STOP, WINNER, ABANDON, ERROR);

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
