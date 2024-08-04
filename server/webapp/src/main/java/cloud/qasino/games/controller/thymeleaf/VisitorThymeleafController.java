package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.HandleSecuredLoanAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.SignUpNewVisitorAction;
import cloud.qasino.games.action.UpdateVisitorAction;
import cloud.qasino.games.action.dto.HandleSecuredLoanNewAction;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.action.dto.UpdateVisitorNewAction;
import cloud.qasino.games.action.dto.load.LoadPrincipalDtoAction;
import cloud.qasino.games.action.dto.load.LoadVisitorDtoAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.QasinoFlowDto;
import cloud.qasino.games.dto.validation.VisitorBasic;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import cloud.qasino.games.response.QasinoResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static cloud.qasino.games.pattern.statemachine.event.EventOutput.Result.FAILURE;

// basic path /qasino
//
// 200 - ok
// 201 - created
// 400 - bad request - error/reason "url ... not available"
// 404 - not found - error/message "invalid value x for y" + reason [missing]
// 412 - precondition failed = error/message - "violation of rule z"
// 500 - internal server error

@Controller
@ControllerAdvice
@Slf4j
public class VisitorThymeleafController extends AbstractThymeleafController {

    // formatter:of
    private static final String VISITOR_VIEW_LOCATION = "pages/visitor";
    private static final String SETUP_VIEW_LOCATION = "pages/setup";
    private static final String PLAY_VIEW_LOCATION = "pages/play";

    EventOutput.Result result;
    private VisitorRepository visitorRepository;
    @Autowired UpdateVisitorNewAction updateVisitorAction;
    @Autowired HandleSecuredLoanNewAction handleSecuredLoanAction;
    @Autowired LoadPrincipalDtoAction loadVisitorAction;

    // formatter:on
    @Autowired
    public VisitorThymeleafController(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    @GetMapping("visitor")
    public String getVisitor(
            Model model,
            Principal principal,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        loadVisitorAction.perform(qasino);
        // 2 - validate input
        // 3 - process
        // 4 - return  response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        // 4 - return response
        return VISITOR_VIEW_LOCATION;
    }

    @PostMapping("visitor")
    public String putVisitor(
            Model model,
            Principal principal,
            @Validated(VisitorBasic.class)
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            RedirectAttributes ra,
            HttpServletResponse response
    ) {

        // 1 - map input
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.UPDATE_VISITOR);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitorAction.perform(qasino);
        updateVisitorAction.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }

    @PostMapping(value = "pawn")
    public String visitorPawnsShip(
            Model model,
            Principal principal,
            BindingResult result,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.PAWN);
        qasino.getParams().setSuppliedVisitorUsername(principal == null ? "" : principal.getName());
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitorAction.perform(qasino);
        handleSecuredLoanAction.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }

    @PostMapping(value = "repay")
    public String visitorRepaysLoan(
            Model model,
            Principal principal,
            BindingResult result,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.REPAY);
        qasino.getParams().setSuppliedVisitorUsername(principal == null ? "" : principal.getName());
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitorAction.perform(qasino);
        handleSecuredLoanAction.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }

    @DeleteMapping("visitor")
    public String deleteVisitor(
            Model model,
            Principal principal,
            BindingResult result,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.DELETE_VISITOR);
        qasino.getParams().setSuppliedVisitorUsername(principal == null ? "" : principal.getName());
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitorAction.perform(qasino);
        visitorRepository.deleteById(qasino.getVisitor().getVisitorId());
        loadVisitorAction.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/logon";
    }

}