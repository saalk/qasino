package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CreateNewGameAction;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.IsGameConsistentForGameEventAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.PrepareGameAction;
import cloud.qasino.games.action.UpdateStyleForGame;
import cloud.qasino.games.action.dto.FindAllDtosForUsernameAction;
import cloud.qasino.games.action.dto.MapQasinoFromDtosAction;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.service.PlayerServiceOld;
import cloud.qasino.games.dto.QasinoFlowDto;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.response.QasinoResponse;
import com.google.gson.Gson;
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
    PlayerServiceOld playerServiceOld;

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
    FindAllDtosForUsernameAction findDtos;
    @Autowired
    MapQasinoFromDtosAction mapQasino;

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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id);
        // 2 - validate input
        if (!flowDto.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return SETUP_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDto.getQasinoResponse()));
//        log.warn("model !! {}", model);
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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id);
        // 2 - validate input
        if (!flowDto.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return PLAY_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return new response
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        prepareQasino(response, qasino);
        var gson = new Gson();
        log.warn("Qasino gson = {} ", gson.toJson(qasino));
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("QasinoResponse = {} ", gson.toJson(flowDto.getQasinoResponse()));

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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "ante", String.valueOf(qasinoResponse.getPageGameSetup().getSelectedGame().getAnte()),
                "type", qasinoResponse.getPageGameSetup().getSelectedGame().getType().getLabel(),
                "style", qasinoResponse.getPageGameSetup().getSelectedGame().getStyle(),
                "avatar", qasinoResponse.getPageGameSetup().getHumanPlayer().getAvatar().getLabel(),
                "gameEvent", "start"
        );
        if (qasinoResponse.getParams().getLid() > 0) {
            flowDto.setPathVariables("leagueId", String.valueOf(qasinoResponse.getParams().getLid()));
        }
        if (qasinoResponse.getPageGameSetup().getBotPlayer() != null) {
            flowDto.setPathVariables("aiLevel", qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel());
        }
        // 2 - validate input
        if (!flowDto.isInputValid()) {
//            log.warn("Errors validateInput!!: {}", errors);
            log.warn("QasinoResponse {} !!", qasinoResponse );
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            throw new MyBusinessException("validateInput problem [" + flowDto.getErrorMessage() + "]");
//            return "redirect:/visitor";
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDto);
        if (FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
//            return "redirect:/visitor";
            throw new MyBusinessException("loadEntitiesToDtoAction problem [" + flowDto.getErrorMessage() + "]");

//            return ResponseEntity.status(HttpStatus.valueOf(flowDto.getHttpStatus())).headers(flowDto.getHeaders()).build();
        }
        // create or update the game
        result = isGameConsistentForGameEventAction.perform(flowDto);
        if (FAILURE.equals(result)) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/visitor";
//            return ResponseEntity.status(HttpStatus.valueOf(flowDto.getHttpStatus())).headers(flowDto.getHeaders()).build();
        }
        createNewGameAction.perform(flowDto);
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDto.getQasinoResponse()));
//        log.warn("model !! {}", model);
        return "redirect:/setup/" + flowDto.getQasinoGame().getGameId();
//        return "redirect:/visitor";
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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "ante", String.valueOf(qasinoResponse.getPageGameSetup().getSelectedGame().getAnte()),
                "gameEvent", "validate"
        );
        if (qasinoResponse.getParams().getLid() > 0) {
            flowDto.setPathVariables("leagueId", String.valueOf(qasinoResponse.getParams().getLid()));
        }
        if ((qasinoResponse.getPageGameSetup().getBotPlayer() != null && qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel() != null)) {
            flowDto.setPathVariables("aiLevel", qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel());
        }
        // 2 - validate input
        if (!flowDto.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            log.warn("Model !!: {}", model);
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDto);
        if (FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.valueOf(flowDto.getHttpStatus())).headers(flowDto.getHeaders()).build();
        }
        result = isGameConsistentForGameEventAction.perform(flowDto);
        if (FAILURE.equals(result)) {
            log.warn("Errors isGameConsistentForGameEvent!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.valueOf(flowDto.getHttpStatus())).headers(flowDto.getHeaders()).build();
        }
        // update game
        prepareGameAction.perform(flowDto);
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDto.getQasinoResponse()));
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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "gameEvent", "validate"
        );
        if ((qasinoResponse.getPageGameSetup().getAnteToWin() != null)) {
            flowDto.setPathVariables("anteToWin", qasinoResponse.getPageGameSetup().getAnteToWin().name());
        }
        if ((qasinoResponse.getPageGameSetup().getBettingStrategy() != null)) {
            flowDto.setPathVariables("bettingStrategy", qasinoResponse.getPageGameSetup().getBettingStrategy().name());
        }
        if ((qasinoResponse.getPageGameSetup().getDeckConfiguration() != null)) {
            flowDto.setPathVariables("deckConfiguration", qasinoResponse.getPageGameSetup().getDeckConfiguration().name());
        }
        if ((qasinoResponse.getPageGameSetup().getOneTimeInsurance() != null)) {
            flowDto.setPathVariables("oneTimeInsurance", qasinoResponse.getPageGameSetup().getOneTimeInsurance().name());
        }
        if ((qasinoResponse.getPageGameSetup().getRoundsToWin() != null)) {
            flowDto.setPathVariables("roundsToWin", qasinoResponse.getPageGameSetup().getRoundsToWin().name());
        }
        if ((qasinoResponse.getPageGameSetup().getTurnsToWin() != null)) {
            flowDto.setPathVariables("turnsToWin", qasinoResponse.getPageGameSetup().getTurnsToWin().name());
        }
        // 2 - validate input
        if (!flowDto.isInputValid() && errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            log.warn("Model !!: {}", model);
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDto);
        if (FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + id;
        }
        updateStyleForGame.perform(flowDto);
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDto.getQasinoResponse()));
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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "avatar", qasinoResponse.getPageGameSetup().getBotPlayer().getAvatar().getLabel(),
                "aiLevel", qasinoResponse.getPageGameSetup().getBotPlayer().getAiLevel().getLabel()
        );
        // 2 - validate input
        if (!flowDto.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDto);
        Player bot = playerServiceOld.addBotPlayerToAGame(flowDto.getQasinoGame(), flowDto.getSuppliedAvatar(), flowDto.getSuppliedAiLevel());
        playerRepository.save(bot);
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDto.getQasinoResponse()));
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
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "gameId", id
        );
        // 2 - validate input
        if (!flowDto.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/visitor";
        }
        // 3 - process
        // get all entities
        result = loadEntitiesToDtoAction.perform(flowDto);
        if (FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/visitor";
//            return ResponseEntity.status(HttpStatus.valueOf(flowDto.getHttpStatus())).headers(flowDto.getHeaders()).build();
        }
        // delete
        gameRepository.deleteById(flowDto.getSuppliedGameId());
        flowDto.setQasinoGame(null);
        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
//        log.warn("QasinoResponse !! {}", prettyPrintJson(flowDto.getQasinoResponse()));
//        log.warn("model !! {}", model);
        return "redirect:/visitor";
    }
}


