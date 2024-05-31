package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CalculateAndFinishGameAction;
import cloud.qasino.games.action.CalculateQasinoStatistics;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.IsGameConsistentForGameEvent;
import cloud.qasino.games.action.IsGameFinished;
import cloud.qasino.games.action.IsPlayerHuman;
import cloud.qasino.games.action.IsTurnConsistentForTurnEvent;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDto;
import cloud.qasino.games.action.MapQasinoResponseFromDto;
import cloud.qasino.games.action.PlayFirstTurnAction;
import cloud.qasino.games.action.StartGameForType;
import cloud.qasino.games.action.PlayNextBotTurnAction;
import cloud.qasino.games.action.PlayNextHumanTurnAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.StopGameAction;
import cloud.qasino.games.action.UpdateFichesForPlayer;
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

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class TurnAndCardMoveThymeleafController extends AbstractThymeleafController {

    EventOutput.Result output;

    @Autowired
    IsGameConsistentForGameEvent isGameConsistentForGameEvent;
    @Autowired
    IsTurnConsistentForTurnEvent isTurnConsistentForTurnEvent;
    @Autowired
    IsGameFinished isGameFinished;
    @Autowired
    UpdateFichesForPlayer updateFichesForPlayer;
    @Autowired
    CalculateAndFinishGameAction calculateAndFinishGameAction;
    @Autowired
    StartGameForType startGameForType;
    @Autowired
    PlayFirstTurnAction playFirstTurnAction;
    @Autowired
    StopGameAction stopGameAction;
    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateQasinoStatistics calculateQasinoStatistics;
    @Autowired
    MapQasinoResponseFromDto mapQasinoResponseFromDto;
    @Autowired
    MapQasinoGameTableFromDto mapQasinoGameTableFromDto;
    @Autowired
    PlayNextHumanTurnAction playNextHumanTurnAction;
    @Autowired
    IsPlayerHuman isPlayerHuman;
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
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "gameEvent", "shuffle"
        );
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        startGameForType.perform(flowDTO);
        mapQasinoGameTableFromDto.perform(flowDTO);
        playFirstTurnAction.perform(flowDTO);
        // get all entities and build reponse
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("PostMapping: shuffle/{gameId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
    }

    @PostMapping(value = "turn/{turnEvent}/{gameId}")
    public String playerMakesAMoveForAGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,
            @PathVariable("turnEvent") String trigger) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id,
//                "turnPlayerId", String.valueOf(qasinoResponse.getPageGamePlay().getTable().getCurrentTurn().getActivePlayerId()),
                "gameEvent", "turn",
                "turnEvent", trigger
        );
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/play/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/play/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
        mapQasinoGameTableFromDto.perform(flowDTO);

        // logic
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/play/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
        output = isTurnConsistentForTurnEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors isTurnConsistentForTurnEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/play/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
//        output = canPlayerStillPlay.perform(flowDTO); // for now stop after one round
        mapQasinoGameTableFromDto.perform(flowDTO);
        output = isPlayerHuman.perform(flowDTO);
        if (output == EventOutput.Result.SUCCESS) {
            playNextHumanTurnAction.perform(flowDTO);
        } else {
            playNextBotTurnAction.perform(flowDTO);
        }
        updateFichesForPlayer.perform(flowDTO);
        output = isGameFinished.perform(flowDTO);
        if (output == EventOutput.Result.SUCCESS) {
            output = calculateAndFinishGameAction.perform(flowDTO);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("PostMapping: turn/{turnEvent}/{gameId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        return "redirect:/play/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
    }

    @PostMapping(value = "stop/{gameId}")
    public String stopPlayingTheGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id,
                "gameEvent", "stop"
        );
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
        // logic
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
        stopGameAction.perform(flowDTO);
        if (output == EventOutput.Result.SUCCESS) {
            output = calculateAndFinishGameAction.perform(flowDTO);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("PostMapping: stop/{gameId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        return "redirect:/visitor";
    }
}

