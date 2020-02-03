package cloud.qasino.card.dto;

import cloud.qasino.card.action.FindAllEntitiesForInputAction;
import cloud.qasino.card.entity.*;
import cloud.qasino.card.statemachine.GameTrigger;
import cloud.qasino.card.entity.enums.card.Face;
import cloud.qasino.card.entity.enums.card.Location;
import cloud.qasino.card.entity.enums.card.Position;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.move.Move;
import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.entity.enums.player.Role;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class QasinoFlowDTO //extends AbstractFlowDTO
        implements
        FindAllEntitiesForInputAction.FindAllEntitiesForInputActionDTO
{
    // suppress lombok setter for these fixed values
    @Setter(AccessLevel.NONE)
    private String applicationName = "qasino";

    // gui
    private Qasino qasino;


    // FRONTEND DATA
    // frontend header
    private int suppliedUserId;
    private int suppliedGameId;
    // frontend path ids
    private int suppliedLeagueId;
    private int invitedPlayerId;
    private int acceptedPlayerId;
    // enums
    private GameTrigger suppliedTrigger;
    private Move suppliedMove;
    private List<Card> suppliedCards;   // todo
    // generic frontend path ids
    private String suppliedEntity;      // todo
    private int suppliedEntityId;       // todo
    private String suppliedPathParam;   // todo
    // frontend query params
    // user
    private String suppliedAlias;
    private String suppliedEmail;
    // league
    private String suppliedLeagueName;
    private String suppliedLeagueEnd;    // todo monthsEnd, thisMonday, x days.
    private Boolean suppliedLeagueClose; // todo null, or true/false
    // player
    private Role suppliedRole;
    private int suppliedFiches;
    private Avatar suppliedAvatar;
    private AiLevel suppliedAiLevel;
    // game
    private Type suppliedType;
    private String suppliedStyle;
    private int suppliedAnte;
    private int suppliedJokers;
    // cardMove
    private Location suppliedLocation;
    private int suppliedTurnPlayerId;
    private int suppliedBet;

    // ACTION DATA
    // in game data
    private User gameUser;
    private Player invitedPlayer;
    private Player acceptedPlayer;
    private Player turnPlayer;
    private Game qasinoGame;
    private League gameLeague;

    private List<Game> initiatedGames;

    private List<Player> qasinoPlayers;
    private Turn qasinoTurn;
    private List<Card> qasinoCards;
    private List<CardMove> qasinoCardMoves;

    private List<League> leaguesForUser;
    private List<Result> resultsForLeague;

    private List<User> friends;

    // stats
    private boolean hasLoggedOn = false;     // icon is spaceShip -> logon to enable nav-user
    private boolean hasBalance = false;      // icon is cards -> select/start game to enable nav-game
    private boolean isPlayable = false;      // icon is fiches -> playable game to enable nav-qasino
    private boolean hasLeague = false;       // icon is hallOfFame -> position in league or no league
    private boolean hasFriends = false;      // icon is chat -> chats and relations

    // ERROR DATA
    private int httpStatus;
    private String errorKey;
    private String errorValue;
    private String errorMessage;
    private HttpHeaders headers;

    // PROCESS AND VALIDATE INPUT
    public boolean validateInput(
            Map<String, String> headerData,
            Map<String, String> pathData,
            Map<String, String> paramData,
            Object payloadData) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(pathData, paramData)
                .toUri();
        this.headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        if (!processHeader(headerData)
            | !processPathData(pathData)
            | !processParamData(paramData)) {
            headers.add(this.getErrorKey(), this.getErrorValue());
            headers.add("Error", this.getErrorMessage());
            return false;
        }
        return true;
    }
    boolean processHeader(Map<String, String> headerData) {
        String key;
        String dataName = "headerData";
        String headerDataString = StringUtils.join(headerData);
        log.info(this.getClass().getName() + ": " + dataName + " is " + headerDataString);

        key = "gameId";
        if (headerData.containsKey(key)) {
            if (isValueForIntKeyValid(key, headerData.get(key), dataName, headerDataString)) {
                this.suppliedGameId = Integer.parseInt(headerData.get(key));
            } else {
                return false;
            }
        }
        key = "userId";
        if (headerData.containsKey(key)) {
            if (isValueForIntKeyValid(key, headerData.get(key), dataName, headerDataString)) {
                this.suppliedUserId = Integer.parseInt(headerData.get(key));
            } else {
                return false;
            }
        }
        return true;
    }
    boolean processPathData(Map<String, String> pathData) {
        String key;
        String dataName = "pathData";
        String pathDataString = StringUtils.join(pathData);
        log.info(this.getClass().getName() + ": " + dataName + " is " + pathDataString);

        key = "leagueId";
        if (pathData.containsKey(key)) {
            if (isValueForIntKeyValid(key, pathData.get(key), dataName, pathDataString)) {
                this.suppliedLeagueId = Integer.parseInt(pathData.get(key));
            } else {
                return false;
            }
        }
        key = "invitedPlayerId";
        if (pathData.containsKey(key)) {
            if (isValueForIntKeyValid(key, pathData.get(key), dataName, pathDataString)) {
                this.invitedPlayerId = Integer.parseInt(pathData.get(key));
            } else {
                return false;
            }
        }
        key = "acceptedPlayerId";
        if (pathData.containsKey(key)) {
            if (isValueForIntKeyValid(key, pathData.get(key), dataName, pathDataString)) {
                this.suppliedLeagueId = Integer.parseInt(pathData.get(key));
            } else {
                return false;
            }
        }
        key = "turnPlayerId";
        if (pathData.containsKey(key)) {
            if (isValueForIntKeyValid(key, pathData.get(key), dataName, pathDataString)) {
                this.suppliedTurnPlayerId = Integer.parseInt(pathData.get(key));
            } else {
                return false;
            }
        }

        // call param data with path data to be sure
        return processParamData(pathData);

    }
    boolean processParamData(Map<String, String> paramData) {

        String key;
        String dataName = "paramData";
        String paramDataString = StringUtils.join(paramData);
        log.info(this.getClass().getName() + ": " + dataName + " is " + paramDataString);
        // user
        key = "alias";
        if (paramData.containsKey(key)) {
            this.suppliedAlias = (paramData.get("alias"));
        }
        key = "email";
        if (paramData.containsKey(key)) {
            this.suppliedEmail = (paramData.get("email"));
        }
        // player
        key = "role";
        if (paramData.containsKey(key)) {
            if (isValueForEnumKeyValid(key, paramData.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedRole = Role.fromLabel(paramData.get(key));
            } else {
                return false;
            }
        }
        key = "fiches";
        if (paramData.containsKey(key)) {
            if (isValueForIntKeyValid(key, paramData.get(key), dataName, paramDataString)) {
                this.suppliedFiches = Integer.parseInt(paramData.get(key));
            } else {
                return false;
            }
        }
        key = "avatar";
        if (paramData.containsKey(key)) {
            if (isValueForEnumKeyValid(key, paramData.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedAvatar = Avatar.fromLabel(paramData.get(key));
            } else {
                return false;
            }
        }
        key = "aiLevel";
        if (paramData.containsKey(key)) {
            if (isValueForEnumKeyValid(key, paramData.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedAiLevel = AiLevel.fromLabel(paramData.get(key));
            } else {
                return false;
            }
        }
        // game
        key = "trigger";
        if (paramData.containsKey(key)) {
            if (isValueForEnumKeyValid(key, paramData.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedTrigger = GameTrigger.valueOf(paramData.get(key));
            } else {
                return false;
            }
        }
        key = "type";
        if (paramData.containsKey(key)) {
            if (isValueForEnumKeyValid(key, paramData.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedType = Type.fromLabel(paramData.get(key));
            } else {
                return false;
            }
        }
        key = "style";
        if (paramData.containsKey(key)) {
            this.suppliedStyle = (paramData.get("style"));
        }
        key = "ante";
        if (paramData.containsKey(key)) {
            if (isValueForIntKeyValid(key, paramData.get(key), dataName, paramDataString)) {
                this.suppliedAnte = Integer.parseInt(paramData.get(key));
            } else {
                return false;
            }
        }
        key = "jokers";
        if (paramData.containsKey(key)) {
            if (isValueForIntKeyValid(key, paramData.get(key), dataName, paramDataString)) {
                this.suppliedJokers = Integer.parseInt(paramData.get(key));
            } else {
                return false;
            }
        }
        // league
        key = "leagueName";
        if (paramData.containsKey(key)) {
            this.suppliedLeagueName = (paramData.get("leagueName"));
        }
        // cardmove
        key = "move";
        if (paramData.containsKey(key)) {
            if (isValueForEnumKeyValid(key, paramData.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedMove = Move.fromLabel(paramData.get(key));
            } else {
                return false;
            }
        }
        key = "location";
        if (paramData.containsKey(key)) {
            if (isValueForEnumKeyValid(key, paramData.get(
                    key), dataName,
                    paramDataString)) {
                this.suppliedLocation = Location.fromLabel(paramData.get(key));
            } else {
                return false;
            }
        }
        key = "bet";
        if (paramData.containsKey(key)) {
            if (isValueForIntKeyValid(key, paramData.get(key), dataName, paramDataString)) {
                this.suppliedBet = Integer.parseInt(paramData.get(key));
            } else {
                return false;
            }
        }
        return true;
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
            case "suppliedTrigger":
                if (!(GameTrigger.fromLabelWithDefault(key) == GameTrigger.ERROR)) {
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
