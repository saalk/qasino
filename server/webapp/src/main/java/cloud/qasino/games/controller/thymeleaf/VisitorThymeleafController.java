package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CalculateQasinoStatistics;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.HandleSecuredLoanAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDto;
import cloud.qasino.games.action.MapQasinoResponseFromDto;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.SignUpNewVisitorAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.statemachine.event.EventOutput;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

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


    EventOutput.Result output;

    private VisitorRepository visitorRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;
    private ResultsRepository resultsRepository;
    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    FindVisitorIdByAliasOrUsernameAction findVisitorIdByAliasOrUsernameAction;
    @Autowired
    SignUpNewVisitorAction signUpNewVisitorAction;
    @Autowired
    HandleSecuredLoanAction handleSecuredLoanAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateQasinoStatistics calculateQasinoStatistics;
    @Autowired
    MapQasinoResponseFromDto mapQasinoResponseFromDto;
    @Autowired
    MapQasinoGameTableFromDto mapQasinoGameTableFromDto;

    @Autowired
    public VisitorThymeleafController(
            VisitorRepository visitorRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository) {

        this.visitorRepository = visitorRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.turnRepository = turnRepository;
    }

    @GetMapping(value = {"visitor/{visitorId}", "visitor/"})
    public String getVisitor(
            Model model,
            @PathVariable("visitorId") Optional<String> id,
            @ModelAttribute QasinoResponse qasinoResponse,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        if (id.isPresent()) flowDTO.setPathVariables("visitorId", id.get());
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: visitor/{visitorId}");
        log.warn("HttpServletResponse: {}", response.getHeaderNames());
        log.warn("Model: {}", model);
        log.warn("Errors: {}", errors);
        log.warn("qasinoResponse: {}", flowDTO.getQasinoResponse());

        return VISITOR_VIEW_LOCATION.concat(" :: qasinoResponse");
    }

    @PutMapping("visitor/{visitorId}")
    public String putVisitor(
            Model model,
            @PathVariable("visitorId") Optional<String> id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", id.get(),
                "username", qasinoResponse.getPageVisitor().getSelectedVisitor().getUsername(),
                "alias", qasinoResponse.getPageVisitor().getSelectedVisitor().getAlias(),
                "email", qasinoResponse.getPageVisitor().getSelectedVisitor().getEmail(),
                "aliasSequence", String.valueOf(qasinoResponse.getPageVisitor().getSelectedVisitor().getAliasSequence())
        );
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        flowDTO.prepareResponseHeaders();
        qasinoResponse = flowDTO.getQasinoResponse();
        model.addAttribute(qasinoResponse);
        log.warn("PutMapping: /visitor");
        log.warn("Model: {}", model);
        return VISITOR_VIEW_LOCATION;
    }

    @PutMapping(value = "pawn/{visitorId}")
    public String visitorPawnsShip(
            Model model,
            @PathVariable("visitorId") Optional<String> id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", id.get(), "pawn", "true");
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            prepareQasinoResponse(response, flowDTO);

            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (!(output == EventOutput.Result.FAILURE)) {
            handleSecuredLoanAction.perform(flowDTO);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        return VISITOR_VIEW_LOCATION;
    }

    @PutMapping(value = "repay/{visitorId}")
    public String visitorRepaysLoan(
            Model model,
            @PathVariable("visitorId") Optional<String> id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", id.get(), "repay", "true");
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            prepareQasinoResponse(response, flowDTO);

            model.addAttribute(flowDTO.getQasinoResponse());
            return VISITOR_VIEW_LOCATION;
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (!(output == EventOutput.Result.FAILURE)) {
            handleSecuredLoanAction.perform(flowDTO);
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        return VISITOR_VIEW_LOCATION;
    }

    //    @DeleteMapping("/visitor/{visitorId}")
    public ResponseEntity<QasinoResponse> deleteVisitor(
            @PathVariable("visitorId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", id);
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // delete
        // TODO delete players with this visitor first
        visitorRepository.deleteById(flowDTO.getSuppliedVisitorId());
        flowDTO.setSuppliedVisitorId(0);
        loadEntitiesToDtoAction.perform(flowDTO);
        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        // delete 204 -> 200 otherwise no content in response body
        return ResponseEntity.status(HttpStatus.OK).headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

}