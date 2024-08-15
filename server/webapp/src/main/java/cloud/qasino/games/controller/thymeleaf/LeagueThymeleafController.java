package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.action.dto.load.LoadPrincipalDtoAction;
import cloud.qasino.games.action.league.CreateNewLeagueAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
public class LeagueThymeleafController extends AbstractThymeleafController {

    // @formatter:off
    private static final String LEAGUE_VIEW_LOCATION = "pages/league";

    EventOutput.Result output;
    private LeagueRepository leagueRepository;

    @Autowired LoadPrincipalDtoAction loadVisitor;
    @Autowired CreateNewLeagueAction createNewLeagueAction;
    // @formatter:on

    @Autowired
    public LeagueThymeleafController(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    @GetMapping("/league/{leagueId}")
    public String getLeague(
            Principal principal,
            Model model,
            @PathVariable("leagueId") String id,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedLeagueId(Long.parseLong(id));
        // 2 - validate input
        // 3 - process
        loadVisitor.perform(qasino);
        // 4 - return  response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return LEAGUE_VIEW_LOCATION;
    }

    @PostMapping(value = "league")
    public String putLeague(
            Principal principal,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.CREATE_LEAGUE);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        // "leagueName"
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        // create - League for Visitor
        output = createNewLeagueAction.perform(qasino);
        if (FAILURE.equals(output)) {
            log.warn("Errors createNewLeagueAction!!");
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return "redirect:/visitor";
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:league/" + qasino.getLeague().getLeagueId();
    }

    @DeleteMapping("/league/{leagueId}")
    public String deleteLeague(
            Principal principal,
            @PathVariable("leagueId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.DELETE_LEAGUE);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedLeagueId(Long.parseLong(id));
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        // TODO check if league does not have games any more..
        leagueRepository.deleteById(qasino.getParams().getSuppliedLeagueId());
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }
}


