package cloud.qasino.games.controller;

import cloud.qasino.games.action.visitor.HandleSecuredLoanAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.action.visitor.UpdateVisitorAction;
import cloud.qasino.games.action.common.load.LoadPrincipalDtoAction;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.validation.VisitorBasic;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
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

    // @formatter:off
    private static final String VISITOR_VIEW_LOCATION = "pages/visitor";
    private static final String ERROR_VIEW_LOCATION = "pages/error";

    EventOutput.Result output;
    private final VisitorRepository visitorRepository;
    @Autowired UpdateVisitorAction updateVisitor;
    @Autowired HandleSecuredLoanAction handleSecuredLoan;
    @Autowired LoadPrincipalDtoAction loadVisitor;

    // @formatter:on
    @Autowired
    public VisitorThymeleafController(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    @GetMapping("visitor")
    public String getVisitor(
            Principal principal,
            Model model,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        // 2 - validate input
        // 3 - process
        // 4 - return  response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return VISITOR_VIEW_LOCATION;
    }

    @PostMapping("visitor")
    public String putVisitor(
            Principal principal,
            @Validated(VisitorBasic.class)
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.UPDATE_VISITOR);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        // 2 - validate input
        if (result.hasErrors()) {
            log.info("errors in supplied data {}", result);
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = updateVisitor.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }

    @PostMapping(value = "pawn")
    public String visitorPawnsShip(
            Principal principal,
//            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.PAWN);
        qasino.getParams().setSuppliedVisitorUsername(principal == null ? "" : principal.getName());
        // 2 - validate input
//        if (result.hasErrors()) {
//            return "error";
//        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = handleSecuredLoan.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }

    @PostMapping(value = "repay")
    public String visitorRepaysLoan(
            Principal principal,
//            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.REPAY);
        qasino.getParams().setSuppliedVisitorUsername(principal == null ? "" : principal.getName());
        // 2 - validate input
//        if (result.hasErrors()) {
//            return "error";
//        }
        // 3 - process
        loadVisitor.perform(qasino);
        output = handleSecuredLoan.perform(qasino);
        if (FAILURE.equals(output)) {
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/visitor";
    }

    @DeleteMapping("visitor")
    public String deleteVisitor(
            Principal principal,
//            BindingResult result, // can only be directly after @Validated/valid
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.DELETE_VISITOR);
        qasino.getParams().setSuppliedVisitorUsername(principal == null ? "" : principal.getName());
        // 2 - validate input
//        if (result.hasErrors()) {
//            return "error";
//        }
        // 3 - process
        loadVisitor.perform(qasino);
        visitorRepository.deleteById(qasino.getVisitor().getVisitorId());
        loadVisitor.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:/logon";
    }

}