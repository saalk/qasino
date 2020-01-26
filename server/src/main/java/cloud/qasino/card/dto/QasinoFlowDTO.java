package cloud.qasino.card.dto;

import cloud.qasino.card.controller.statemachine.GameTrigger;
import cloud.qasino.card.entity.enums.playingcard.Card;
import cloud.qasino.card.entity.enums.Style;
import cloud.qasino.card.dto.event.AbstractFlowDTO;
import cloud.qasino.card.entity.*;
import cloud.qasino.card.entity.enums.event.Action;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.entity.enums.playingcard.Location;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class QasinoFlowDTO extends AbstractFlowDTO
/*        implements
        CreateCasinoForGameAndPlayerEvent.CreateCasinoForGameAndPlayerEventDTO,
        CreatePlayingCardForGameEvent.CreatePlayingCardForGameEventDTO,
        CreateUserForCasinoForGameAndPlayerEvent.CreateUserForCasinoForGameAndPlayerEventDTO,
        CreatePlayerEvent.CreatePlayerEventDTO,
        DeleteCardGameEvent.DeleteCardGameEventDTO,
        DeleteCasinoForGameAndPlayerEvent.DeleteCasinoForGameAndPlayerEventDTO,
        DetermineTurnResultsEvent.DetermineTurnResultsEventDTO,
        GetCardGameDetailsEvent.GetCardGameDetailsEventDTO,
        SetupFlowDTOForEveryEvent.SetupFlowDTOForEveryEventDTO,
        UpdateCardGameDetailsEvent.UpdateCardGameDetailsEventDTO,
        UpdateCasinoForPlayingOrderEvent.UpdateCasinoForPlayingOrderEventDTO,
        UpdateCasinoForTurnAndBetEvent.UpdateCasinoForTurnAndBetEventDTO,
        UpdatePlayingCardForGameAndCasinoEvent.UpdatePlayingCardForGameAndCasinoEventDTO,
        UpdatePlayerCubitsAndSecuredLoanEvent.UpdatePlayerCubitsAndSecuredLoanEventDTO,
        UpdatePlayerForCasinoDetailsEvent.UpdatePlayerForCasinoDetailsEventDTO,
        VerifyTurnStateEvent.VerifyTurnStateEventDTO */ {
    // suppress lombok setter for these fixed values
    @Setter(AccessLevel.NONE)
    private String applicationId = "001";

    // navigation game user
    private boolean hasUser;
    private String userAlias;
    private Avatar userAvatar;
    private int userSecuredLoan;
    private int userBalance;
    private int userFiches;
    private int currentBet;

    // navigation game players
    private boolean hasPlayers;
    private boolean initiator;
    private int invitations;
    private int accepted;
    private int bots;

    // navigation active game
    private boolean hasGame;
    private Type gameType;
    private Style gameStyle;
    private int gameAnte;

    // navigation league
    private boolean hasLeague;
    private String leagueName;
    private String endDate;

    // frontend path ids
    private String suppliedUserId;
    private String suppliedLeagueId;
    private String suppliedGameId;
    private String suppliedPlayerId;
    private String suppliedPlayingCardId;
    private String suppliedEventId;
    private String suppliedResultId;
    private String suppliedCardId;

    private String suppliedResource;
    private String suppliedResourceId;
    private String suppliedExtraResource;

    // frontend query params
    private Type suppliedType;
    private Style suppliedStyle;
    private String suppliedAnte;
    private String suppliedAlias;
    private Avatar suppliedAvatar;
    private AiLevel suppliedAiLevel;
    private String suppliedJokers;
    private Action suppliedAction;
    private Location suppliedLocation;

    // in game changes
    private int newAnte;
    private int fichesWon;
    private int newCurrentRound;
    private int newCurrentTurn;
    private int newActiveEvent;

    // frontend trigger
    private GameTrigger suppliedTrigger;

    // process all frontend data to the fields above
    // processing related
    private User currentUser;
    private League currentLeague;
    private Game currentGame;
    private Player currentPlayer;
    private PlayingCard currentPlayingCard;
    private Card currentCard;
    private Event currentEvent;
    private Result currentResult;

    // lists of all the 6 entities, no setter?: that is done in the EventDTO's
    private List<User> currentUsers;
    private List<Game> games;
    private List<League> leagues;
    private List<Player> currentPlayers;
    private List<PlayingCard> currentPlayingCards;
    private List<Card> currentCards;
    private List<Event> currentEvents;
    private List<Result> currentResults;

    // business rules related
    private List<Game> pendingInvitations;

    private Map<Location, PlayingCard> locationPlayingCardMap;
    private Map<Player, PlayingCard> playerPlayingCardMap;

    // return fields
    private Integer rulesCode;

    public void processPathAndQueryParamsAndTrigger(Map<String, String> pathAndQueryData,
                                                    GameTrigger trigger) {
        if (pathAndQueryData != null || !pathAndQueryData.isEmpty()) {

            String message = String.format("CardGameFlowDTO processPatuserQueryParamsAndTrigger is: %s", pathAndQueryData);
            log.info(message);

            // pass pathid's to the Flow
            if (pathAndQueryData.containsKey("gameId")) {
                this.suppliedGameId = pathAndQueryData.get("gameId");
            }
            if (pathAndQueryData.containsKey("playerId")) {
                this.suppliedPlayerId = pathAndQueryData.get("playerId");
            }
            if (pathAndQueryData.containsKey("eventId")) {
                this.suppliedEventId = pathAndQueryData.get("eventId");
            }

/*            if (pathAndQueryData.containsKey("humanOrAi")) {
                this.suppliedHumanOrAi = pathAndQueryData.get("humanOrAi");
            }*/

            // pass queryParam's to the Flow
            if (pathAndQueryData.containsKey("type")) {
                this.suppliedType = Type.fromLabel(pathAndQueryData.get("type"));

                message = String.format("CardGameFlowDTO type in path is: %s", pathAndQueryData.get("type"));
                log.info(message);

                message = String.format("CardGameFlowDTO type from Label is: %s", Type.fromLabel(pathAndQueryData.get("type")));
                log.info(message);

                message = String.format("CardGameFlowDTO type stored is: %s", this.suppliedType);
                log.info(message);
            }
            if (pathAndQueryData.containsKey("gameVariant")) {

                message = String.format("CardGameFlowDTO gameVariant in path is: %s", pathAndQueryData.get("gameVariant"));
                log.info(message);

//				GameVariant gv = new GameVariant();
//				gv.fromVariant(patuserQueryData.get("gameVariant"));
//
//				message = String.format("CardGameFlowDTO gameVariant from Label is: %s", gv.getVariant());
//				log.info(message);

            }
            if (pathAndQueryData.containsKey("ante")) {
                this.suppliedAnte = pathAndQueryData.get("ante");
            }
            if (pathAndQueryData.containsKey("alias")) {
                this.suppliedAlias = pathAndQueryData.get("alias");
            }
            if (pathAndQueryData.containsKey("avatar")) {
                this.suppliedAvatar = Avatar.fromLabel(pathAndQueryData.get("avatar"));
            }
            if (pathAndQueryData.containsKey("aiLevel")) {
                this.suppliedAiLevel = AiLevel.fromLabel(pathAndQueryData.get("aiLevel"));
            }

            if (pathAndQueryData.containsKey("jokers")) {
                this.suppliedJokers = pathAndQueryData.get("jokers");
            }
            if (pathAndQueryData.containsKey("action")) {
                this.suppliedAction = Action.fromLabel(pathAndQueryData.get("action"));
            }
            if (pathAndQueryData.containsKey("location")) {
                this.suppliedLocation = Location.fromLabel(pathAndQueryData.get("location"));
            }
            if (pathAndQueryData.containsKey("resource")) {
                this.suppliedResource = pathAndQueryData.get("resource");
            }
            if (pathAndQueryData.containsKey("resourceId")) {
                this.suppliedResourceId = pathAndQueryData.get("resourceId");
            }
            if (pathAndQueryData.containsKey("extraResource")) {
                this.suppliedExtraResource = pathAndQueryData.get("extraResource");
            }
        }
        if (trigger != null) {
            this.suppliedTrigger = trigger;
        }
    }

    public void setContextByGame() {
        setGameContext(currentGame);
    }

    public void setGameByContext() {
        setCurrentGame(getGameContext());
        setSuppliedGameId(String.valueOf(currentGame.getGameId()));
    }

    public List<PlayingCard> getCurrentPlayingCards() {
        if (currentPlayingCards == null) {
            currentPlayingCards = new ArrayList<>();
        }
        return currentPlayingCards;
    }

    public List<Event> getCurrentEvents() {
        if (currentEvents == null) {
            currentEvents = new ArrayList<>();
        }
        return currentEvents;
    }

    public List<User> getCurrentUsers() {
        if (currentUsers == null) {
            currentUsers = new ArrayList<>();
        }
        return currentUsers;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
        setContextByGame();
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public void setCurrentEvent(Event event) {
        this.currentEvent = event;
    }

    public void setSuppliedPlayerId(String playerId) {
        this.suppliedPlayerId = playerId;
    }

    public void setSuppliedEventId(String eventId) {
        this.suppliedEventId = eventId;
    }

    public String getSuppliedGameId() {
        return this.suppliedGameId;
    }


}