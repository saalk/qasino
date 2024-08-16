package cloud.qasino.games.controller;

import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.action.common.load.LoadPrincipalDtoAction;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class InvitationsThymeleafController extends AbstractThymeleafController {

    // @formatter:off
    private static final String INVITES_VIEW_LOCATION = "pages/invite";

    EventOutput.Result output;
    private PlayerRepository playerRepository;
    private PlayerService playerService;
    @Autowired LoadPrincipalDtoAction loadVisitor;
    // @formatter:on

    @Autowired
    public InvitationsThymeleafController(
            PlayerRepository playerRepository,
            PlayerService playerService) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    @GetMapping("/invite")
    public String getLeague(
            Principal principal,
            Model model,
            HttpServletResponse response) {

        // 1 - map input
        Qasino qasino = new Qasino();
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        // 2 - validate input
        // 3 - process
        loadVisitor.perform(qasino);
        // 4 - return  response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return INVITES_VIEW_LOCATION;
    }

    @PostMapping(value = "/invite/{otherVisitorId}/game/{gameId}")
    public String inviteVisitorForAGame(
            Principal principal,
            Model model,
            @PathVariable("otherVisitorId") String vid,
            @PathVariable("gameId") String gid,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.ADD_INVITEE);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedInvitedVisitorId(Long.parseLong(vid));
        qasino.getParams().setSuppliedGameId(Long.parseLong(gid));
        // "invitedVisitorId", "avatar"
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        loadVisitor.perform(qasino);
        playerService.addInvitedHumanPlayerToAGame(
                qasino.getVisitor(), qasino.getGame(), qasino.getCreation().getSuppliedAvatar());
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:setup/" + qasino.getParams().getSuppliedGameId();
    }

    @PostMapping(value = "/accept/{gameId}")
    public String acceptInvitationForAGame(
            Principal principal,
            @PathVariable("gameId") String gid,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        log.warn("PostMapping: /accept/{gameId}");

        // 1 - map input
        qasino.getParams().setSuppliedGameEvent(GameEvent.ADD_INVITEE);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        qasino.getParams().setSuppliedGameId(Long.parseLong(gid));
        // "gameId", "visitorId", "acceptedPlayerId", "fiches", "gameEvent", "invite"
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        playerService.acceptInvitationForAGame(null);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:invitations/";
    }

    @PutMapping(value = "/decline/{gameId}")
    public String declineInvitationForAGame(
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute("qasino") Qasino qasino,
            BindingResult result,
            Model model,
            RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        qasino.getParams().setSuppliedQasinoEvent(QasinoEvent.UPDATE_VISITOR);
        qasino.getParams().setSuppliedVisitorUsername(principal.getName());
        // "gameId", "visitorId", "declinedPlayerId", "gameEvent", "decline"
        // 2 - validate input
        if (result.hasErrors()) {
            return "error";
        }
        // 3 - process
        playerService.rejectInvitationForAGame(null);
        // 4 - return response
        prepareQasino(response, qasino);
        model.addAttribute(qasino);
        return "redirect:invitations/";
    }
}


