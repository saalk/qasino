package cloud.qasino.card.controller;

import cloud.qasino.card.controller.statemachine.EventState;
import cloud.qasino.card.controller.statemachine.EventTrigger;
import cloud.qasino.card.dto.QasinoFlowDTO;
import cloud.qasino.card.dto.event.FlowDTOBuilder;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.error.ErrorResponse;
import cloud.qasino.card.error.HttpError;
import cloud.qasino.card.response.QasinoResponse;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@Scope("prototype")
public class QasinoController extends AbstractController<Game> {

    // 1 - configure the state machine in the AbstractController
    private static StateMachineConfig<EventState, EventTrigger> config = new StateMachineConfig<>();

    static {
        // @formatter:off
        // start on player page

        config.configure(EventState.NEW)
                .permit(EventTrigger.START, EventState.NEW)

                .permit(EventTrigger.STOP, EventState.HIGHER)
                .permit(EventTrigger.STOP, EventState.LOWER);

        config.configure(EventState.DEALT)
                .permit(EventTrigger.HIGHER, EventState.HIGHER)
                .permit(EventTrigger.LOWER, EventState.LOWER);

        config.configure(EventState.DEALT)
                .permitReentry(EventTrigger.CRASH)
                .permitReentry(EventTrigger.INVITE)
                .permitReentry(EventTrigger.ACCEPT)
                .permit(EventTrigger.CRASH, EventState.ERROR);

        config.configure(EventState.HIGHER)
                .permitReentry(EventTrigger.DEAL)
                .permit(EventTrigger.WINNER, EventState.FINISHED)
                .permit(EventTrigger.CRASH, EventState.ERROR);

        config.configure(EventState.LOWER)
                .permit(EventTrigger.DEAL, EventState.PLAYING); // allows continue other players!

        // @formatter:on
    }

    // 2 - Prepare a builder for CardGameFlowDTO and get methods like
    //     addEvent, addStateMachine, getNextInFlow and
    //     start, transition, build
    private FlowDTOBuilder<QasinoFlowDTO> builder;
    @Resource
    private ApplicationContext applicationContext;

    protected StateMachineConfig<EventState, EventTrigger> getStateMachineConfiguration() {
        return config;
    }

    @PostConstruct
    public void init() {
        this.builder = new FlowDTOBuilder<>(new QasinoFlowDTO());
        this.builder.setApplicationContext(applicationContext);

        String message = String.format("PostConstruct in controller is: %s", applicationContext);
        log.info(message);
    }

    // 3 - Play a the CardGame based on the EventTrigger and a list of input data
/*	@Autowired
	private CardGameMapperUtil mapUtil;*/

