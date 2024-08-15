package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.old.LoadEntitiesToDtoAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.service.PlayerServiceOld;
import cloud.qasino.games.dto.QasinoFlowDto;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
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

    private static final String INVITES_VIEW_LOCATION = "pages/invites";

    @Autowired
    PlayerServiceOld playerServiceOld;

    private PlayerRepository playerRepository;

    EventOutput.Result result;

    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;

    @Autowired
    public InvitationsThymeleafController( PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @PostMapping(value = "/invite/{otherVisitorId}/game/{gameId}")
    public String inviteVisitorForAGame(
            Model model,
            Principal principal,
            @PathVariable("otherVisitorId") String vid,
            @PathVariable("gameId") String gid,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response) {

        log.warn("PostMapping: /invite/{otherVisitorId}/game/{gameId}");

        // 1 - map input
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "invitedVisitorId", vid,
                "gameId", gid,
                "avatar", qasinoResponse.getPageGameSetup().getHumanPlayer().getAvatar().getLabel(),
                "gameEvent", "invite"
        );
        // 2 - validate input
        if (!flowDto.isInputValid() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDto);
        if (EventOutput.Result.FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDto);
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        Player invitee = playerServiceOld.addInvitedHumanPlayerToAGame(flowDto.getInvitedVisitor(), flowDto.getQasinoGame(), flowDto.getSuppliedAvatar());
        playerRepository.save(invitee);

        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
        log.warn("get qasinoResponse: {}", flowDto.getQasinoResponse());
        return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
    }

    @PostMapping(value = "/accept/{gameId}")
    public String acceptInvitationForAGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String gid,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response) {

        log.warn("PostMapping: /accept/{gameId}");

        // 1 - map input
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "gameId", gid,
                "visitorId", getPricipalVisitorId(principal),
                "acceptedPlayerId", getPricipalVisitorId(principal),
                "fiches", String.valueOf(qasinoResponse.getPageGameSetup().getHumanPlayer().getFiches()),
                "gameEvent", "invite"
        );
        // 2 - validate input
        if (!flowDto.isInputValid() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
//            flowDto.setAction("Username incorrect");
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        Player accepted = playerServiceOld.acceptInvitationForAGame(flowDto.getAcceptedPlayer());
        playerRepository.save(accepted);

        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
        log.warn("qasinoResponse: {}", flowDto.getQasinoResponse());
        return "redirect:invitations/";
    }

    @PutMapping(value = "/decline/{gameId}")
    public String declineInvitationForAGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult bindingResult,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response) {

        log.warn("PostMapping: /decline/{gameId}");

        // 1 - map input
        QasinoFlowDto flowDto = new QasinoFlowDto();
        flowDto.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "declinedPlayerId", getPricipalVisitorId(principal),
                "gameEvent", "decline"
        );
        // 2 - validate input
        if (!flowDto.isInputValid() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDto);
//            flowDto.setAction("Username incorrect");
            model.addAttribute(flowDto.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        Player rejected = playerServiceOld.rejectInvitationForAGame(flowDto.getAcceptedPlayer());
        playerRepository.save(rejected);

        // 4 - return response
        prepareQasinoResponse(response, flowDto);
        model.addAttribute(flowDto.getQasinoResponse());
        log.warn("qasinoResponse: {}", flowDto.getQasinoResponse());
        return "redirect:invitations/";
    }
}


