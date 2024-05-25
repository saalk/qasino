package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.SignUpNewVisitorAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.security.MyUserDetailService;
import cloud.qasino.games.database.security.MyUserPrincipal;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.web.AjaxUtils;
import cloud.qasino.games.web.MessageHelper;
import com.google.common.base.Throwables;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
public class HomeSigninSignupThymeleafController extends AbstractThymeleafController {

//    private static final String IMAGES_FAVICON_LOCATION = "static/images/favicon.ico";
    private static final String HOME_SIGNUP_VIEW_LOCATION = "home/signup";
    private static final String HOME_SIGNIN_VIEW_LOCATION = "home/signin";
    private static final String HOME_SIGNED_IN_LOCATION = "home/homeSignedIn";
    private static final String HOME_NOT_SIGNED_IN_LOCATION = "home/homeNotSignedIn";
    private static final String ERROR_GENERAL_LOCATION = "error/general";

    @Autowired
    SignUpNewVisitorAction signUpNewVisitorAction;
    @Autowired
    private MyUserDetailService userDetailService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public HomeSigninSignupThymeleafController(
            AuthenticationManager authenticationManager,
            SignUpNewVisitorAction signUpNewVisitorAction
    ) {
        this.authenticationManager = authenticationManager;
        this.signUpNewVisitorAction = signUpNewVisitorAction;
    }

//    @RequestMapping("favicon.ico")
//    String favicon(HttpServletResponse response) {
////        return "forward:/images/favicon.ico";
//        return IMAGES_FAVICON_LOCATION;
//    }

    @GetMapping(value = "signin")
    public String signin(
            Model model,
            @RequestParam(value = "error", required = false) String error,
            HttpServletResponse response
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        // 2 - validate input
        if (error != null) {
            flowDTO.setAction("Username or password not recognised");
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute( flowDTO.getQasinoResponse());

//        log.warn("GetMapping: signin");
//        log.warn("Model: {}", model);
//        log.warn("error: {}", error);
//        log.warn("qasinoResponse: {}", flowDTO.getQasinoResponse());

        return HOME_SIGNIN_VIEW_LOCATION;
    }

    @PostMapping(value = "perform_signin") // works with get, post, put etc
    public ResponseEntity<Void> signin(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        log.warn("PostMapping: perform_signin");
        log.warn("LoginRequest: {}", loginRequest);
        return null;
    }

    public record LoginRequest(String username, String password) {
    }

    @GetMapping("signup")
    String signup(
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false)
            String requestedWith,
            HttpServletResponse response
    ) {

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        // 2 - validate input
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        model.addAttribute(new SignupForm());

//        log.warn("GetMapping: signup");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("String: {}",requestedWith);
//        log.warn("qasinoResponse: {}", flowDTO.getQasinoResponse());


        if (AjaxUtils.isAjaxRequest(requestedWith)) {
            return HOME_SIGNUP_VIEW_LOCATION.concat(" :: signupForm");
        }
        return HOME_SIGNUP_VIEW_LOCATION;
    }

    @PostMapping("signup")
    public String signup(
            Model model,
            @Valid @ModelAttribute SignupForm signupForm,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
    ) {

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "username", signupForm.getUsername(),
                "alias", signupForm.getAlias(),
                "email", signupForm.getEmail(),
                "password", signupForm.getPassword());
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            model.addAttribute(signupForm);
            return HOME_SIGNUP_VIEW_LOCATION;
        }
        // 3 - process
        signUpNewVisitorAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        QasinoResponse qasinoResponse = flowDTO.getQasinoResponse();
        model.addAttribute(flowDTO.getQasinoResponse());

//        log.warn("PostMapping: signup");
//        log.warn("SignupForm: {}", signupForm);
//        log.warn("RedirectAttributes: {}", ra);
//        log.warn("qasinoResponse: {}", flowDTO.getQasinoResponse());

        // see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
        MessageHelper.addSuccessAttribute(ra, "signup.success");
        return "redirect:/";
    }


    @GetMapping({"/"})
    String index(
            Model model,
            Principal principal,
            HttpServletResponse response) {

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        if (principal != null) flowDTO.setPathVariables("username", principal.getName());
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return principal != null ? HOME_SIGNED_IN_LOCATION : HOME_NOT_SIGNED_IN_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: /");
//        log.warn("Principal: {}", principal);
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
        log.warn("qasinoResponse: {}", flowDTO.getQasinoResponse());

        return principal != null ? HOME_SIGNED_IN_LOCATION : HOME_NOT_SIGNED_IN_LOCATION;
    }

    /**
     * Display an error page, as defined in web.xml <code>custom-error</code> element.
     */
    @RequestMapping("general")
    public String generalError(HttpServletRequest request, HttpServletResponse response, Model model) {
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
        return ERROR_GENERAL_LOCATION;
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return Throwables.getRootCause(throwable).getMessage();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
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
            Set<String> roles = new HashSet<>();
            final MyUserPrincipal visitor = (MyUserPrincipal) ((Authentication) principal).getPrincipal();
            Collection<? extends GrantedAuthority> authorities = visitor.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                roles.add(grantedAuthority.getAuthority());
            }
            return roles;
        }
    }
}