package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CreateNewGameAction;
import cloud.qasino.games.action.IsGameConsistentForGameEvent;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.PrepareGameAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.statemachine.event.EventOutput;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class GameThymeleafController extends AbstractThymeleafController {

    private static final String VISITOR_VIEW_LOCATION = "pages/visitor";
    private static final String SETUP_VIEW_LOCATION = "pages/setup";
    private static final String PLAY_VIEW_LOCATION = "pages/play";

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    EventOutput.Result output;

    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    IsGameConsistentForGameEvent isGameConsistentForGameEvent;
    @Autowired
    CreateNewGameAction createNewGameAction;
    @Autowired
    PrepareGameAction prepareGameAction;

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
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", String.valueOf(qasinoResponse.getParams().getVid()),
                "gameId", id);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return SETUP_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("GetMapping: setup/{gameId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return SETUP_VIEW_LOCATION;
    }

    @GetMapping("play/{gameId}")
    public String getGamePlay(
            Model model,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", String.valueOf(qasinoResponse.getParams().getVid()),
                "gameId", id);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return PLAY_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("GetMapping: setup/{gameId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return PLAY_VIEW_LOCATION;
    }

    @PostMapping(value = "game/start")
    public String startGame(
            Model model,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        log.warn("PostMapping: /game/start");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", String.valueOf(qasinoResponse.getParams().getVid()),
                "type", qasinoResponse.getPageGameSetup().getSelectedGame().getType().getLabel(),
                "style", qasinoResponse.getPageGameSetup().getSelectedGame().getStyle(),
                "ante", String.valueOf(qasinoResponse.getPageGameSetup().getSelectedGame().getAnte()),
                "avatar", qasinoResponse.getPageGameSetup().getHumanPlayer().getAvatar().getLabel(),

                "gameEvent", "start"
        );
        if (qasinoResponse.getParams().getLid() > 0) {
            flowDTO.setPathVariables("leagueId", String.valueOf(qasinoResponse.getParams().getLid()));
        }

        if (qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel() != null) {
            flowDTO.setPathVariables("aiLevel", qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel());
        }
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            log.warn("Errors exist!!: {}", errors);
            log.warn("HttpServletResponse: {}", response.getHeaderNames());
            log.warn("Model: {}", model);
            log.warn("Errors: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/visitor/" + flowDTO.getSuppliedVisitorId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:/visitor/" + flowDTO.getSuppliedVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create or update the game
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:/visitor/" + flowDTO.getSuppliedVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        createNewGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("PostMapping: /game/start");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        return "redirect:/visitor/" + flowDTO.getSuppliedVisitorId();
    }

    @PostMapping(value = "/game/validate")
    public String validateGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", String.valueOf(qasinoResponse.getParams().getVid()),
                "type", qasinoResponse.getPageGameSetup().getSelectedGame().getType().getLabel(),
                "style", qasinoResponse.getPageGameSetup().getSelectedGame().getStyle(),
                "ante", String.valueOf(qasinoResponse.getPageGameSetup().getSelectedGame().getAnte()),
                "avatar", qasinoResponse.getPageGameSetup().getHumanPlayer().getAvatar().getLabel(),

                "gameEvent", "validate"
        );
        if (qasinoResponse.getParams().getLid() > 0) {
            flowDTO.setPathVariables("leagueId", String.valueOf(qasinoResponse.getParams().getLid()));
        }

        if (qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel() != null) {
            flowDTO.setPathVariables("aiLevel", qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel());
        }
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // update game
        prepareGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: /game/validate");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
    }

    @PostMapping(value = "/game/add/bot")
    public String addBotPLayerForAGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id,
            @RequestParam(name = "aiLevel", defaultValue = "average") String aiLevel,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar) {

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", String.valueOf(qasinoResponse.getPageVisitor().getSelectedVisitor().getVisitorId()),
                "gameId", String.valueOf(qasinoResponse.getPageGameSetup().getSelectedGame().getGameId()),
                "aiLevel", String.valueOf(qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel()),
                "avatar", qasinoResponse.getPageGameSetup().getBotPlayer().getAvatar().getLabel(),

                "gameEvent", "validate"
        );
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        // rules - AiLevel bot cannot be Human
        if (AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.HUMAN)
            // todo LOW split username and number
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        //            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();
        long gameId = Long.parseLong(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        // logic
        int sequenceCalculated = playerRepository.countByGame(foundGame.get()) + 1;
        int fiches = (int) (Math.random() * 1000 + 1);

        Player createdAi = new Player(
                null, linkedGame, Role.BOT, fiches, sequenceCalculated,
                Avatar.fromLabelWithDefault(avatar), AiLevel.fromLabelWithDefault(aiLevel));
        createdAi = playerRepository.save(createdAi);
        if (createdAi.getPlayerId() == 0) {
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: /game/add/bot");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
    }

    @DeleteMapping("/game/{gameId}")
    public String deleteGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "gameId", id);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // 3 - process
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:/visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // delete
        gameRepository.deleteById(flowDTO.getSuppliedGameId());
        flowDTO.setQasinoGame(null);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: visitor/{visitorId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:/visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }

}


