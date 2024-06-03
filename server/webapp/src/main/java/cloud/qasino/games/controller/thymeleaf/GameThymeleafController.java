package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CreateNewGameAction;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.IsGameConsistentForGameEventAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.PrepareGameAction;
import cloud.qasino.games.action.UpdateStyleForGame;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.response.QasinoResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
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

    private static final String VISITOR_VIEW_LOCATION = "pages/visitor";
    private static final String SETUP_VIEW_LOCATION = "pages/setup";
    private static final String PLAY_VIEW_LOCATION = "pages/play";

    @Autowired
    PlayerService playerService;

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    EventOutput.Result result;

    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    IsGameConsistentForGameEventAction isGameConsistentForGameEventAction;
    @Autowired
    CreateNewGameAction createNewGameAction;
    @Autowired
    PrepareGameAction prepareGameAction;
    @Autowired
    FindVisitorIdByAliasOrUsernameAction findVisitorIdByAliasOrUsernameAction;
    @Autowired
    UpdateStyleForGame updateStyleForGame;


    @Autowired
    public GameThymeleafController(
            GameRepository gameRepository,
            PlayerRepository playerRepository) {

        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @GetMapping("setup/{gameId}")
    public String getGameSetup(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        log.warn("GetMapping: setup/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id);
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return SETUP_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDTO.getQasinoResponse()));
        log.warn("model !! {}", model);
        return SETUP_VIEW_LOCATION;
    }

    @GetMapping("play/{gameId}")
    public String getGamePlay(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        log.warn("GetMapping: play/{gameId}");
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id);
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return PLAY_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDTO.getQasinoResponse()));
        log.warn("model !! {}", model);
        return PLAY_VIEW_LOCATION;
    }

    @PostMapping("start/{gameId}")
    public String startGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        log.warn("PostMapping: start/{visitorId}");
//        log.warn("QasinoResponse {} !!", qasinoResponse );

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "ante", String.valueOf(qasinoResponse.getPageGameSetup().getSelectedGame().getAnte()),
                "type", qasinoResponse.getPageGameSetup().getSelectedGame().getType().getLabel(),
                "style", qasinoResponse.getPageGameSetup().getSelectedGame().getStyle(),
                "avatar", qasinoResponse.getPageGameSetup().getHumanPlayer().getAvatar().getLabel(),
                "gameEvent", "start"
        );
        if (qasinoResponse.getParams().getLid() > 0) {
            flowDTO.setPathVariables("leagueId", String.valueOf(qasinoResponse.getParams().getLid()));
        }
        if (qasinoResponse.getPageGameSetup().getBotPlayer() != null) {
            flowDTO.setPathVariables("aiLevel", qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel());
        }
        // 2 - validate input
        if (!flowDTO.isInputValid()) {
//            log.warn("Errors validateInput!!: {}", errors);
            log.warn("QasinoResponse {} !!", qasinoResponse );
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            throw new MyBusinessException("validateInput problem [" + flowDTO.getErrorMessage() + "]");
//            return "redirect:/visitor";
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDTO);
        if (FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
//            return "redirect:/visitor";
            throw new MyBusinessException("loadEntitiesToDtoAction problem [" + flowDTO.getErrorMessage() + "]");

//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create or update the game
        result = isGameConsistentForGameEventAction.perform(flowDTO);
        if (FAILURE.equals(result)) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/visitor";
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        createNewGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDTO.getQasinoResponse()));
//        log.warn("model !! {}", model);
        return "redirect:/visitor";
    }

    @PostMapping("validate/{gameId}")
    public String validateGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        log.warn("PostMapping: validate/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "ante", String.valueOf(qasinoResponse.getPageGameSetup().getSelectedGame().getAnte()),
                "gameEvent", "validate"
        );
        if (qasinoResponse.getParams().getLid() > 0) {
            flowDTO.setPathVariables("leagueId", String.valueOf(qasinoResponse.getParams().getLid()));
        }
        if ((qasinoResponse.getPageGameSetup().getBotPlayer() != null && qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel() != null)) {
            flowDTO.setPathVariables("aiLevel", qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel());
        }
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            log.warn("Model !!: {}", model);
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDTO);
        if (FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        result = isGameConsistentForGameEventAction.perform(flowDTO);
        if (FAILURE.equals(result)) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // update game
        prepareGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDTO.getQasinoResponse()));
//        log.warn("model !! {}", model);
        return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
    }

    @PostMapping("style/{gameId}")
    public String styleGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        log.warn("PostMapping: style/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "gameEvent", "validate"
        );
        if ((qasinoResponse.getPageGameSetup().getAnteToWin() != null)) {
            flowDTO.setPathVariables("anteToWin", qasinoResponse.getPageGameSetup().getAnteToWin().name());
        }
        if ((qasinoResponse.getPageGameSetup().getBettingStrategy() != null)) {
            flowDTO.setPathVariables("bettingStrategy", qasinoResponse.getPageGameSetup().getBettingStrategy().name());
        }
        if ((qasinoResponse.getPageGameSetup().getDeckConfiguration() != null)) {
            flowDTO.setPathVariables("deckConfiguration", qasinoResponse.getPageGameSetup().getDeckConfiguration().name());
        }
        if ((qasinoResponse.getPageGameSetup().getOneTimeInsurance() != null)) {
            flowDTO.setPathVariables("oneTimeInsurance", qasinoResponse.getPageGameSetup().getOneTimeInsurance().name());
        }
        if ((qasinoResponse.getPageGameSetup().getRoundsToWin() != null)) {
            flowDTO.setPathVariables("roundsToWin", qasinoResponse.getPageGameSetup().getRoundsToWin().name());
        }
        if ((qasinoResponse.getPageGameSetup().getTurnsToWin() != null)) {
            flowDTO.setPathVariables("turnsToWin", qasinoResponse.getPageGameSetup().getTurnsToWin().name());
        }
        // 2 - validate input
        if (!flowDTO.isInputValid() && errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            log.warn("Model !!: {}", model);
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDTO);
        if (FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + id;
        }
        updateStyleForGame.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDTO.getQasinoResponse()));
//        log.warn("model !! {}", model);
        return "redirect:/setup/" + id;
    }


    @PostMapping("bot/{gameId}")
    public String addBotPLayerForAGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response) {

        log.warn("PostMapping: bot/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "avatar", qasinoResponse.getPageGameSetup().getBotPlayer().getAvatar().getLabel(),
                "aiLevel", qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel()
        );
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDTO);
        Player bot = playerService.addBotPlayerToAGame(flowDTO.getQasinoGame(), flowDTO.getSuppliedAvatar(), flowDTO.getSuppliedAiLevel());
        playerRepository.save(bot);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDTO.getQasinoResponse()));
//        log.warn("model !! {}", model);
        return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
    }

    @DeleteMapping("game/{gameId}")
    public String deleteGame(
            Model model,
            Principal principal,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,
            @PathVariable("gameId") String id
    ) {
        log.warn("DeleteMapping: game/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id
        );
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/visitor";
        }
        // 3 - process
        // get all entities
        result = loadEntitiesToDtoAction.perform(flowDTO);
        if (FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/visitor";
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // delete
        gameRepository.deleteById(flowDTO.getSuppliedGameId());
        flowDTO.setQasinoGame(null);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDTO.getQasinoResponse()));
//        log.warn("model !! {}", model);
        return "redirect:/visitor";
    }
}


