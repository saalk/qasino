package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CreateNewLeagueAction;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.repository.LeagueRepository;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class LeagueThymeleafController extends AbstractThymeleafController {

    private static final String VISITOR_VIEW_LOCATION = "pages/visitor";
    private static final String SETUP_VIEW_LOCATION = "pages/setup";
    private static final String PLAY_VIEW_LOCATION = "pages/play";
    private static final String LEAGUE_VIEW_LOCATION = "pages/league";

    private LeagueRepository leagueRepository;

    EventOutput.Result output;

    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    CreateNewLeagueAction createNewLeagueAction;
    @Autowired
    FindVisitorIdByAliasOrUsernameAction findVisitorIdByAliasOrUsernameAction;

    @Autowired
    public LeagueThymeleafController(
            LeagueRepository leagueRepository
    ) {
        this.leagueRepository = leagueRepository;
    }

    @GetMapping("/league/{leagueId}")
    public String getLeague(
            Model model,
            Principal principal,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,
            @PathVariable("leagueId") String id
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("leagueId", id);
        flowDTO.setPathVariables("username", principal.getName());
        findVisitorIdByAliasOrUsernameAction.perform(flowDTO);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:league/" + id;
        }
        // get all entities
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: visitor");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return LEAGUE_VIEW_LOCATION;
    }

    @PostMapping(value = "league")
    public String putLeague(
            Model model,
            Principal principal,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "leagueName", qasinoResponse.getPageLeague().getSelectedLeague().getName()
        );
        flowDTO.setPathVariables("username", principal.getName());
        findVisitorIdByAliasOrUsernameAction.perform(flowDTO);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/visitor";
        }
        // 3 - process
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:/visitor";
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create - League for Visitor
        output = createNewLeagueAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:/visitor";
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: league");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:league/" + flowDTO.getQasinoResponse().getPageLeague().getSelectedLeague().getLeagueId();
    }

    @DeleteMapping("/league/{leagueId}")
    public String deleteLeague(
            Model model,
            Principal principal,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,
            @PathVariable("leagueId") String id
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("leagueId", id);
        flowDTO.setPathVariables("username", principal.getName());
        findVisitorIdByAliasOrUsernameAction.perform(flowDTO);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:/visitor";
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // delete
        // TODO check if league does not have games any more..
        leagueRepository.deleteById(flowDTO.getSuppliedLeagueId());
        flowDTO.setQasinoGameLeague(null);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("DeleteMapping: /league/{leagueId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:/visitor";
    }
}


