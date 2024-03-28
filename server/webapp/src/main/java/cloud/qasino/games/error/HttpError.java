package cloud.qasino.games.error;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum HttpError {

    BAD_REQUEST_GAMETYPE("400", "Please supply correct 'gameType' in your move to 'init' your cardgame."),
    BAD_REQUEST_ANTE("400", "Please supply a normal 'ante' in your move to 'init' your cardgame."),
    BAD_REQUEST_HUMAN_UserName("400", "Please supply a normal 'userName' in your move to 'setup' a human player in your cardgame."),
    BAD_REQUEST_HUMAN_AILEVEL("400", "Please supply the 'Human' aiLevel in your move to 'setup' a human player in your cardgame."),
    BAD_REQUEST_AI_AILEVEL("400", "Please do not supply the 'Human' aiLevel in your move to 'setup' a ai player in your cardgame."),

    UNAUTHORIZED("401", "Please supply correct credentials to play the cardgame."),
    FORBIDDEN("403", "Sorry, you don't have permission to play this cardgame."),

    NOT_FOUND_CARDGAME("404", "Please supply an existing id for the cardgame you want to 'init'"),
    NOT_FOUND_PLAYER("404", "Please supply an existing id for the player of your cardgame"),
    NOT_FOUND_CARD("404", "Please supply a valid playingcard for the aciton you want to play"),

    METHOD_NOT_ALLOWED("405", "Please supply a correct move that is in sync with the state of your cardgame."),
    CODE_CONFLICT("409", "Please supply a correct suppliedMove, this suppliedMove is not possible at this moment."),
    PRECONDITION_FAILED("412", "Please check the rules for the cardgame, this move and moves are not possible."),

    INTERNAL_SERVER_ERROR("500", "Internal server error, please try again.");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    private static final Map<Integer, HttpError> lookup
            = new HashMap<>();

    static {
        for (HttpError httpError : EnumSet.allOf(HttpError.class))
            lookup.put(Integer.valueOf(httpError.getLabel()), httpError);
    }

    private String label;
    private String message;

    HttpError() {
    }

    HttpError(String label, String message) {
        this();
        this.label = label;
        this.message = message;
    }

    public static HttpError fromLabel(Integer label) {
        return lookup.get(label);
    }


}