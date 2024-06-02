package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CalculateAndFinishGameAction;
import cloud.qasino.games.action.CalculateQasinoStatisticsAction;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.IsGameConsistentForGameEventAction;
import cloud.qasino.games.action.IsGameFinishedAction;
import cloud.qasino.games.action.IsPlayerHumanAction;
import cloud.qasino.games.action.IsTurnConsistentForTurnEventAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDtoAction;
import cloud.qasino.games.action.MapQasinoResponseFromDtoAction;
import cloud.qasino.games.action.PlayFirstTurnAction;
import cloud.qasino.games.action.PlayNextBotTurnAction;
import cloud.qasino.games.action.PlayNextHumanTurnAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.StartGameForTypeAction;
import cloud.qasino.games.action.StopGameAction;
import cloud.qasino.games.action.UpdateFichesForPlayerAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.statemachine.event.EventOutput;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static cloud.qasino.games.statemachine.event.EventOutput.Result.FAILURE;
import static cloud.qasino.games.statemachine.event.EventOutput.Result.SUCCESS;

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class TurnAndCardMoveThymeleafController extends AbstractThymeleafController {

    EventOutput.Result result;

    @Autowired
    IsGameConsistentForGameEventAction isGameConsistentForGameEventAction;
    @Autowired
    IsTurnConsistentForTurnEventAction isTurnConsistentForTurnEventAction;
    @Autowired
    IsGameFinishedAction isGameFinishedAction;
    @Autowired
    UpdateFichesForPlayerAction updateFichesForPlayerAction;
    @Autowired
    CalculateAndFinishGameAction calculateAndFinishGameAction;
    @Autowired
    StartGameForTypeAction startGameForTypeAction;
    @Autowired
    PlayFirstTurnAction playFirstTurnAction;
    @Autowired
    StopGameAction stopGameAction;
    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateQasinoStatisticsAction calculateQasinoStatisticsAction;
    @Autowired
    MapQasinoResponseFromDtoAction mapQasinoResponseFromDtoAction;
    @Autowired
    MapQasinoGameTableFromDtoAction mapQasinoGameTableFromDtoAction;
    @Autowired
    PlayNextHumanTurnAction playNextHumanTurnAction;
    @Autowired
    IsPlayerHumanAction isPlayerHumanAction;
    @Autowired
    PlayNextBotTurnAction playNextBotTurnAction;
    @Autowired
    FindVisitorIdByAliasOrUsernameAction findVisitorIdByAliasOrUsernameAction;

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;
    private CardMoveRepository cardMoveRepository;

    @Autowired
    public TurnAndCardMoveThymeleafController(
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository,
            CardMoveRepository cardMoveRepository
    ) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.turnRepository = turnRepository;
        this.cardMoveRepository = cardMoveRepository;
    }

    @PostMapping(value = "shuffle/{gameId}")
    public String shuffleTheGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        log.warn("PostMapping: shuffle/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "gameEvent", "shuffle"
        );
        // 2 - validate input
        if (!flowDTO.isInputValid()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDTO);
        result = isGameConsistentForGameEventAction.perform(flowDTO);
        if (FAILURE.equals(result)) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + flowDTO.getSuppliedGameId();
        }
        startGameForTypeAction.perform(flowDTO);
        mapQasinoGameTableFromDtoAction.perform(flowDTO);
        playFirstTurnAction.perform(flowDTO);
        // get all entities and build reponse
        loadEntitiesToDtoAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        return "redirect:/setup/" + flowDTO.getSuppliedGameId();
    }

    @PostMapping(value = "turn/{turnEvent}/{gameId}")
    public String playerMakesAMoveForAGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,
            @PathVariable("turnEvent") String trigger) {

        log.warn("PostMapping: turn/{turnEvent}/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id,
//                "turnPlayerId", String.valueOf(qasinoResponse.getPageGamePlay().getTable().getCurrentTurn().getActivePlayerId()),
                "gameEvent", "turn",
                "turnEvent", trigger.toLowerCase()
        );
        // 2 - validate input
        if (!flowDTO.isInputValid()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/play/" + id;
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDTO);
        mapQasinoGameTableFromDtoAction.perform(flowDTO);

        // logic
        result = isGameConsistentForGameEventAction.perform(flowDTO);
        if (EventOutput.Result.FAILURE.equals(result)) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/play/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
        result = isTurnConsistentForTurnEventAction.perform(flowDTO);
        if (EventOutput.Result.FAILURE.equals(result)) {
            log.warn("Errors isTurnConsistentForTurnEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/play/" + flowDTO.getSuppliedGameId();
        }
//        result = canPlayerStillPlay.perform(flowDTO); // for now stop after one round
        mapQasinoGameTableFromDtoAction.perform(flowDTO);
        result = isPlayerHumanAction.perform(flowDTO);
        if (SUCCESS.equals(result)) {
            playNextHumanTurnAction.perform(flowDTO);
        } else {
            playNextBotTurnAction.perform(flowDTO);
        }
        updateFichesForPlayerAction.perform(flowDTO);
        result = isGameFinishedAction.perform(flowDTO);
        if (SUCCESS.equals(result)) {
            result = calculateAndFinishGameAction.perform(flowDTO);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:/play/" + flowDTO.getSuppliedGameId();
    }

    @PostMapping(value = "stop/{gameId}")
    public String stopPlayingTheGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        log.warn("PostMapping: stop/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id,
                "gameEvent", "stop"
        );
        // 2 - validate input
        if (!flowDTO.isInputValid()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + flowDTO.getSuppliedGameId();
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDTO);
        result = isGameConsistentForGameEventAction.perform(flowDTO);
        if (FAILURE.equals(result)) {
            log.warn("264 Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + flowDTO.getSuppliedGameId();
        }
        result = stopGameAction.perform(flowDTO);
        if (SUCCESS.equals(result)) {
            result = calculateAndFinishGameAction.perform(flowDTO);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        return "redirect:/visitor";
    }
}

