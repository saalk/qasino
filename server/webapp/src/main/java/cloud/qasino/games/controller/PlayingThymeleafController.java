package cloud.qasino.games.controller;

import cloud.qasino.games.action.common.load.LoadPrincipalDtoAction;
import cloud.qasino.games.action.game.CalculateAndFinishGameAction;
import cloud.qasino.games.action.game.IsGameConsistentForGameEventAction;
import cloud.qasino.games.action.game.StartGameForTypeAction;
import cloud.qasino.games.action.game.StopGameAction;
import cloud.qasino.games.action.playing.IsGameFinishedAction;
import cloud.qasino.games.action.playing.IsPlayerHumanAction;
import cloud.qasino.games.action.playing.IsPlayingConsistentForPlayEventAction;
import cloud.qasino.games.action.playing.PlayFirstMoveAction;
import cloud.qasino.games.action.playing.PlayNextBotMoveAction;
import cloud.qasino.games.action.playing.PlayNextHumanMoveAction;
import cloud.qasino.games.action.playing.UpdateFichesForPlayerAction;
import cloud.qasino.games.action.playing.UpdatePlayingStateForGame;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    // @formatter:off
    private static final String ERROR_VIEW_LOCATION = "pages/error";
    EventOutput.Result output;

    @Autowired LoadPrincipalDtoAction loadVisitor;
    @Autowired IsGameConsistentForGameEventAction isGameConsistentForGameEventAction;
    @Autowired IsPlayingConsistentForPlayEventAction isPlayingConsistentForPlayEventAction;
    @Autowired IsGameFinishedAction isGameFinishedAction;
    @Autowired UpdateFichesForPlayerAction updateFichesForPlayerAction;
    @Autowired CalculateAndFinishGameAction calculateAndFinishGameAction;
    @Autowired StartGameForTypeAction startGameForTypeAction;
    @Autowired PlayFirstMoveAction playFirstMoveAction;
    @Autowired StopGameAction stopGameAction;
    @Autowired PlayNextHumanMoveAction playNextHumanMoveAction;
    @Autowired IsPlayerHumanAction isPlayerHumanAction;
    @Autowired PlayNextBotMoveAction playNextBotMoveAction;
    @Autowired UpdatePlayingStateForGame updatePlayingStateForGame;
    // @formatter:on

    @PostMapping(value = "shuffle/{gameId}")
    public String shuffleTheGame(
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.SHUFFLE);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
        // 2 - validate input
        // 3 - process
        loadVisitor.perform(qasino);
        output = isGameConsistentForGameEventAction.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        output = startGameForTypeAction.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        output = playFirstMoveAction.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/setup/" + qasino.getParams().getSuppliedGameId();
    }

    @PostMapping(value = "play/{playEvent}/{gameId}")
    public String playerMakesAMoveForAGame(
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response,
            @PathVariable("playEvent") String trigger) {

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.PLAY);
        qasino.getParams().setSuppliedPlayEvent(PlayEvent.fromLabel(trigger.toLowerCase()));
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
        // 2 - validate input
        if (result.hasErrors()) {
            log.warn("errors in supplied data {}", result);
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = isGameConsistentForGameEventAction.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        output = isPlayingConsistentForPlayEventAction.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
//        result = canPlayerStillPlay.perform(qasino); // for now stop after one round
        output = isPlayerHumanAction.perform(qasino);
        updatePlayingStateForGame.perform(qasino);
        if (SUCCESS.equals(output)) {
            log.warn("playNextHumanMoveAction {}", qasino.getParams().getSuppliedGameEvent());
            playNextHumanMoveAction.perform(qasino);
        } else {
            log.warn("playNextBotMoveAction {}", qasino.getParams().getSuppliedGameEvent());
            playNextBotMoveAction.perform(qasino);
        }
        output = updateFichesForPlayerAction.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        output = isGameFinishedAction.perform(qasino);
        if (SUCCESS.equals(output)) {
            output = calculateAndFinishGameAction.perform(qasino);
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/play/" + qasino.getParams().getSuppliedGameId();
    }

    @PostMapping(value = "stop/{gameId}")
    public String stopPlayingTheGame(
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        log.warn("PostMapping: stop/{gameId}");

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.STOP);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
        // 2 - validate input
        if (result.hasErrors()) {
            log.warn("errors in supplied data {}", result);
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = isGameConsistentForGameEventAction.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        output = stopGameAction.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        if (SUCCESS.equals(output)) {
            output = calculateAndFinishGameAction.perform(qasino);
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }
}

