package cloud.qasino.card.dto;

import cloud.qasino.card.controller.statemachine.GameTrigger;
import cloud.qasino.card.dto.event.AbstractFlowDTO;
import cloud.qasino.card.entity.*;
import cloud.qasino.card.entity.enums.event.Action;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.entity.enums.playingcard.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
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

    // frontend path ids
    private String suppliedGameId;
    private String suppliedPlayerId;
    private String suppliedEventId;
    private String suppliedPlayingCardId;
    private String suppliedUserId;
    private String suppliedCardId;

    private String suppliedResource;
    private String suppliedResourceId;
    private String suppliedExtraResource;

    // frontend query params
    private String suppliedHumanOrAi;
    private Type suppliedType;
    private String suppliedGameVariant;
    private String suppliedAnte;
    private String suppliedAlias;
    private Avatar suppliedAvatar;
    private String suppliedSecuredLoan;
    private AiLevel suppliedAiLevel;
    private String suppliedJokers;
    private String suppliedTest;
    private String suppliedPlayingOrder;

    private Action suppliedAction;
    private String suppliedTotal;
    private Location suppliedLocation;

    // in game changes
    private int newBet;
    private int newCubits;
    private int newCurrentRound;
    private int newCurrentTurn;
    private int newActiveEvent;

    // frontend trigger
    private GameTrigger suppliedTrigger;

    // process all frontend data to the fields above
    // processing related
    private Game currentGame;
    private Player currentPlayer;
    private PlayingCard currentPlayingCard;
    private League currentLeague;
    private User currentUser;
    private Event currentEvent;
    // lists of all the 6 entities, no setter?: that is done in the EventDTO's
    private List<Game> games;
    private List<Player> currentPlayers;
    private List<PlayingCard> currentPlayingCards;
    private List<League> currentLeagues;
    private List<User> currentUsers;
    private List<Event> currentEvents;
    // business rules related
    private List<Game> abandonedGames;
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

            if (pathAndQueryData.containsKey("humanOrAi")) {
                this.suppliedHumanOrAi = pathAndQueryData.get("humanOrAi");
            }

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

                this.suppliedGameVariant = pathAndQueryData.get("gameVariant");

                message = String.format("CardGameFlowDTO gameVariant stored is: %s", this.suppliedGameVariant);
                log.info(message);
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
            if (pathAndQueryData.containsKey("securedLoan")) {
                this.suppliedSecuredLoan = pathAndQueryData.get("securedLoan");
            }

            if (pathAndQueryData.containsKey("jokers")) {
                this.suppliedJokers = pathAndQueryData.get("jokers");
            }
            if (pathAndQueryData.containsKey("test")) {
                this.suppliedTest = pathAndQueryData.get("test");
            }
            if (pathAndQueryData.containsKey("playingOrder")) {
                this.suppliedPlayingOrder = pathAndQueryData.get("playingOrder");
            }
            if (pathAndQueryData.containsKey("action")) {
                this.suppliedAction = Action.fromLabel(pathAndQueryData.get("action"));
            }
            if (pathAndQueryData.containsKey("location")) {
                this.suppliedLocation = Location.fromLabel(pathAndQueryData.get("location"));
            }
            if (pathAndQueryData.containsKey("total")) {
                this.suppliedTotal = pathAndQueryData.get("total");
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