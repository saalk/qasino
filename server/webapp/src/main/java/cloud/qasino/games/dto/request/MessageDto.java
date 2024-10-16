package cloud.qasino.games.dto.request;

import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.pattern.singleton.OnlineVisitorsPerDay;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.Data;

@Data
public class MessageDto {

    // FUNCTIONAL
    private String action;
    public void setAction(String action) {
        this.action = "today's visitor count = " + OnlineVisitorsPerDay.getInstance().getOnlineVisitors() + " | " + action;
    }
    private boolean actionNeeded = false;


    // EXCEPTION HANDLING
    // 400 bad request "malformed entity syntax" - eg null, zero, not numeric or invalid enum
    // 404 not found "unknown id" - eg id not in db
    // 409 conflict "update sent at the wrong time" eg state not valid now/anymore
    // 422 unprocessable "unable to process action" eg event not in correct order
    // @formatter:off
    private int httpStatus = 200;
    private String errorMessage = "All is processed correctly";
    private String errorReason = "";

    private String errorKey = "Key";
    private String errorValue = "Value";

    public void setBadRequestErrorMessage(String key, String value, String problem) {
        setErrorKey(key);
        setErrorValue(value);
        this.errorMessage = "Supplied value for [" + this.errorKey + "] is [" + problem + "]";
        this.httpStatus = 400;
    }
    public void setNotFoundErrorMessage(String key, String value, String problem) {
        setErrorKey(key);
        setErrorValue(value);
        String defaultProblem = (problem == null || problem.isEmpty()) ? "not found" : problem;
        this.errorMessage = "Supplied value for [" + this.errorKey + "] is [" + defaultProblem + "]";
        this.httpStatus = 404;
    }
    public void setConflictErrorMessage(String key, String value, String reason) {
        setErrorKey(key);
        setErrorValue(value);
        String defaultReason = reason.isEmpty() ? "Reason cannot be given" : reason;
        this.errorMessage = this.errorKey + " [" + this.errorValue + "] not valid now/anymore";
        this.errorReason = defaultReason;
        this.httpStatus = 409;
    }
    public void setUnprocessableErrorMessage(String key, String value, String reason) {
        setErrorKey(key);
        setErrorValue(value);
        String defaultReason = reason.isEmpty() ? "Reason cannot be given" : reason;
        this.errorMessage = this.errorKey + " [" + this.errorValue + "] cannot be processed";
        this.errorReason = defaultReason;
        this.httpStatus = 422;
    }

    // @formatter:on
    boolean isValueForEnumKeyValid(String key, String value, String dataName, String dataToValidate) {

        switch (key) {

            case "role":
                if (!(PlayerType.fromLabelWithDefault(value) == PlayerType.ERROR)) {
                    return true;
                }
                break;
            case "avatar":
                if (!(Avatar.fromLabelWithDefault(value) == Avatar.ERROR)) {
                    return true;
                }
                break;
            case "aiLevel":
                if (!(AiLevel.fromLabelWithDefault(value) == AiLevel.ERROR)) {
                    return true;
                }
                break;
            case "gameEvent":
                if (!(GameEvent.fromLabelWithDefault(value) == GameEvent.ERROR)) {
                    return true;
                }
                break;
            case "playEvent":
                if (!(PlayEvent.fromLabelWithDefault(value) == PlayEvent.ERROR)) {
                    return true;
                }
                break;
            case "gameStateGroup":
                if (!(GameStateGroup.fromLabelWithDefault(value) == GameStateGroup.ERROR)) {
                    // todo ERROR can be a valid state but for now is bad request coming from client
                    return true;
                }
                break;
            case "type":
                if (!(Type.fromLabelWithDefault(value) == Type.ERROR)) {
                    return true;
                }
                break;
            case "move":
                if (!(Move.fromLabelWithDefault(value) == Move.ERROR)) {
                    return true;
                }
                break;
            case "location":
                if (!(Location.fromLabelWithDefault(value) == Location.ERROR)) {
                    return true;
                }
                break;
            case "position":
                if (!(Position.fromLabelWithDefault(value) == Position.ERROR)) {
                    return true;
                }
                break;
            case "face":
                if (!(Face.fromLabelWithDefault(value) == Face.ERROR)) {
                    return true;
                }
                break;
            case "style":
                // TODO never in error due to a default
                Style.fromLabelWithDefault(value);
                return true;
        }
        return false;
    }

}