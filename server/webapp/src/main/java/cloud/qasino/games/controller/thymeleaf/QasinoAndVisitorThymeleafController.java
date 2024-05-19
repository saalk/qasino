package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.database.security.MyUserDetailService;
import cloud.qasino.games.database.security.MyUserPrincipal;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Throwables;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    private final AuthenticationManager authenticationManager;

    @Autowired
    public QasinoAndVisitorThymeleafController(
            AuthenticationManager authenticationManager,
            VisitorRepository visitorRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository) {

        this.authenticationManager = authenticationManager;
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

    @GetMapping(value = "signin") // works with get, post, put etc
    public String signin(Model model, @RequestParam(value = "error", required = false) String error) {

        // build response
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        loadEntitiesToDtoAction.perform(flowDTO);
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        QasinoResponse qasinoResponse = flowDTO.getQasinoResponse();
        if (error != null) {
            qasinoResponse.setAction("Username or password not recognised");
        }
        model.addAttribute(qasinoResponse);
        log.warn("GetMapping: /signin");
        log.warn("Model: {}",model);

        return SIGNIN_VIEW_LOCATION;
    }

    @PostMapping(value = "perform_signin") // works with get, post, put etc
    public ResponseEntity<Void> signin(@RequestBody LoginRequest loginRequest) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        log.warn("PostMapping: perform_signin");
        log.warn("LoginRequest: {}",loginRequest);
        return null;
    }

    public record LoginRequest(String username, String password) {
    }

    @GetMapping("signup")
    String signup(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        model.addAttribute(new SignupForm());

        log.warn("GetMapping: signup");
        log.warn("String: {}",requestedWith);
        log.warn("Model: {}",model);

        if (AjaxUtils.isAjaxRequest(requestedWith)) {
            return SIGNUP_VIEW_LOCATION.concat(" :: signupForm");
        }
        return SIGNUP_VIEW_LOCATION;
    }

    @PostMapping("signup")
    public String signup(
            @Valid @ModelAttribute SignupForm signupForm,
            Errors errors, RedirectAttributes ra) {

        log.warn("PostMapping: signup");
        log.warn("SignupForm: {}",signupForm);
        log.warn("RedirectAttributes: {}",ra);

        if (errors.hasErrors()) {
            log.warn("Errors exist!!: {}",errors);

            return SIGNUP_VIEW_LOCATION;
        }
        Visitor visitor = visitorService.saveUser(signupForm.createVisitor());
        userDetailService.signin(visitor);

        log.warn("visitor signed in: {}",visitor.toString());

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
        QasinoResponse qasinoResponse = flowDTO.getQasinoResponse();
        model.addAttribute(qasinoResponse);
        setVaryResponseHeader(response, flowDTO);
        log.warn("GetMapping: /");
        log.warn("Principal: {}",principal);
        log.warn("HttpServletResponse: {}",response.getHeaderNames());
        log.warn("Model: {}",model);
        return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
    }

    /**
     * Display an error page, as defined in web.xml <code>custom-error</code> element.
     */
    @RequestMapping("general")
    public String generalError(HttpServletRequest request, HttpServletResponse response, Model model) {
        // retrieve some useful information from the request

        log.warn("RequestMapping: general");
        log.warn("HttpServletRequest: {}",request.toString());
        log.warn("HttpServletResponse: {}",response.toString());
        log.warn("Model: {}",model.toString());

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

    private String getUserName(Principal principal) {
        if (principal == null) {
            return "anonymous";
        } else {
            final MyUserPrincipal visitor = (MyUserPrincipal) ((Authentication) principal).getPrincipal();
            return visitor.getUsername();
        }
    }

    private Collection<String> getUserRoles(Principal principal) {
        if (principal == null) {
            return Arrays.asList("none");
        } else {
            Set<String> roles = new HashSet<String>();
            final MyUserPrincipal visitor = (MyUserPrincipal) ((Authentication) principal).getPrincipal();
            Collection<? extends GrantedAuthority> authorities = visitor.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                roles.add(grantedAuthority.getAuthority());
            }
            return roles;
        }
    }
}