    public QasinoResponse play(EventTrigger EventTrigger, Map<String, String> pathAndQueryData) throws Exception {

        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        final QasinoResponse.QasinoResponseBuilder responseBuilder;


        String message = String.format("CardGameController EventTrigger to execute is: %s and data %s", EventTrigger, pathAndQueryData);
        log.info(message);

        switch (EventTrigger) {

            case NEW:

                //POST   api/cardgames/init?gameType={g},gameVariant=(v),ante={a}
                // init makes a default card game and adds it as context to flowDTO
                flowDTO = builder
                        .addContext(super.init(new Game()))
                        .addStateMachine(this.stateMachine)
/*						          .addEvent(SetupFlowDTOForEveryEvent.class)
						          .addEvent(CreatePlayerEvent.class)
						          .addEvent(UpdateCardGameDetailsEvent.class)
						          .addEvent(CreateCasinoForGameAndPlayerEvent.class)*/
                        .build();
                flowDTO.processPathAndQueryParamsAndTrigger(pathAndQueryData, EventTrigger);
                flowDTO.setGameByContext();
                flowDTO.start();

                message = String.format("CardGameController gametype are: %s", flowDTO.getSuppliedType());
                log.info(message);

                // state INITIALIZED
                break;


            case ACCEPT:

                //PUT    api/cardgames/init/1?gameType={g},ante={a}
                flowDTO = builder
                        .addContext(super.reinstate(Integer.parseInt(pathAndQueryData.get("gameId"))))
                        .addStateMachine(this.stateMachine)
/*						          .addEvent(SetupFlowDTOForEveryEvent.class)
						          .addEvent(UpdateCardGameDetailsEvent.class)*/
                        .build();

                // reinstate get the card game and adds it as context to flowDTO
                List<EventState> possibleEventStates = new ArrayList<>();
                possibleEventStates.add(EventState.NEW);
                possibleEventStates.add(EventState.PENDING_INVITATIONS);
                try {
                    stateMachine.checkAll(possibleEventStates);
                } catch (Exception e) {
                    // make error response
                    responseBuilder = QasinoResponse.builder();
                    ErrorResponse error = new ErrorResponse(HttpError.CODE_CONFLICT, "shuffle or turn", "init", "state");
                    responseBuilder.errorCode(error.getCode());
                    responseBuilder.errorMessage(error.getMessage());
                    responseBuilder.solution(error.getSolution());
                    responseBuilder.reason(QasinoResponse.Reason.FAILURE);
                    return responseBuilder.build();
                }

                flowDTO.processPathAndQueryParamsAndTrigger(pathAndQueryData, EventTrigger);
                flowDTO.start();
                // state INITIALIZED or PENDING_INVITATIONS
                break;

            case INVITE:

                //POST   api/cardgames/1/setup/human        ?alias/avatar/securedLoan            // no dealing yet
                //POST   api/cardgames/1/setup/ai           ?alias/avatar/securedLoan/aiLevel


                // reinstate get the card game and adds it as context to flowDTO
                flowDTO = builder
                        .addContext(super.reinstate(Integer.parseInt(pathAndQueryData.get("gameId"))))
/*
						          .addEvent(SetupFlowDTOForEveryEvent.class)
						          .addEvent(CreatePlayerEvent.class)
						          .addEvent(CreateCasinoForGameAndPlayerEvent.class)
*/
                        .addStateMachine(this.stateMachine)
                        .build();

                List<EventState> possiblePOST_SETUPStates = new ArrayList<>();
                possiblePOST_SETUPStates.add(EventState.NEW);
                possiblePOST_SETUPStates.add(EventState.PENDING_INVITATIONS);
                try {
                    stateMachine.checkAll(possiblePOST_SETUPStates);
                } catch (Exception e) {
                    // make error response
                    responseBuilder = QasinoResponse.builder();
                    ErrorResponse error = new ErrorResponse(HttpError.CODE_CONFLICT, "shuffle or turn", "setup", "state");
                    responseBuilder.errorCode(error.getCode());
                    responseBuilder.errorMessage(error.getMessage());
                    responseBuilder.solution(error.getSolution());
                    responseBuilder.reason(QasinoResponse.Reason.FAILURE);
                    return responseBuilder.build();
                }

                flowDTO.processPathAndQueryParamsAndTrigger(pathAndQueryData, EventTrigger);
                flowDTO.start();
                // state PENDING_INVITATIONS
                break;


            case DEAL:

                //PUT    api/cardgames/1/turn/players/2?action=deal/higher/lower/pass // for human player
                //PUT    api/cardgames/1/turn/players/3?action=next  // auto deal or pass for ai player

                // reinstate get the card game and adds it as context to flowDTO
                flowDTO = builder
                        .addContext(super.reinstate(Integer.parseInt(pathAndQueryData.get("gameId"))))
/*
						          .addEvent(SetupFlowDTOForEveryEvent.class)
						          .addEvent(UpdateDeckForGameAndCasinoEvent.class) // DEAL
						          .addEvent(CreateHandForCasinoForGameAndPlayerEvent.class) // DEAL
						          .addEvent(DetermineTurnResultsEvent.class) // TURN, ROUND
						          .addEvent(UpdateCardGameDetailsEvent.class) //Set ROUND + 1
						          .addEvent(UpdateCasinoForTurnAndBetEvent.class) // set TURN = 1
*/
                        .addStateMachine(this.stateMachine)
                        .build();

				possibleEventStates = new ArrayList<>();
                possibleEventStates.add(EventState.FINISHED);
                try {
                    stateMachine.checkAll(possibleEventStates);
                } catch (Exception e) {
                    // make error response
                    responseBuilder = QasinoResponse.builder();
                    ErrorResponse error = new ErrorResponse(HttpError.CODE_CONFLICT, "init, setup or shuffle", "turn", "state");
                    responseBuilder.errorCode(error.getCode());
                    responseBuilder.errorMessage(error.getMessage());
                    responseBuilder.solution(error.getSolution());
                    responseBuilder.reason(QasinoResponse.Reason.FAILURE);
                    return responseBuilder.build();
                }

                flowDTO.processPathAndQueryParamsAndTrigger(pathAndQueryData, EventTrigger);
                flowDTO.start();
                // state PLAYING or still in IS_SHUFFLED
                // TODO determine event for state GameWon, NoMoreCards or RoundsEnded
                break;

            case PASS:

                //PUT    api/cardgames/1/turn/players/2?action=pass // for human player
                //PUT    api/cardgames/1/turn/players/3?action=next  // auto deal or pass for ai player

                // reinstate get the card game and adds it as context to flowDTO
                // TODO
                flowDTO = builder
                        .addContext(super.reinstate(Integer.parseInt(pathAndQueryData.get("gameId"))))
/*						          .addEvent(SetupFlowDTOForEveryEvent.class)
						          .addEvent(DetermineTurnResultsEvent.class) // BET, ACTIVE CASINO
						          .addEvent(UpdatePlayerCubitsAndSecuredLoanEvent.class) // CUBITS
						          .addEvent(UpdateCardGameDetailsEvent.class) // NEXT ACTIVE CASINO
						          .addEvent(UpdateCasinoForTurnAndBetEvent.class) // BET*/

                        .addStateMachine(this.stateMachine)
                        .build();

                List<EventState> possiblePUT__PASS_TURNStates = new ArrayList<>();
                possiblePUT__PASS_TURNStates.add(EventState.PLAYING);
                try {
                    stateMachine.checkAll(possiblePUT__PASS_TURNStates);
                } catch (Exception e) {
                    // make error response
                    responseBuilder = QasinoResponse.builder();
                    ErrorResponse error = new ErrorResponse(HttpError.CODE_CONFLICT, "init, setup or shuffle", "turn", "state");
                    responseBuilder.errorCode(error.getCode());
                    responseBuilder.errorMessage(error.getMessage());
                    responseBuilder.solution(error.getSolution());
                    responseBuilder.reason(QasinoResponse.Reason.FAILURE);
                    return responseBuilder.build();
                }

                flowDTO.processPathAndQueryParamsAndTrigger(pathAndQueryData, EventTrigger);
                flowDTO.start();
                // state PLAYING or still in IS_SHUFFLED
                // TODO determine event for state GameWon, NoMoreCards or RoundsEnded
                break;

            case LEAVE:

                // EventTrigger is OK

                //DELETE    api/cardgames/1                           // deletes the hands, decks, players, casinos and finally the game
                //DELETE    api/cardgames/1/players                   // deletes all players as resource
                //DELETE    api/cardgames/1/cards                     // gives cards in the deck as resources
                //DELETE    api/cardgames/1/players/2                 // gives a player as resource with resourceId
                //DELETE    api/cardgames/1/players/2/cards           // gives a player as resource with resourceId and cards in the hand as extraResource


                // reinstate get the card game and adds it as context to flowDTO
                flowDTO = builder
                        .addContext(super.reinstate(Integer.parseInt(pathAndQueryData.get("gameId"))))
/*						          .addEvent(DeleteCardGameEvent.class)*/
                        .addStateMachine(this.stateMachine)
                        .build();
                flowDTO.processPathAndQueryParamsAndTrigger(pathAndQueryData, EventTrigger);
                try {
                    flowDTO.start();
                } catch (Exception e) {
                    // make error response
                    responseBuilder = QasinoResponse.builder();
                    ErrorResponse error = new ErrorResponse(HttpError.NOT_FOUND_CARDGAME, "valid cardgame", "invalid id", "id");
                    responseBuilder.errorCode(error.getCode());
                    responseBuilder.errorMessage(error.getMessage());
                    responseBuilder.solution(error.getSolution());
                    responseBuilder.reason(QasinoResponse.Reason.FAILURE);
                    return responseBuilder.build();
                }

                break;

            default:

                // EventTrigger is GET

                //GET    api/cardgames/1
                //GET    api/cardgames/1/player                    // gives active casino (resource=player)
                //GET    api/cardgames/1/players                   // gives all casinos (resource=players)
                //GET    api/cardgames/1/cards                     // gives all decks (resources=cards)
                //GET    api/cardgames/1/players/2                 // gives a specific casino (resource=players, resourceId=int)
                //GET    api/cardgames/1/players/2/cards           // gives a all hands for a player (resource=players, resourceId=int, extraResource=cards)

                // reinstate get the card game and adds it as context to flowDTO
                flowDTO = builder
/*						          .addEvent(GetCardGameDetailsEvent.class)*/
                        .addStateMachine(this.stateMachine)
                        .build();
                flowDTO.processPathAndQueryParamsAndTrigger(pathAndQueryData, EventTrigger);

                try {
                    flowDTO.start();
                } catch (Exception e) {
                    // make error response
                    responseBuilder = QasinoResponse.builder();
                    ErrorResponse error = new ErrorResponse(HttpError.NOT_FOUND_CARDGAME, "valid cardgame", "invalid id", "id");
                    responseBuilder.errorCode(error.getCode());
                    responseBuilder.errorMessage(error.getMessage());
                    responseBuilder.solution(error.getSolution());
                    responseBuilder.reason(QasinoResponse.Reason.FAILURE);
                    return responseBuilder.build();
                }

                // state does not matter

                break;
        }


        // generic tasks
        message = String.format("CardGameController EventTrigger without transition (is done in event) is: %s", EventTrigger);
        log.info(message);


        // make response
        responseBuilder = QasinoResponse.builder();
        if (EventTrigger != EventTrigger.CRASH) {
            message = String.format("CardGameController convertFromGameEntity is: %s", flowDTO.getCurrentGame());
            log.info(message);
            //responseBuilder.cardGame(mapUtil.convertFromGameEntity(flowDTO.getCurrentGame()));
        }

        // TODO more mappings

        // TODO make rules
        final Integer rulesCode = flowDTO.getRulesCode();
        if (rulesCode != null && rulesCode != 0) {
            responseBuilder.errorCode(rulesCode.toString());
            responseBuilder.reason(QasinoResponse.Reason.FAILURE);
        } else {
            responseBuilder.errorCode(null);
            responseBuilder.errorMessage(null);
            responseBuilder.solution(null);
            responseBuilder.reason(QasinoResponse.Reason.SUCCESS);

        }

        return responseBuilder.build();


    }
}

