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
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.statemachine.event.GameEvent;
import cloud.qasino.games.statemachine.event.TurnEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Data
@Getter
@Setter
@Slf4j
public class QasinoRequestValidator {

    // FRONTEND
    // path params
    private long suppliedVisitorId;
    private long suppliedGameId;
    private long suppliedLeagueId;
    private long initiatingPlayerId;
    private long invitedPlayerId;
    private long acceptedPlayerId;
    private long suppliedTurnPlayerId;
    // triggers for the Game
    private GameEvent suppliedGameEvent;
    private TurnEvent suppliedTurnEvent;
    private GameStateGroup suppliedGameStateGroup;
    // Triggers for playing a Game
    private Move suppliedMove;
    private List<Card> suppliedCards;

    // FRONTEND request params
    // paging
    private int suppliedPage = 1;
    private int suppliedMaxPerPage = 4;
    // visitor
    private String suppliedUsername;
    private String suppliedEmail;
    public boolean requestingToRepay = false;
    public boolean offeringShipForPawn = false;
    // league
    private String suppliedLeagueName;
    private String suppliedLeagueEnd;    // todo monthsEnd, thisMonday, x days.
    private Boolean suppliedLeagueClose; // todo null, or true/false
    // player
    private Role suppliedRole; // bot, initiator or guest
    private int suppliedFiches;
    private Avatar suppliedAvatar;
    private AiLevel suppliedAiLevel; // human or ai
    // game
    private Type suppliedType; // highlow
    private String suppliedStyle; // maxrounds etc
    private int suppliedAnte; // inleg
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

    // INPUT // TODO move out of DTO
    @Setter(AccessLevel.NONE)
    private Map<String, String> pathVariables = new HashMap<>();
    private Map<String, String> requestParams = new HashMap<>();

    public void setPathVariables(String... pathVariables) {
        if (pathVariables == null) return;
        if (pathVariables.length % 2 != 0) return;
        for (int i = 0; i < pathVariables.length; i = i + 2) {
            this.pathVariables.put(pathVariables[i], pathVariables[i + 1]);
        }
    }

