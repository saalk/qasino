package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CalculateAndFinishGameAction;
import cloud.qasino.games.action.IsGameConsistentForGameEventAction;
import cloud.qasino.games.action.IsGameFinishedAction;
import cloud.qasino.games.action.IsPlayerHumanAction;
import cloud.qasino.games.action.IsPlayingConsistentForPlayEventAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDtoAction;
import cloud.qasino.games.action.PlayFirstMoveAction;
import cloud.qasino.games.action.PlayNextBotMoveAction;
import cloud.qasino.games.action.PlayNextHumanMoveAction;
import cloud.qasino.games.action.StartGameForTypeAction;
import cloud.qasino.games.action.StopGameAction;
import cloud.qasino.games.action.UpdateFichesForPlayerAction;
import cloud.qasino.games.action.UpdatePlayingStateForGame;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.PlayingRepository;
import cloud.qasino.games.dto.QasinoFlowDto;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
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

import static cloud.qasino.games.pattern.statemachine.event.EventOutput.Result.FAILURE;
import static cloud.qasino.games.pattern.statemachine.event.EventOutput.Result.SUCCESS;

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class PlayingThymeleafController extends AbstractThymeleafController {

    EventOutput.Result result;

    @Autowired
    IsGameConsistentForGameEventAction isGameConsistentForGameEventAction;
    @Autowired
    IsPlayingConsistentForPlayEventAction isPlayingConsistentForPlayEventAction;
    @Autowired
    IsGameFinishedAction isGameFinishedAction;
    @Autowired
    UpdateFichesForPlayerAction updateFichesForPlayerAction;
    @Autowired
    CalculateAndFinishGameAction calculateAndFinishGameAction;
    @Autowired
    StartGameForTypeAction startGameForTypeAction;
    @Autowired
    PlayFirstMoveAction playFirstMoveAction;
    @Autowired
    StopGameAction stopGameAction;
    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    MapQasinoGameTableFromDtoAction mapQasinoGameTableFromDtoAction;
    @Autowired
    PlayNextHumanMoveAction playNextHumanMoveAction;
    @Autowired
    IsPlayerHumanAction isPlayerHumanAction;
    @Autowired
    PlayNextBotMoveAction playNextBotMoveAction;
    @Autowired
    UpdatePlayingStateForGame updatePlayingStateForGame;


    @Autowired
    public PlayingThymeleafController(
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            PlayingRepository playingRepository,
            CardMoveRepository cardMoveRepository
    ) {
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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "gameEvent", "shuffle"
        );
        // 2 - validate input
        if (!flowDto.isInputValid()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDto);
        result = isGameConsistentForGameEventAction.perform(flowDto);
        if (FAILURE.equals(result)) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + flowDto.getSuppliedGameId();
        }
        startGameForTypeAction.perform(flowDto);
        mapQasinoGameTableFromDtoAction.perform(flowDto);
        playFirstMoveAction.perform(flowDto);
        // get all entities and build reponse
        loadEntitiesToDtoAction.perform(flowDto);
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        return "redirect:/setup/" + flowDto.getSuppliedGameId();
    }

    @PostMapping(value = "play/{playEvent}/{gameId}")
    public String playerMakesAMoveForAGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,
            @PathVariable("playEvent") String trigger) {

        log.warn("PostMapping: play/{playEvent}/{gameId}");

        // 1 - map input
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id,
//                "turnPlayerId", String.valueOf(qasinoResponse.getPageGamePlay().getTable().getPlaying().getPlayerId()),
                "gameEvent", "play",
                "playEvent", trigger.toLowerCase()
        );
        // 2 - validate input
        if (!flowDto.isInputValid()) {
            log.warn("Errors validateInput!!: {}",
                    flowDto.getPathVariables().toString());
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/play/" + id;
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDto);
        mapQasinoGameTableFromDtoAction.perform(flowDto);

        // logic
        result = isGameConsistentForGameEventAction.perform(flowDto);
        if (EventOutput.Result.FAILURE.equals(result)) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/play/" + qasinoResponse.getPageGamePlay().getSelectedGame().getGameId();
        }
        result = isPlayingConsistentForPlayEventAction.perform(flowDto);
        if (EventOutput.Result.FAILURE.equals(result)) {
            log.warn("Errors isPlayingConsistentForPlayEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/play/" + flowDto.getSuppliedGameId();
        }
//        result = canPlayerStillPlay.perform(flowDto); // for now stop after one round
        mapQasinoGameTableFromDtoAction.perform(flowDto);
        result = isPlayerHumanAction.perform(flowDto);
        updatePlayingStateForGame.perform(flowDto);
        if (SUCCESS.equals(result)) {
            log.warn("playNextHumanMoveAction {}", flowDto.getSuppliedGameEvent());
            playNextHumanMoveAction.perform(flowDto);
        } else {
            log.warn("playNextBotMoveAction {}", flowDto.getSuppliedGameEvent());
            playNextBotMoveAction.perform(flowDto);
        }
        updateFichesForPlayerAction.perform(flowDto);
        result = isGameFinishedAction.perform(flowDto);
        if (SUCCESS.equals(result)) {
            result = calculateAndFinishGameAction.perform(flowDto);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
//        log.warn("qasinoResponse: {}", flowDto.getQasinoResponse());
        log.warn("model !! {}", model);
        return "redirect:/play/" + flowDto.getSuppliedGameId();
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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id,
                "gameEvent", "stop"
        );
        // 2 - validate input
        if (!flowDto.isInputValid()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + flowDto.getSuppliedGameId();
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDto);
        result = isGameConsistentForGameEventAction.perform(flowDto);
        if (FAILURE.equals(result)) {
            log.warn("264 Errors isGameConsistentForGameEvent!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + flowDto.getSuppliedGameId();
        }
        result = stopGameAction.perform(flowDto);
        if (SUCCESS.equals(result)) {
            result = calculateAndFinishGameAction.perform(flowDto);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        return "redirect:/visitor";
    }
}
