package cloud.qasino.games.dto;

import cloud.qasino.games.action.*;
import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.dto.statistics.Statistics;
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
import cloud.qasino.games.statemachine.trigger.TurnTrigger;
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
        CreateNewLeagueAction.CreateNewLeagueActionDTO,
        FindAllEntitiesForInputAction.FindAllEntitiesForInputActionDTO,
        CalculateHallOfFameAction.CalculateHallOfFameActionDTO,
        HandleSecuredLoanAction.HandleSecuredLoanActionDTO,
        SetStatusIndicatorsBaseOnRetrievedDataAction.SetStatusIndicatorsBaseOnRetrievedDataDTO,
        MapQasinoResponseFromRetrievedDataAction.Dto,
        MapTableFromRetrievedDataAction.Dto,
        PlayNextTurnAndCardMovesForHuman.Dto,
        IsGameConsistentForGameTrigger.Dto,
        IsTurnConsistentForTurnTrigger.Dto,
        ProgressCardMovesForTurnTrigger.ProgressCardMovesForTurnTriggerDTO,
        UpdateTurnForGameType.UpdateTurnForGameTypeDTO,
        IsGameFinished.IsGameFinishedDTO,
        MakeGamePlayableForGameType.Dto,
        SetupTurnAndInitialCardMovesForGameType.Dto
{
    // suppress lombok setter for these fixed values
    @Setter(AccessLevel.NONE)
    private String applicationName = "qasino";

    // gui
    private Qasino qasino;
    private Statistics statistics;

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
    private GameTrigger suppliedGameTrigger;
    private TurnTrigger suppliedTurnTrigger;
    private GameStateGroup suppliedGameStateGroup;
    // Triggers for playing a Game
    private Move suppliedMove;
    private List<Card> suppliedCards;   // todo

    // FRONTEND request params
    // paging
    private int suppliedPage = 1;
    private int suppliedMaxPerPage = 4;
    // visitor
    private String suppliedVisitorName;
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

    // RETRIEVED DATA BASED ON FRONTEND ID's
    // the logged on visitor
    private Visitor qasinoVisitor;
    private List<Visitor> friends;
    // the game and players
    private Game qasinoGame;
    private List<Player> qasinoGamePlayers;
    private List<Game> newGamesForVisitor;
    private List<Game> startedGamesForVisitor;
    private List<Game> finishedGamesForVisitor;
    // the individual player
    private Player invitedPlayer;
    private Player acceptedPlayer;
    private Player initiatingPlayer;
    // the league and list of leagues the visitor created
    private League qasinoGameLeague;
    private List<League> leaguesForVisitor;
    // the game results
    private Result gameResult;
    private List<Result> resultsForLeague;

    // FOR THE GAME BIENG PLAYED
    private SectionTable table;

    // during cardmoves in a turn
    private Player turnPlayer;
    private Turn activeTurn;
    private List<Card> cardsInTheGameSorted;
    private List<CardMove> allCardMovesForTheGame;

    // NAVIGATION based on RETRIEVED DATA
    public boolean showVisitorPage;       // spaceShip - nav-visitor
    public boolean showGameConfigurator;  // cards - nav-game
    public boolean showGamePlay;          // fiches - nav-qasino
    public boolean showPendingGames;      // chat - nav-chat
    public boolean showLeagues;           // hallOfFame - nav-fame

    // ERROR DATA
    private int httpStatus = 200;
    private String errorKey = "Key";
    private String errorValue = "Value";
    private String errorMessage = "No error";
    private HttpHeaders headers = new HttpHeaders();
    public void addKeyValueToHeader(String key, String value) {
        this.headers.add(key, value);
    }

    // todo PROCESS AND VALIDATE INPUT
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
        if (!validatePathVariables(this.pathVariables)
            | !validateRequestParams(this.requestParams)) {
            return false;
        }
        return true;
    }
    public void prepareResponseHeaders() {
        this.uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(this.pathVariables, this.requestParams)
                .toUri();
        this.headers.add("URI", String.valueOf(this.getUri()));
        if (qasinoVisitor != null) {
            this.headers.add("visitorId", String.valueOf(qasinoVisitor.getVisitorId()));
        }
        if (qasinoGame != null) {
            this.headers.add("gameId", String.valueOf(qasinoGame.getGameId()));
        }
        if (qasinoGameLeague != null) {
            this.headers.add("leagueId", String.valueOf(qasinoGameLeague.getLeagueId()));
        }
        if (turnPlayer != null) {
            this.headers.add("turnPlayerId", String.valueOf(turnPlayer.getPlayerId()));
        }
        if (activeTurn != null) {
            this.headers.add("turnId", String.valueOf(activeTurn.getTurnId()));
        }
        if (this.httpStatus > 299) {
            // also add error to header
            addKeyValueToHeader(this.getErrorKey(), this.getErrorValue());
            addKeyValueToHeader("Error", this.getErrorMessage());
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
        key = "pageNumber";
        if (requestParam.containsKey(key)) {
            if (isValueForIntKeyValid(key, requestParam.get(key), dataName, paramDataString)) {
                if (Integer.parseInt(requestParam.get(key)) < 1) {
                    this.setHttpStatus(400);
                    this.setErrorKey(key);
                    this.setErrorValue(errorValue);
                    this.setErrorMessage("Value for [" + key + "] is less than 1");
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
        key = "visitorName";
        if (requestParam.containsKey(key)) {
            this.suppliedVisitorName = (requestParam.get("visitorName"));
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
        key = "gameTrigger";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedGameTrigger = GameTrigger.fromLabel(requestParam.get(key));
            } else {
                return false;
            }
        }
        // turn
        key = "turnTrigger";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedTurnTrigger = TurnTrigger.fromLabel(requestParam.get(key));
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
                this.setErrorMessage("Value for [" + key + "] is zero");
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
            this.setErrorMessage("Invalid numeric value for [" + key + "]");
            return false;
        }
        return true;
    }
    boolean isValueForEnumKeyValid(String key, String value, String dataName,
                                   String dataToValidate) {

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
            case "gameTrigger":
                if (!(GameTrigger.fromLabelWithDefault(value) == GameTrigger.ERROR)) {
                    return true;
                }
                break;
            case "turnTrigger":
                if (!(TurnTrigger.fromLabelWithDefault(value) == TurnTrigger.ERROR)) {
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
        }

        // 400 - bad request
        this.setHttpStatus(400);
        this.setErrorKey(key);
        this.setErrorValue(value);
        this.setErrorMessage("Invalid enum value for [" + key + "]");

        return false;
    }

}