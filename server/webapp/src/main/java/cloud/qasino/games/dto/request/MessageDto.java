package cloud.qasino.games.dto.request;

import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class MessageDto {

    // FUNCTIONAL
    @JsonProperty("Message")
    private String action = "Empty";
    @JsonProperty("ShowMessage")
    private boolean actionNeeded = false;


    // EXCEPTION - - TODO move out of DTO
    // 400 bad request "malformed entity syntax" - eg null, zero, not numeric or invalid enum
    // 404 not found "unknown id" - eg id not in db
    // 409 conflict "update sent at the wrong time" eg state not valid now/anymore
    // 422 unprocessable "unable to process action" eg event not in correct order
    // @formatter:off
    @Setter(AccessLevel.NONE)
    private int httpStatus = 200;
    private String errorKey = "Key";
    private String errorValue = "Value";
    @Setter(AccessLevel.NONE)
    private String errorMessage = "";
    @Setter(AccessLevel.NONE)
    private String errorReason = "";

    public void setBadRequestErrorMessage(String problem) {
        this.errorMessage = "Supplied value for [" + this.errorKey + "] is [" + problem + "]";
        this.httpStatus = 400;
    }
    public void setNotFoundErrorMessage(String problem) {
        String defaultProblem = problem.isEmpty() ? "not found" : problem;
        this.errorMessage = "Supplied value for [" + this.errorKey + "] is [" + defaultProblem + "]";
        this.httpStatus = 400;
    }
    public void setConflictErrorMessage(String reason) {
        String defaultReason = reason.isEmpty() ? "Reason cannot be given" : reason;
        this.errorMessage = this.errorKey + " [" + this.errorValue + "] not valid now/anymore";
        this.errorReason = defaultReason;
        this.httpStatus = 409;
    }
    public void setUnprocessableErrorMessage(String reason) {
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
            case "turnEvent":
                if (!(TurnEvent.fromLabelWithDefault(value) == TurnEvent.ERROR)) {
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
                if (!Style.fromLabelWithDefault(value).equals(null)) {
                    return true;
                }
                break;
        }
        return false;
    }

}