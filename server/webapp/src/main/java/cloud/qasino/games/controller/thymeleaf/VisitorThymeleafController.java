package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.HandleSecuredLoanAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.SignUpNewVisitorAction;
import cloud.qasino.games.action.UpdateVisitorAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static cloud.qasino.games.pattern.statemachine.event.EventOutput.Result.FAILURE;

// basic path /qasino
// basic header @RequestHeader(value "visitor", required = true) int visitorId" // else 400
//
// 200 - ok
// 201 - created
// 400 - bad request - error/reason "url ... not available"
// 404 - not found - error/message "invalid value x for y" + reason [missing]
// 412 - precondition failed = error/message - "violation of rule z"
// 500 - internal server error

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class VisitorThymeleafController extends AbstractThymeleafController {

    private static final String VISITOR_VIEW_LOCATION = "pages/visitor";
    private static final String SETUP_VIEW_LOCATION = "pages/setup";
    private static final String PLAY_VIEW_LOCATION = "pages/play";


    EventOutput.Result result;

    private VisitorRepository visitorRepository;

    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    FindVisitorIdByAliasOrUsernameAction findVisitorIdByAliasOrUsernameAction;
    @Autowired
    SignUpNewVisitorAction signUpNewVisitorAction;
    @Autowired
    UpdateVisitorAction updateVisitorAction;
    @Autowired
    HandleSecuredLoanAction handleSecuredLoanAction;

    @Autowired
    public VisitorThymeleafController(
            VisitorRepository visitorRepository
            ) {

        this.visitorRepository = visitorRepository;
    }

    @GetMapping("visitor")
    public String getVisitor(
            Model model,
            Principal principal,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", getPricipalVisitorId(principal));
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("Model => ", model);
        return VISITOR_VIEW_LOCATION;
    }

    @PostMapping("visitor")
    public String putVisitor(
            Model model,
            Principal principal,
//            @PathVariable("visitorId") Optional<String> id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            Errors errors, RedirectAttributes ra,
            BindingResult bindingResult,
            HttpServletResponse response
    ) {
        log.warn("PostMapping: visitor");
        log.warn("QasinoResponse {} !!", qasinoResponse );

//        log.warn("post in qasinoResponse: {}", qasinoResponse);

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),

                "visitorId", String.valueOf(qasinoResponse.getPageVisitor().getSelectedVisitor().getVisitorId()),
                "username", qasinoResponse.getPageVisitor().getSelectedVisitor().getUsername(),
                "alias", qasinoResponse.getPageVisitor().getSelectedVisitor().getAlias(),
                "email", qasinoResponse.getPageVisitor().getSelectedVisitor().getEmail()
        );
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDTO);
        updateVisitorAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
//        log.warn("post out qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:/visitor";
    }

    @PostMapping(value = "pawn")
    public String visitorPawnsShip(
            Model model,
//            @PathVariable("visitorId") Optional<String> id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", String.valueOf(qasinoResponse.getPageVisitor().getSelectedVisitor().getVisitorId()), "pawn", "true");
        // 2 - validate input
        if (!flowDTO.isInputValid()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDTO);
        handleSecuredLoanAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("PostMapping: /pawn");
        return "redirect:/visitor";
    }

    @PostMapping(value = "repay")
    public String visitorRepaysLoan(
            Model model,
//            @PathVariable("visitorId") Optional<String> id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", String.valueOf(qasinoResponse.getPageVisitor().getSelectedVisitor().getVisitorId()), "repay", "true");
        // 2 - validate input
        if (!flowDTO.isInputValid()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        // 3 - process
        loadEntitiesToDtoAction.perform(flowDTO);
        handleSecuredLoanAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("PostMapping: /repay");
//        log.warn("Model: {}", model);
        return "redirect:/visitor";
    }

    @DeleteMapping("visitor")
    public String deleteVisitor(
            Model model,
//            @PathVariable("visitorId") Optional<String> id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", String.valueOf(qasinoResponse.getPageVisitor().getSelectedVisitor().getVisitorId()));
        // 2 - validate input
        if (!flowDTO.isInputValid()) {
            log.warn("Errors validateInput!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDTO);
        if (!(FAILURE.equals(result))) {
            flowDTO.prepareResponseHeaders();
            visitorRepository.deleteById(flowDTO.getSuppliedVisitorId());
            flowDTO.setSuppliedVisitorId(0);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("DeleteMapping: /visitor");
//        log.warn("Model: {}", model);
        return "redirect:/logout";
    }

}