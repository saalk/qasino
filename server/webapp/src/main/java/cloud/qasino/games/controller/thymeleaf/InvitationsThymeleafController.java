package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CreateNewGameAction;
import cloud.qasino.games.action.CreateNewLeagueAction;
import cloud.qasino.games.action.IsGameConsistentForGameEvent;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.PrepareGameAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
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
import java.util.List;
import java.util.Optional;

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class InvitationsThymeleafController extends AbstractThymeleafController {

    private static final String VISITOR_VIEW_LOCATION = "pages/visitor";
    private static final String SETUP_VIEW_LOCATION = "pages/setup";

    private VisitorRepository visitorRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    EventOutput.Result output;

    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    CreateNewLeagueAction createNewLeagueAction;
    @Autowired
    IsGameConsistentForGameEvent isGameConsistentForGameEvent;
    @Autowired
    CreateNewGameAction createNewGameAction;
    @Autowired
    PrepareGameAction prepareGameAction;

    @Autowired
    public InvitationsThymeleafController(
            VisitorRepository visitorRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository) {

        this.visitorRepository = visitorRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @PostMapping(value = "/invite/{otherVisitorId}/game/{gameId}")
    public String inviteVisitorForAGame(
            Model model,
            Principal principal,
            @PathVariable("otherVisitorId") String vid,
            @PathVariable("gameId") String gid,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response) {

        log.warn("PostMapping: /invite/{otherVisitorId}/game/{gameId}");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", getPricipalVisitorId(principal),
                "invitedPlayerId", vid,
                "gameId", gid,
                "avatar", qasinoResponse.getPageGameSetup().getHumanPlayer().getAvatar().getLabel(),
                "gameEvent", "invite"
        );
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            log.warn("Errors loadEntitiesToDtoAction!!: {}", errors);
            log.warn("Model !!: {}", model);
            prepareQasinoResponse(response, flowDTO);
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        long gameId = flowDTO.getSuppliedGameId();
        long invitedPlayerId = flowDTO.getInvitedPlayerId();

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Visitor> foundVisitor = visitorRepository.findById(invitedPlayerId);
        if (!foundVisitor.isPresent()) {
            // 404
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Visitor linkedVisitor = foundVisitor.get();

        int sequenceCalculated = (playerRepository.countByGame(linkedGame)) + 1;
        // create initiator
        Player createdHuman = new Player(
                linkedVisitor, linkedGame, Role.INVITED, 0, sequenceCalculated,
                Avatar.fromLabelWithDefault(qasinoResponse.getPageGameSetup().getHumanPlayer().getAvatar().getLabel()), AiLevel.HUMAN);
        createdHuman = playerRepository.save(createdHuman);
        if (createdHuman.getPlayerId() == 0) {
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
            //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
        // 200
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        //        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

    @PostMapping(value = "/accept/{gameId}")
    public String acceptInvitationForAGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String gid,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
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
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        long gameId = flowDTO.getSuppliedGameId();
        long acceptedPlayerId = flowDTO.getAcceptedPlayerId();
        int fiches = flowDTO.getSuppliedFiches();

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return "redirect:invitations/" + acceptedPlayerId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Player> foundPlayer = playerRepository.findById(acceptedPlayerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return "redirect:invitations/" + acceptedPlayerId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Player linkedPlayer = foundPlayer.get();
        // create initiator
        Player updatedPlayer = linkedPlayer;
        updatedPlayer.setRole(Role.ACCEPTED);
        updatedPlayer.setFiches(fiches);
        updatedPlayer = playerRepository.save(updatedPlayer);
        if (updatedPlayer.getPlayerId() == 0) {
            return "redirect:invitations/" + acceptedPlayerId;
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        int index = linkedGame.getPlayers().indexOf(updatedPlayer);
        List<Player> updatedPlayers = linkedGame.getPlayers();
        updatedPlayers.add(index, updatedPlayer);
        // 200
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: /game/invite");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:invitations/" + acceptedPlayerId;
//        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

    @PutMapping(value = "/decline/{gameId}")
    public String declineInvitationForAGame(
            Model model,
            Principal principal,
            @PathVariable("gameId") String id,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response) {

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "gameId", id,
                "visitorId", getPricipalVisitorId(principal),
                "declinedPlayerId", getPricipalVisitorId(principal),
                "gameEvent", "decline"
        );
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:setup/" + qasinoResponse.getPageGameSetup().getSelectedGame().getGameId();
        }
        // 3 - process
        long gameId = flowDTO.getSuppliedGameId();
        long declinedPlayerId = flowDTO.getDeclinedPlayerId();

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return "redirect:invitations/" + declinedPlayerId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Player> foundPlayer = playerRepository.findById(declinedPlayerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return "redirect:invitations/" + declinedPlayerId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Player linkedPlayer = foundPlayer.get();
        // create initiator
        Player updatedPlayer = linkedPlayer;
        updatedPlayer.setRole(Role.REJECTED);
        updatedPlayer = playerRepository.save(updatedPlayer);
        if (updatedPlayer.getPlayerId() == 0) {
            return "redirect:invitations/" + declinedPlayerId;
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
        int index = linkedGame.getPlayers().indexOf(updatedPlayer);
        List<Player> updatedPlayers = linkedGame.getPlayers();
        updatedPlayers.add(index, updatedPlayer);
        // 200
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: /game/invite");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:invitations/" + declinedPlayerId;
//        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

}


