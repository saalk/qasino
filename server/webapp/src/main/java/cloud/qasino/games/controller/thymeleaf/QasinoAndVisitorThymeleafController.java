package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.database.security.MyUserDetailService;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorService;
import cloud.qasino.games.action.CalculateQasinoStatistics;
import cloud.qasino.games.action.FindVisitorIdByAliasAction;
import cloud.qasino.games.action.HandleSecuredLoanAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDto;
import cloud.qasino.games.action.MapQasinoResponseFromDto;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.SignUpNewVisitorAction;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.web.AjaxUtils;
import cloud.qasino.games.web.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Throwables;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.text.MessageFormat;
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
public class QasinoAndVisitorThymeleafController {

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
    FindVisitorIdByAliasAction findVisitorIdByAliasAction;
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

    private static final String SIGNUP_VIEW_LOCATION = "home/signup";
    private static final String SIGNIN_VIEW_LOCATION = "home/signin";

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private MyUserDetailService userDetailService;

    @Autowired
    public QasinoAndVisitorThymeleafController(
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

    @RequestMapping("favicon.ico")
    String favicon() {
//        return "forward:/images/favicon.ico";
        return "/images/favicon.ico";
    }

    @RequestMapping(value = "signin") // works with get, post, put etc
    public String signin() {
        return SIGNIN_VIEW_LOCATION;
    }

    @GetMapping("signup")
    String signup(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        model.addAttribute(new SignupForm());

        log.info("GetMapping: signup");
        log.info("Model: {}",model);
        log.info("String: {}",requestedWith);

        if (AjaxUtils.isAjaxRequest(requestedWith)) {
            return SIGNUP_VIEW_LOCATION.concat(" :: signupForm");
        }
        return SIGNUP_VIEW_LOCATION;
    }

    @PostMapping("signup")
    public String signup(
            @Valid @ModelAttribute SignupForm signupForm,
            Errors errors, RedirectAttributes ra) {

        log.info("PostMapping: signup");
        log.info("SignupForm: {}",signupForm);
        log.info("Errors: {}",errors);
        log.info("RedirectAttributes: {}",ra);

        if (errors.hasErrors()) {
            return SIGNUP_VIEW_LOCATION;
        }
        Visitor visitor = visitorService.saveUser(signupForm.createVisitor());
        userDetailService.signin(visitor);

        log.info("visitor: {}",visitor.toString());

        // see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
        MessageHelper.addSuccessAttribute(ra, "signup.success");
        return "redirect:/";
//        return "redirect:/"+visitor.getVisitorId();
    }


//    // move to abstract class
//    @ModelAttribute
//    public void addAttributes(final Model model) {
//        model.addAttribute("welcome", "Welcome to the Qasino in the Cloud!");
//
//        // build qasino
//        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
//        loadEntitiesToDtoAction.perform(flowDTO);
//        mapQasinoGameTableFromDto.perform(flowDTO);
//        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
//        calculateQasinoStatistics.perform(flowDTO);
//        mapQasinoResponseFromDto.perform(flowDTO);
//        flowDTO.prepareResponseHeaders();
//        model.addAttribute("qasino", flowDTO.getQasinoResponse());
//    }

    @GetMapping({"/"} )
    String index(
            Model model,
            Principal principal,
            HttpServletResponse response) {



        // build response
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        loadEntitiesToDtoAction.perform(flowDTO);
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();

        model.addAttribute("qasino", flowDTO.getQasinoResponse());
        setVaryResponseHeader(response, flowDTO);

        log.info("GetMapping: /");
        log.info("Model qasino: {}",model.getAttribute("qasino"));
        log.info("Principal: {}",principal);
        log.info("HttpServletResponse: {}",response);

        return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
    }

    /**
     * Display an error page, as defined in web.xml <code>custom-error</code> element.
     */
    @RequestMapping("general")
    public String generalError(HttpServletRequest request, HttpServletResponse response, Model model) {
        // retrieve some useful information from the request

        log.info("RequestMapping: general");
        log.info("HttpServletRequest: {}",request.toString());
        log.info("HttpServletResponse: {}",response.toString());
        log.info("Model: {}",model.toString());

        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
        // String servletName = (String) request.getAttribute("jakarta.servlet.error.servlet_name");
        String exceptionMessage = getExceptionMessage(throwable, statusCode);

        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        if (requestUri == null) {
            requestUri = "Unknown";
        }

        String message = MessageFormat.format("{0} returned for {1} with message {2}",
                statusCode, requestUri, exceptionMessage
        );

        model.addAttribute("errorMessage", message);
        return "error/general";
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return Throwables.getRootCause(throwable).getMessage();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }

    @ModelAttribute
    public void setVaryResponseHeader(HttpServletResponse response, QasinoFlowDTO flowDTO) {
        MultiValueMap<String, String> headers = flowDTO.getHeaders();
        headers.forEach((name, values) -> {
            for (String value : values) {
                response.setHeader(name, value);
            }
        });
    }
}