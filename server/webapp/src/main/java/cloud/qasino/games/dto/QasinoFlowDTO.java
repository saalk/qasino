package cloud.qasino.games.dto;

import cloud.qasino.games.action.CalculateAndFinishGameAction;
import cloud.qasino.games.action.CalculateQasinoStatisticsAction;
import cloud.qasino.games.action.CreateNewGameAction;
import cloud.qasino.games.action.CreateNewLeagueAction;
import cloud.qasino.games.action.DeterminePossibleEventsAction;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.HandleSecuredLoanAction;
import cloud.qasino.games.action.IsGameConsistentForGameEventAction;
import cloud.qasino.games.action.IsGameFinishedAction;
import cloud.qasino.games.action.IsPlayerHumanAction;
import cloud.qasino.games.action.IsTurnConsistentForTurnEventAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDtoAction;
import cloud.qasino.games.action.MapQasinoResponseFromDtoAction;
import cloud.qasino.games.action.PlayFirstTurnAction;
import cloud.qasino.games.action.StartGameForTypeAction;
import cloud.qasino.games.action.PlayNextBotTurnAction;
import cloud.qasino.games.action.PlayNextHumanTurnAction;
import cloud.qasino.games.action.PrepareGameAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.SignUpNewVisitorAction;
import cloud.qasino.games.action.StopGameAction;
import cloud.qasino.games.action.UpdateFichesForPlayerAction;
import cloud.qasino.games.action.UpdateStyleForGame;
import cloud.qasino.games.action.UpdateVisitorAction;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.dto.statistics.Statistic;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.statemachine.event.GameEvent;
import cloud.qasino.games.statemachine.event.TurnEvent;
import cloud.qasino.games.statemachine.event.interfaces.AbstractFlowDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Data
@Getter
@Setter
@Slf4j
public class QasinoFlowDTO extends AbstractFlowDTO
        implements
        CalculateAndFinishGameAction.Dto,
        CalculateQasinoStatisticsAction.Dto,
        CreateNewGameAction.Dto,
        CreateNewLeagueAction.Dto,
        DeterminePossibleEventsAction.Dto,
        FindVisitorIdByAliasOrUsernameAction.Dto,
        HandleSecuredLoanAction.Dto,
        IsGameConsistentForGameEventAction.Dto,
        IsTurnConsistentForTurnEventAction.Dto,
        IsGameFinishedAction.Dto,
        IsPlayerHumanAction.Dto,
        LoadEntitiesToDtoAction.Dto,
        MapQasinoResponseFromDtoAction.Dto,
        MapQasinoGameTableFromDtoAction.Dto,
        PlayNextHumanTurnAction.Dto,
        PlayNextBotTurnAction.Dto,
        PlayFirstTurnAction.Dto,
        PrepareGameAction.Dto,
        SetStatusIndicatorsBaseOnRetrievedDataAction.Dto,
        SignUpNewVisitorAction.Dto,
        StartGameForTypeAction.Dto,
        StopGameAction.Dto,
        UpdateVisitorAction.Dto,
        UpdateStyleForGame.Dto,
        UpdateFichesForPlayerAction.Dto
{
    // suppress lombok setter for these fixed values
    @Setter(AccessLevel.NONE)
    private String applicationName = "qasino";

    // gui
    private QasinoResponse qasinoResponse;
    private List<Statistic> statistics;

    // FRONTEND IDS
    // path params
    private long suppliedVisitorId;
    private long suppliedGameId;
    private long suppliedLeagueId;
    private long initiatingPlayerId;

    private long invitedVisitorId;
    private long invitedPlayerId;
    private long acceptedPlayerId;
    private long declinedPlayerId;

    private long suppliedTurnPlayerId;
    // triggers for the Game
    private GameEvent suppliedGameEvent;
    private TurnEvent suppliedTurnEvent;
    private GameStateGroup suppliedGameStateGroup;
    // Triggers for playing a Game
    private Move suppliedMove;
    private List<PlayingCard> suppliedCards;   // todo

    // FRONTEND request params
    // paging
    private int suppliedPage = 1;
    private int suppliedMaxPerPage = 4;
    // visitor
    private String suppliedUsername;
    private String suppliedPassword;
    private String suppliedEmail;
    private String suppliedAlias;
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
    private AnteToWin suppliedAnteToWin; // maxrounds etc
    private BettingStrategy suppliedBettingStrategy; // maxrounds etc
    private DeckConfiguration suppliedDeckConfiguration; // maxrounds etc
    private OneTimeInsurance suppliedOneTimeInsurance; // maxrounds etc
    private RoundsToWin suppliedRoundsToWin; // maxrounds etc
    private TurnsToWin suppliedTurnsToWin; // maxrounds etc
    private int suppliedAnte; // inleg
    private int suppliedJokers;
    // cardMove
    private Location suppliedLocation;
    private int suppliedBet;

    // RETRIEVED DATA BASED ON FRONTEND ID's
    // the logged on visitor
    private Visitor qasinoVisitor;
    private List<Game> initiatedGamesForVisitor;
    private List<Game> invitedGamesForVisitor;
    // the game and players
    private Game qasinoGame;
    private List<Player> qasinoGamePlayers;
    // the individual player
    private Visitor invitedVisitor;
    private Player invitedPlayer;
    private Player acceptedPlayer;
    private Player initiatingPlayer;
    // the league and list of leagues the visitor created
    private League qasinoGameLeague;
    private List<League> leaguesForVisitor;
    // the game results
    private List<Result> gameResults;
    private List<Result> resultsForLeague;

    // FOR THE GAME BEING PLAYED
    private SectionTable table;
    private Player turnPlayer;
    private Player nextPlayer;
    private Turn activeTurn;
    private List<Card> cardsInTheGameSorted;
    private List<CardMove> allCardMovesForTheGame;
    private List<TurnEvent> possibleTurnEvents;
    private List<GameEvent> possibleGameEvents;

    // NAVIGATION based on RETRIEVED DATA
    public boolean showVisitorPage;
    public boolean showGameSetupPage;
    public boolean showGamePlayPage;
    public boolean showGameInvitationsPage;
    public boolean showLeaguesPage;

    // MESSAGE AND ERROR DATA
    private String action;
    private boolean actionNeeded;

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

    // RESPONSE DATA
    // @formatter:off
    private HttpHeaders headers = new HttpHeaders();
    private Object payloadData;
    private URI uri;
    public void addKeyValueToHeader(String key, String value) {
        this.headers.add(key, value);
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
            if (!this.errorReason.isEmpty()) {
                // also add error to header
                addKeyValueToHeader("Reason", this.getErrorReason());
            }
        }

    }
    // @formatter:on

    // INPUT // TODO move out of DTO
    @Setter(AccessLevel.NONE)
    private Map<String, String> pathVariables = new HashMap<>();
    private Map<String, String> requestParams = new HashMap<>();

    public void setPathVariables(String... pathVariables) {
        if (pathVariables == null) {
            throw new MyBusinessException("No pathVariables [" + Arrays.toString(pathVariables) + "]");
        };
        if (pathVariables.length % 2 != 0) {
            throw new MyBusinessException("PathVariables not even [" + Arrays.toString(pathVariables) + "]");
        };
        for (int i = 0; i < pathVariables.length; i = i + 2) {
            this.pathVariables.put(pathVariables[i], pathVariables[i + 1]);
        }
    }

    // VALIDATE INPUT - TODO move out of DTO
    // @formatter:off
    public boolean isInputValid() {
        if (!validatePathVariables(this.pathVariables)
                | !validateRequestParams(this.requestParams)) {
            return false;
        }
        return true;
    }
    boolean validatePathVariables(Map<String, String> pathVariables) {
        String key;
        String dataName = "pathVariables";
        String pathDataString = StringUtils.join(pathVariables);

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
        key = "invitedVisitorId";
        if (pathVariables.containsKey(key)) {
            if (isValueForPrimaryKeyValid(key, pathVariables.get(key), dataName, pathDataString)) {
                this.invitedVisitorId = Long.parseLong(pathVariables.get(key));
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
        key = "declinedPlayerId";
        if (pathVariables.containsKey(key)) {
            if (isValueForPrimaryKeyValid(key, pathVariables.get(key), dataName, pathDataString)) {
                this.declinedPlayerId = Long.parseLong(pathVariables.get(key));
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
        key = "alias";
        if (requestParam.containsKey(key)) {
            this.suppliedAlias = (requestParam.get("alias"));
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
        key = "anteToWin";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedAnteToWin = AnteToWin.valueOf(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "bettingStrategy";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedBettingStrategy = BettingStrategy.valueOf(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "deckConfiguration";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedDeckConfiguration = DeckConfiguration.valueOf(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "oneTimeInsurance";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedOneTimeInsurance = OneTimeInsurance.valueOf(requestParam.get(key));
            } else {
                return false;
            }
        }
        key = "roundsToWin";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                log.warn("rounds to win requestParam valid");

                this.suppliedRoundsToWin = RoundsToWin.valueOf(requestParam.get(key));
            } else {
                log.warn("rounds to win not valid");

                return false;
            }
        }
        key = "turnsToWin";
        if (requestParam.containsKey(key)) {
            if (isValueForEnumKeyValid(key, requestParam.get(
                            key), dataName,
                    paramDataString)) {
                this.suppliedTurnsToWin = TurnsToWin.valueOf(requestParam.get(key));
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
                if (Style.fromLabelWithDefault(value)!=null) {
                    return true;
                }
                break;
            case "anteToWin":
                if (AnteToWin.fromName(value) !=null) {
                    return true;
                }
                break;
            case "bettingStrategy":
                if (BettingStrategy.fromName(value) !=null) {
                    return true;
                }
                break;
            case "deckConfiguration":
                if (DeckConfiguration.fromName(value) !=null) {
                    return true;
                }
                break;
            case "oneTimeInsurance":
                if (OneTimeInsurance.fromName(value) !=null) {
                    return true;
                }
                break;
            case "roundsToWin":
                if (RoundsToWin.fromName(value) !=null) {
                    return true;
                }
                break;
            case "turnsToWin":
                if (TurnsToWin.fromName(value) !=null) {
                    return true;
                }
                break;

        }

        this.setErrorKey(key);
        this.setErrorValue(value);
        setBadRequestErrorMessage("Not a valid emun value supplied");
        return false;
    }
    // @formatter:on

}