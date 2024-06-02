package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.statemachine.event.EventOutput;
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
    PlayerService playerService;

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
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "invitedVisitorId", vid,
                "gameId", gid,
                "avatar", qasinoResponse.getPageGameSetup().getHumanPlayer().getAvatar().getLabel(),
                "gameEvent", "invite"
        );
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        result = loadEntitiesToDtoAction.perform(flowDTO);
        if (EventOutput.Result.FAILURE.equals(result)) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        Player invitee = playerService.addInvitedHumanPlayerToAGame(flowDTO.getInvitedVisitor(), flowDTO.getQasinoGame(), flowDTO.getSuppliedAvatar());
        playerRepository.save(invitee);

        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
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
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "gameId", gid,
                "visitorId", getPricipalVisitorId(principal),
                "acceptedPlayerId", getPricipalVisitorId(principal),
                "fiches", String.valueOf(qasinoResponse.getPageGameSetup().getHumanPlayer().getFiches()),
                "gameEvent", "invite"
        );
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        Player accepted = playerService.acceptInvitationForAGame(flowDTO.getAcceptedPlayer());
        playerRepository.save(accepted);

        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("qasinoResponse: {}", flowDTO.getQasinoResponse());
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
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "declinedPlayerId", getPricipalVisitorId(principal),
                "gameEvent", "decline"
        );
        // 2 - validate input
        if (!flowDTO.isInputValid() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        Player rejected = playerService.rejectInvitationForAGame(flowDTO.getAcceptedPlayer());
        playerRepository.save(rejected);

        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:invitations/";
    }
}


