package cloud.qasino.games.controller;

import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.action.visitor.RegisterVisitorAction;
import cloud.qasino.games.database.security.MyUserPrincipal;
import cloud.qasino.games.dto.validation.VisitorBasic;
import cloud.qasino.games.pattern.singleton.OnlineVisitorsPerDay;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import cloud.qasino.games.web.AjaxUtils;
import cloud.qasino.games.web.MessageHelper;
import com.google.common.base.Throwables;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
public class HomeThymeleafController extends AbstractThymeleafController {

    // @formatter:off
    //    private static final String IMAGES_FAVICON_LOCATION = "static/images/favicon.ico";
    private static final String HOME_SIGNUP_VIEW_LOCATION = "home/register";
    private static final String HOME_SIGNIN_VIEW_LOCATION = "home/signin";
    private static final String HOME_SIGNED_IN_LOCATION = "home/homeSignedIn";
    private static final String HOME_NOT_SIGNED_IN_LOCATION = "home/homeNotSignedIn";
    private static final String ERROR_VIEW_LOCATION = "pages/error";

    private final AuthenticationManager authenticationManager;

    @Autowired
    private RegisterVisitorAction registerAction;
    // @formatter:on

    @Autowired
    public HomeThymeleafController(
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.LOGON);
        // 2 - validate input
        // 3 - process
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return HOME_SIGNIN_VIEW_LOCATION;
    }

    @PostMapping(value = "perform_signin") // works with get, post, put etc
    public ResponseEntity<Void> signin(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        log.info("PostMapping: perform_signin");
        log.info("LoginRequest: {}", loginRequest);
        if (authenticationResponse.isAuthenticated()) {
            OnlineVisitorsPerDay.getInstance().newLogon();
        }
        return null;
    }

    public record LoginRequest(String username, String password) {
    }

    @GetMapping({"register"})
    String signup(
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false)
            String requestedWith,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        // 2 - validate input
        // 3 - process
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        if (AjaxUtils.isAjaxRequest(requestedWith)) {
//            return HOME_SIGNUP_VIEW_LOCATION.concat(" :: qasinoResponse");
            return HOME_SIGNUP_VIEW_LOCATION.concat(" :: qasino");
        }
        return HOME_SIGNUP_VIEW_LOCATION;
    }

    @PostMapping("register")
    public String register(
            Model model,
            @Validated(VisitorBasic.class)
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.REGISTER);
        // 2 - validate input
        if (result.hasErrors()) {
            log.info("errors in supplied data {}", result);
            prepareQasino(response, qasino);
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        if (OnlineVisitorsPerDay.getInstance().getOnlineVisitors() > 3) {
            prepareQasino(response, qasino);
            qasino.getMessage().setBadRequestErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedQasinoEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] is invalid - max registrations is 3 per day");
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 3 - process
        registerAction.perform(qasino);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
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
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedVisitorUsername(principal == null ? "" : principal.getName());
        // 2 - validate input
        if (OnlineVisitorsPerDay.getInstance().getOnlineVisitors() > 3) {
            prepareQasino(response, qasino);

            qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.LOGON);
            principal = null;
            qasino.getMessage().setBadRequestErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedQasinoEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] is invalid - max visitors is 3 per day");
            model.addAttribute(qasino);
            return ERROR_VIEW_LOCATION;
        }
        // 3 - process
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
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
        return ERROR_VIEW_LOCATION;
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