    // VALIDATE INPUT - TODO move out of DTO
    // @formatter:off
    public boolean validate(QasinoRequest request) {
        if (!validatePathVariables(request.getPathVariables())
                | !validateRequestParams(request.getRequestParams())) {
            return false;
        }
        return true;
    }
    boolean validatePathVariables(Map<String, String> pathVariables) {
        String key;
        String dataName = "pathVariables";
        String pathDataString = StringUtils.join(pathVariables);
//        log.warn(this.getClass().getName() + ": " + dataName + " is " + pathDataString);

        if (pathVariables == null) return true;

        key = "gameId";
        if (pathVariables.containsKey(key)) {
            if (isValueForPrimaryKeyValid(key, pathVariables.get(key), dataName, pathDataString)) {
                this.suppliedGameId = Long.parseLong(pathVariables.get(key));
            } else {
                return false;
            }
        }

        key = "visitorId";
        if (pathVariables.containsKey(key)) {
            if (isValueForPrimaryKeyValid(key, pathVariables.get(key), dataName, pathDataString)) {
                this.suppliedVisitorId = Long.parseLong(pathVariables.get(key));
            } else {
                return false;
            }
        }

        key = "leagueId";
        if (pathVariables.containsKey(key)) {
            if (isValueForPrimaryKeyValid(key, pathVariables.get(key), dataName, pathDataString)) {
                this.suppliedLeagueId = Long.parseLong(pathVariables.get(key));
            } else {
                return false;
            }
        }
        key = "invitedPlayerId";
        if (pathVariables.containsKey(key)) {
            if (isValueForPrimaryKeyValid(key, pathVariables.get(key), dataName, pathDataString)) {
                this.invitedPlayerId = Long.parseLong(pathVariables.get(key));
            } else {
                return false;
            }
        }
        key = "acceptedPlayerId";
        if (pathVariables.containsKey(key)) {
            if (isValueForPrimaryKeyValid(key, pathVariables.get(key), dataName, pathDataString)) {
                this.suppliedLeagueId = Long.parseLong(pathVariables.get(key));
            } else {
                return false;
            }
        }
        key = "turnPlayerId";
        if (pathVariables.containsKey(key)) {
            if (isValueForPrimaryKeyValid(key, pathVariables.get(key), dataName, pathDataString)) {
                this.suppliedTurnPlayerId = Long.parseLong(pathVariables.get(key));
            } else {
                return false;
            }
        }

        // call param data with path data to be sure
        return validateRequestParams(pathVariables);

    }
    boolean validateRequestParams(Map<String, String> requestParam) {

        String key;
        String dataName = "requestParam";
        String paramDataString = StringUtils.join(requestParam);
//        log.warn(this.getClass().getName() + ": " + dataName + " is " + paramDataString);

        if (requestParam == null) return true;

        // paging
        key = "pageNumber";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                if (Integer.parseInt(requestParam.get(key)) < 1) {
                    this.setErrorKey(key);
                    this.setErrorValue(errorValue);
                    setBadRequestErrorMessage("Less than 1");
                    return false;
                }
                this.suppliedPage = Integer.parseInt(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "pageSize";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                this.suppliedMaxPerPage = Integer.parseInt(requestParam.get(key));
            } else {
                return false;
            }
        }
        // visitor
        key = "username";
        if (requestParam.containsKey(key)) {
            this.suppliedUsername = (requestParam.get("username"));
        }
        key = "email";
        if (requestParam.containsKey(key)) {
            this.suppliedEmail = (requestParam.get("email"));
        }
        key = "pawn";
        if (requestParam.containsKey(key)) {
            this.offeringShipForPawn = true;
        }
        key = "repay";
        if (requestParam.containsKey(key)) {
            this.requestingToRepay = true;
        }
        // player
        key = "role";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedRole = Role.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "fiches";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                this.suppliedFiches = Integer.parseInt(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "avatar";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedAvatar = Avatar.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "aiLevel";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedAiLevel = AiLevel.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        // game
        key = "gameEvent";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedGameEvent = GameEvent.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        // turn
        key = "turnEvent";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedTurnEvent = TurnEvent.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "gameStateGroup";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedGameStateGroup = GameStateGroup.valueOf(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "type";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedType = Type.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "style";
        if (requestParam.containsKey(key)) {

            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedStyle = requestParam.get("style");
            } else {
                return false;
            }
        }
        key = "ante";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                if (Integer.parseInt(requestParam.get(key)) == 0) {
                    this.setErrorKey(key);
                    this.setErrorValue(requestParam.get(key));
                    setBadRequestErrorMessage("Zero");
                    return false;
                }
                this.suppliedAnte = Integer.parseInt(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "jokers";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                this.suppliedJokers = Integer.parseInt(requestParam.get(key));
            } else {
                return false;
            }
        }
        // league
        key = "leagueName";
        if (requestParam.containsKey(key)) {
            this.suppliedLeagueName = (requestParam.get("leagueName"));
        }
        // cardmove
        key = "move";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedMove = Move.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "location";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedLocation = Location.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "bet";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                this.suppliedBet = Integer.parseInt(requestParam.get(key));
            } else {
                return false;
            }
        }
        return true;
    }

    boolean isValueForPrimaryKeyValid(String key, String value, String dataName, String dataToValidate) {
        if (!StringUtils.isNumeric(value)) {
            this.setErrorKey(key);
            this.setErrorValue(value);
            setBadRequestErrorMessage("Not numeric");
            return false;
        }
        if (Long.parseLong(value) == 0) {
            this.setErrorKey(key);
            this.setErrorValue(value);
            setBadRequestErrorMessage("Zero");
            return false;
        }

        return true;
    }
    boolean isValueForIntKeyValid(String key, String value, String dataName, String dataToValidate) {
        if (!StringUtils.isNumeric(value)) {
            this.setErrorKey(key);
            this.setErrorValue(value);
            setBadRequestErrorMessage("Not numeric");
            return false;
        }
        return true;
    }
    boolean isValueForEnumKeyValid(String key, String value, String dataName, String dataToValidate) {

        switch (key) {

            case "role":
                if (!(Role.fromLabelWithDefault(value) == Role.ERROR)) {
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

        this.setErrorKey(key);
        this.setErrorValue(value);
        setBadRequestErrorMessage("No valid emun");
        return false;
    }
    // @formatter:on

}