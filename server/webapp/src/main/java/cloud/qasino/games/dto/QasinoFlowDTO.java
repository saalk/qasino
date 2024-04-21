package cloud.qasino.games.dto;

import cloud.qasino.games.action.*;
import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.dto.statistics.Counter;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Data
@Getter
@Setter
@Slf4j
public class QasinoFlowDTO //extends AbstractFlowDTO
        implements
        FindVisitorIdByVisitorNameAction.FindVisitorIdByVisitorNameActionDTO,
        SignUpNewVisitorAction.SignUpNewVisitorActionDTO,
        FindAllEntitiesForInputAction.FindAllEntitiesForInputActionDTO,
        CalculateHallOfFameAction.CalculateHallOfFameActionDTO,
        HandleSecuredLoanAction.HandleSecuredLoanActionDTO,
        SetStatusIndicatorsBaseOnRetrievedDataAction.SetStatusIndicatorsBaseOnRetrievedDataDTO,
        MapQasinoResponseFromRetrievedDataAction.MapQasinoResponseFromRetrievedDataDTO

{
    // suppress lombok setter for these fixed values
    @Setter(AccessLevel.NONE)
    private String applicationName = "qasino";

    // gui
    private Qasino qasino;
    private Counter counter;


    // FRONTEND path params
    private long suppliedVisitorId;
    private long suppliedGameId;

    private long suppliedLeagueId;

    private long initiatingPlayerId;
    private long invitedPlayerId;
    private long acceptedPlayerId;
    private long suppliedTurnPlayerId;

    // Triggers for the Game
    private GameTrigger suppliedTrigger;
    private GameStateGroup suppliedGameStateGroup;

    // Triggers while playing a Game
    private Move suppliedMove;
    private List<Card> suppliedCards;   // todo

    // FRONTEND request and or main path params

    // paging
    private int suppliedPages = 0;
    private int suppliedMaxPerPage = 4;
    // visitor
    private String suppliedVisitorName;
    private String suppliedEmail;
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

    // pawn or repay
    public boolean requestingToRepay = false;
    public boolean offeringShipForPawn = false;

    // RETRIEVED DATA BASED ON FRONTEND ID's

    // the game
    private Game qasinoGame;
    private List<Player> qasinoGamePlayers;

    // during game state changes
    private Visitor qasinoVisitor;
    private List<Visitor> friends;

    private List<Game> newGamesForVisitor;
    private List<Game> startedGamesForVisitor;
    private List<Game> finishedGamesForVisitor;

    private Player invitedPlayer;
    private Player acceptedPlayer;
    private Player initiatingPlayer;

    private League qasinoGameLeague;
    private List<League> leaguesForVisitor;

    private Result gameResult;
    private List<Result> resultsForLeague;

    // during cardmove in turn
    private Player turnPlayer;
    private Turn activeTurn;
    private List<Card> cardsInTheGame;
    private List<CardMove> allCardMovesForTheGame;

    // STATS based on RETRIEVED DATA

    // stats
    public boolean loggedOn;     // icon is spaceShip -> logon to enable nav-visitor
    public boolean balanceNotZero;      // icon is cards -> select/start game to enable nav-game
    public boolean gamePlayable;      // icon is fiches -> playable game to enable nav-qasino
    public boolean leaguePresent;       // icon is hallOfFame -> position in league or no league
    public boolean friendsPresent;      // icon is chat -> chats and relations

    // ERROR DATA
    private int httpStatus;
    private String errorKey;
    private String errorValue;
    private String errorMessage;
    private HttpHeaders headers;

    // PROCESS AND VALIDATE INPUT

    private Map<String, String> pathVariables = new HashMap<>();
    public void setPathVariables(String... pathVariables) {
        if (pathVariables == null) return;
        if (pathVariables.length % 2 != 0) return;
        for (int i=0 ; i< pathVariables.length ; i=i+2) {
            this.pathVariables.put(pathVariables[i],pathVariables[i+1]);
        }
    }
    private Map<String, String> requestParams = new HashMap<>();

    private Object payloadData;
    private URI uri;
    public boolean validateInput() {
        prepareResponseHeaders();
        if (!validatePathVariables(this.pathVariables)
            | !validateRequestParams(this.requestParams)) {
            headers.add(this.getErrorKey(), this.getErrorValue());
            headers.add("Error", this.getErrorMessage());
            return false;
        }
        return true;
    }
    public void prepareResponseHeaders() {
        this.uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(this.pathVariables, this.requestParams)
                .toUri();
        this.headers = new HttpHeaders();
        headers.add("URI", String.valueOf(this.getUri()));

        if (this.httpStatus > 299) {
            headers.add(this.getErrorKey(), this.getErrorValue());
            headers.add("Error", this.getErrorMessage());
        }

    }
    boolean validatePathVariables(Map<String, String> pathVariables) {
        String key;
        String dataName = "pathVariables";
        String pathDataString = StringUtils.join(pathVariables);
//        log.info(this.getClass().getName() + ": " + dataName + " is " + pathDataString);

        if (pathVariables==null) return true;

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
//        log.info(this.getClass().getName() + ": " + dataName + " is " + paramDataString);

        if (requestParam==null) return true;

        // paging
        key = "pages";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                this.suppliedPages = Integer.parseInt(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "maxPerPage";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                this.suppliedMaxPerPage = Integer.parseInt(requestParam.get(key));
            } else {
                return false;
            }
        }
        // visitor
        key = "visitorName";
        if (requestParam.containsKey(key)) {
            this.suppliedVisitorName = (requestParam.get("visitorName"));
        }
        key = "email";
        if (requestParam.containsKey(key)) {
            this.suppliedEmail = (requestParam.get("email"));
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
        key = "trigger";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedTrigger = GameTrigger.valueOf(requestParam.get(key));
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
            this.suppliedStyle = (requestParam.get("style"));
        }
        key = "ante";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
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
        if (isValueForIntKeyValid(key, value, dataName, dataToValidate)) {
            if (Long.parseLong(value) == 0) {
                // 404 - not found
                this.setHttpStatus(404);
                this.setErrorKey(key);
                this.setErrorValue(value);
                this.setErrorMessage("Value for " + key + " is zero");
                return false;
            }
            return true;
        }
        return false;
    }
    boolean isValueForIntKeyValid(String key, String value, String dataName, String dataToValidate) {
        if (!StringUtils.isNumeric(value)) {
            // 400 - bad request
            this.setHttpStatus(400);
            this.setErrorKey(key);
            this.setErrorValue(value);
            this.setErrorMessage("Invalid int value for" + key);
            return false;
        }
        return true;
    }
    boolean isValueForEnumKeyValid(String key, String value, String dataName,
                                   String dataToValidate) {

        switch (key) {

            case "role":
                if (!(Role.fromLabelWithDefault(key) == Role.ERROR)) {
                    return true;
                }
                break;
            case "avatar":
                if (!(Avatar.fromLabelWithDefault(key) == Avatar.ERROR)) {
                    return true;
                }
                break;
            case "aiLevel":
                if (!(AiLevel.fromLabelWithDefault(key) == AiLevel.ERROR)) {
                    return true;
                }
                break;
            case "gameTrigger":
                if (!(GameTrigger.fromLabelWithDefault(key) == GameTrigger.ERROR)) {
                    return true;
                }
                break;
            case "gameStateGroup":
                if (!(GameStateGroup.fromLabelWithDefault(key) == GameStateGroup.ERROR)) {
                    // todo ERROR can be a valid state but for now is bad request coming from client
                    return true;
                }
                break;
            case "type":
                if (!(Type.fromLabelWithDefault(key) == Type.ERROR)) {
                    return true;
                }
                break;
            case "move":
                if (!(Move.fromLabelWithDefault(key) == Move.ERROR)) {
                    return true;
                }
                break;
            case "location":
                if (!(Location.fromLabelWithDefault(key) == Location.ERROR)) {
                    return true;
                }
                break;
            case "position":
                if (!(Position.fromLabelWithDefault(key) == Position.ERROR)) {
                    return true;
                }
                break;
            case "face":
                if (!(Face.fromLabelWithDefault(key) == Face.ERROR)) {
                    return true;
                }
                break;
        }

        // 400 - bad request
        this.setHttpStatus(400);
        this.setErrorKey(key);
        this.setErrorValue("in error");
        this.setErrorMessage("Invalid enum value for" + key);

        return false;
    }
}