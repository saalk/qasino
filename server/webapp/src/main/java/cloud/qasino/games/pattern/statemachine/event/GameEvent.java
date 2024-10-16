package cloud.qasino.games.pattern.statemachine.event;

import cloud.qasino.games.pattern.statemachine.event.interfaces.Event;
import jakarta.persistence.Transient;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum GameEvent implements Event {

    //@formatter:off

    // NEW & SETUP - initiated by visitor
    START("start a Game"),        // may not have initial bets
    ADD_INVITEE("invite for other Visitor(s)"),
    ADD_BOT("invite a Bot"),

    // PREPARED - validation by visitor
    VALIDATE("validate Game for playing"),  // no pending invitations
    SHUFFLE("shuffle and deal first Card"), // add cards to the game

    // PLAYING play event by player
    PLAY("play Card(s) in the Game"),       // move some cards

    // STOP game stopped by player
    STOP("stop the Game, elect no winner"),      // stop the game, no winner or results

    // specific triggers initiated by system
    WINNER("end the Game and declare a winner"),     // end game and declare the winner
    ABANDON("abandoned the Game without a winner"),    // game is abandonned
    NONE("starting a Game not possible"),    // game is not possible
    ERROR("Game in error");    // bad label or null supplied

    public static final Map<String, GameEvent> lookup
            = new HashMap<>();
    static {
        for (GameEvent gameEvent : EnumSet.allOf(GameEvent.class))
            lookup.put(String.valueOf(gameEvent).toLowerCase(), gameEvent);
    }

    public static final List<GameEvent> START_GAME_EVENTS = List.of(START);
    public static final List<GameEvent> SETUP_GAME_EVENTS = Arrays.asList(ADD_INVITEE, VALIDATE);
    public static final List<GameEvent> PREPARED_GAME_EVENTS = List.of(SHUFFLE);
    public static final List<GameEvent> PLAYING_GAME_EVENTS = List.of(PLAY);
    public static final List<GameEvent> STOP_GAMES_EVENTS = List.of(STOP);
    public static final List<GameEvent> ERROR_GAME_EVENTS = Arrays.asList(WINNER, ABANDON, ERROR);
    public static final List<GameEvent> ALL_GAME_EVENTS =
            Arrays.asList(START, ADD_INVITEE, VALIDATE, SHUFFLE, PLAY, STOP, WINNER, ABANDON, ERROR);

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
