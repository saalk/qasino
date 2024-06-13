package cloud.qasino.games.request;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerState;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

//@Data
@Getter
@Setter
@Slf4j
public class CreationDto {

    // @formatter:off

    // triggers qasino
    private QasinoEvent suppliedQasinoEvent;

    // triggers game flow
    private GameEvent suppliedGameEvent;

    // triggers turn flow + details
    private TurnEvent suppliedTurnEvent;
    private List<Card> suppliedCards;

    // paging
    @Max(value = 5, message = "max 5 pages")
    private int suppliedPage = 1;
    @Max(value = 5, message = "max 5 rows")
    private int suppliedMaxPerPage = 4;

    // visitor
    @NotEmpty(message = "Username empty")
    private String suppliedUsername;
    @Email(message = "Invalid email")
    private String suppliedEmail;
    @NotEmpty(message = "Password empty")
    private String suppliedPassword;
    @NotEmpty(message = "Alias empty")
    private String suppliedAlias ;

    // league
    @NotEmpty(message = "League name empty")
    private String suppliedLeagueName;

    // player
    @NotEmpty(message = "Player state is empty")
    private PlayerState suppliedPlayerState; // bot, initiator or guest
    private Avatar suppliedAvatar;
    private AiLevel suppliedAiLevel;

    // game
    private String suppliedStyle;
    private Type suppliedType;
    @Min(value = 5, message = "min 5 ante")
    private int suppliedAnte;
    @Max(value = 3, message = "max 3 jokers")
    private int suppliedJokers;

    // cardMove
    private Location suppliedLocation;
    private int suppliedBet;

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
    // @formatter:on
    boolean isValueForEnumKeyValid(String key, String value, String dataName, String dataToValidate) {

        switch (key) {

            case "role":
                if (!(PlayerState.fromLabelWithDefault(value) == PlayerState.ERROR)) {
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