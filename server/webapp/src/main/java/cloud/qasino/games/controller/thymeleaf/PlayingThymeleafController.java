package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.action.dto.load.LoadPrincipalDtoAction;
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
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.PlayingRepository;
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

    @Autowired
    public PlayingThymeleafController(
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            PlayingRepository playingRepository,
            CardMoveRepository cardMoveRepository) {
    }

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
            log.warn("Errors isGameConsistentForGameEvent!!");
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return "redirect:/setup/" + qasino.getParams().getSuppliedGameId();
        }
        startGameForTypeAction.perform(qasino);
        playFirstMoveAction.perform(qasino);
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
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = isGameConsistentForGameEventAction.perform(qasino);
        if (EventOutput.Result.FAILURE.equals(output)) {
            log.warn("Errors isGameConsistentForGameEvent!!");
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return "redirect:/play/" + qasino.getParams().getSuppliedGameId();
        }
        output = isPlayingConsistentForPlayEventAction.perform(qasino);
        if (EventOutput.Result.FAILURE.equals(output)) {
            log.warn("Errors isPlayingConsistentForPlayEvent!!: ");
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return "redirect:/play/" + qasino.getParams().getSuppliedGameId();
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
        updateFichesForPlayerAction.perform(qasino);
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
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = isGameConsistentForGameEventAction.perform(qasino);
        if (FAILURE.equals(output)) {
            log.warn("Errors isGameConsistentForGameEvent!!");
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return "redirect:/setup/" + qasino.getParams().getSuppliedGameId();
        }
        output = stopGameAction.perform(qasino);
        if (SUCCESS.equals(output)) {
            output = calculateAndFinishGameAction.perform(qasino);
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }
}

