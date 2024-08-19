package cloud.qasino.games.controller;

import cloud.qasino.games.action.common.load.LoadPrincipalDtoAction;
import cloud.qasino.games.action.game.CreateNewGameAction;
import cloud.qasino.games.action.game.IsGameConsistentForGameEventAction;
import cloud.qasino.games.action.game.PrepareGameAction;
import cloud.qasino.games.action.game.UpdateStyleForGame;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.validation.GameBasic;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static cloud.qasino.games.pattern.statemachine.event.EventOutput.Result.FAILURE;

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class GameThymeleafController extends AbstractThymeleafController {

    // @formatter:off
    private static final String SETUP_VIEW_LOCATION = "pages/setup";
    private static final String PLAY_VIEW_LOCATION = "pages/play";
    private static final String ERROR_VIEW_LOCATION = "pages/error";

    EventOutput.Result output;
    private GameRepository gameRepository;
    @Autowired PlayerService playerService;

    @Autowired LoadPrincipalDtoAction loadVisitor;
    @Autowired IsGameConsistentForGameEventAction isGameConsistent;
    @Autowired CreateNewGameAction createNewGame;
    @Autowired PrepareGameAction prepareGame;
    @Autowired UpdateStyleForGame updateGameStyle;
    // @formatter:on

    @Autowired
    public GameThymeleafController(
            GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @PostMapping("new/{visitorId}")
    public String newGame(
            Principal principal,
            @PathVariable("visitorId") String id,
            @Validated(GameBasic.class)
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.START);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedVisitorId(Long.parseLong(id));
//        "ante", "type", "style", "avatar",
        // 2 - validate input
        if (result.hasErrors()) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = isGameConsistent.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        output = createNewGame.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/setup/" + qasino.getGame().getGameId();
//        return "redirect:/visitor";
    }

    @GetMapping("setup/{gameId}")
    public String getGameSetup(
            Principal principal,
            Model model,
            @PathVariable("gameId") String id,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
        // 2 - validate input
        // 3 - process
        loadVisitor.perform(qasino);
        // 4 - return  response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return SETUP_VIEW_LOCATION;
    }

    @PostMapping("validate/{gameId}") // button update and validate is same
    public String validateGame(
            Principal principal,
            Model model,
            @PathVariable("gameId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result, // can only be directly after @Validated/valid
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.VALIDATE);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
//      "ante"
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = isGameConsistent.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        prepareGame.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/setup/" + id;
    }

    @GetMapping("play/{gameId}")
    public String getGamePlay(
            Principal principal,
            Model model,
            @PathVariable("gameId") String id,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
        loadVisitor.perform(qasino);
        // 2 - validate input
        // 3 - process
        // 4 - return  response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return PLAY_VIEW_LOCATION;
    }

    @PostMapping("style/{gameId}")
    public String styleGame(
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.VALIDATE);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = updateGameStyle.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/setup/" + id;
    }

    @PostMapping("bot/{gameId}")
    public String addBotPLayerForAGame(
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.ADD_BOT);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
//        "avatar", "aiLevel"
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        playerService.addBotPlayerToAGame(qasino.getGame(), qasino.getCreation().getSuppliedAvatar(), qasino.getCreation().getSuppliedAiLevel());
        loadVisitor.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/setup/" + id;
    }

    @DeleteMapping("game/{gameId}")
    public String deleteGame(
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.ABANDON);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(id));
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        gameRepository.deleteById(qasino.getParams().getSuppliedGameId());
        loadVisitor.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }
}


