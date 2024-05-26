package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.action.CalculateQasinoStatistics;
import cloud.qasino.games.action.CreateNewGameAction;
import cloud.qasino.games.action.CreateNewLeagueAction;
import cloud.qasino.games.action.IsGameConsistentForGameEvent;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDto;
import cloud.qasino.games.action.MapQasinoResponseFromDto;
import cloud.qasino.games.action.PrepareGameAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.controller.AbstractThymeleafController;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.statemachine.event.EventOutput;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@ControllerAdvice
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class GameAndLeagueThymeleafController extends AbstractThymeleafController {

    private static final String VISITOR_VIEW_LOCATION = "pages/visitor";
    private static final String SETUP_VIEW_LOCATION = "pages/setup";

    private VisitorRepository visitorRepository;
    private LeagueRepository leagueRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;

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
    public GameAndLeagueThymeleafController(
            VisitorRepository visitorRepository,
            LeagueRepository leagueRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository) {

        this.visitorRepository = visitorRepository;
        this.leagueRepository = leagueRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.turnRepository = turnRepository;
    }

    // /api/game/{id} - GET, DELETE, PUT type, style, ante - rules apply!
    // /api/game/{id}/ACCEPT -> PUT player fiches
    // /api/game/{id}/WITHDRAW/bot -> DELETE players
    // /api/game/{id}/WITHDRAW/visitor{id} -> DELETE players

    @PostMapping(value = "game/start")
    public String startGameWithVisitorPlayer(
            Model model,
//            @PathVariable("type") String type,
//            @PathVariable("visitorId") String id,
            @Valid @ModelAttribute QasinoResponse qasinoResponse,
            @Valid @ModelAttribute SetupGameForm setupGameForm,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response
//            @RequestParam(name = "style", defaultValue = " ") String style,
//            @RequestParam(name = "ante", defaultValue = "20") String ante,
//            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {
        log.warn("PostMapping: /game/start");

        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", setupGameForm.getVisitorIdFk(),
                "type", setupGameForm.getType(),
                "style", setupGameForm.getStyle(),
                "ante", setupGameForm.getAnte(),
                "avatar", setupGameForm.getAvatar(),

                "gameEvent", "start"
        );

        if (setupGameForm.getAiLevel() != null) {
            flowDTO.setPathVariables("aiLevel", setupGameForm.getAiLevel());
        }
        // 2 - validate input
        if (!flowDTO.validateInput()) {
            log.warn("Errors exist!!: {}", errors);
            log.warn("HttpServletResponse: {}", response.getHeaderNames());
            log.warn("Model: {}", model);
            log.warn("Errors: {}", errors);
            log.warn("end setupGameForm: {}", setupGameForm);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:/visitor/" + flowDTO.getSuppliedVisitorId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getSuppliedVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create or update the game
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getSuppliedVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        createNewGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("PostMapping: /game/start");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("end setupGameForm: {}", setupGameForm);
        return "redirect:visitor/" + flowDTO.getSuppliedVisitorId();
    }

//    @PostMapping(value = "/game/start/{type}/visitor/{visitorId}/bot/{aiLevel}")
    public String startGameWithVisitorPlayerAndBot(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @PathVariable("type") String type,
            @PathVariable("visitorId") String id,
            @PathVariable("aiLevel") String aiLevel,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "type", type,
                "visitorId", id,
                "aiLevel", aiLevel,
                "style", style,
                "ante", ante,
                "avatar", avatar,
                "gameEvent", "start"
        );
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        // create or update the game
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        createNewGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: /game/start/{type}/visitor/{visitorId}/bot/{aiLevel}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }

//    @PostMapping(value = "/game/start/{type}/league/{leagueId}/visitor/{visitorId}")
    public String startGameInLeagueWithVisitorPlayer(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @PathVariable("type") String type,
            @PathVariable("visitorId") String vId,
            @PathVariable("leagueId") String lId,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", vId,
                "leagueId", lId,
                "type", type,
                "style", style,
                "ante", ante,
                "avatar", avatar,
                "gameEvent", "start"
        );
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        // create or update the game
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        createNewGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: /game/start/{type}/league/{leagueId}/visitor/{visitorId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }

//    @PostMapping(value = "/game/start/{type}/league/{leagueId}/visitor/{visitorId}/bot/{aiLevel}")
    public String startGameInLeagueWithVisitorPlayerAndBot(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @PathVariable("type") String type,
            @PathVariable("visitorId") String vId,
            @PathVariable("leagueId") String lId,
            @PathVariable("aiLevel") String aiLevel,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", vId,
                "leagueId", lId,
                "aiLevel", aiLevel,
                "type", type,
                "style", style,
                "ante", ante,
                "avatar", avatar,
                "gameEvent", "start"
        );
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // 3 - process
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // update game
        createNewGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: /game/start/{type}/league/{leagueId}/visitor/{visitorId}/bot/{aiLevel}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }

    @PutMapping(value = "/game/{gameId}/validate/{type}/league/{leagueId}")
    public String validateGameWithTypeLeagueStyleAnte(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String gid,
            @PathVariable("type") String type,
            @PathVariable("leagueId") Optional<String> lid,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        if (lid.isPresent()) flowDTO.setPathVariables("leagueId", lid.get());
        flowDTO.setPathVariables(
                "visitorId", vId,
                "gameId", gid,
                "type", type,
                "style", style,
                "ante", ante,
                "gameEvent", "validate"

        );
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // 3 - process
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
         // update game
        prepareGameAction.perform(flowDTO);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("PostMapping: /game/{gameId}/validate/{type}/league/{leagueId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }

    // @PutMapping(value = "/game/{gameId}/invite/visitor{visitorId}")
    public String inviteVisitorForAGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @PathVariable("gameId") String gId,
            @PathVariable("visitorId") String vId,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(gId, vId, avatar)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(gId)
                || !StringUtils.isNumeric(vId)
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return "redirect:visitor/" + vId;
//            return ResponseEntity.badRequest().headers(headers).build();
        }

        long gameId = Long.parseLong(gId);
        long visitorId = Long.parseLong(vId);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return "redirect:visitor/" + vId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        if (!foundVisitor.isPresent()) {
            // 404
            return "redirect:visitor/" + vId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Visitor linkedVisitor = foundVisitor.get();

        int sequenceCalculated = (playerRepository.countByGame(linkedGame)) + 1;
        // create initiator
        Player createdHuman = new Player(
                linkedVisitor, linkedGame, Role.INVITED, 0, sequenceCalculated,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN);
        createdHuman = playerRepository.save(createdHuman);
        if (createdHuman.getPlayerId() == 0) {
            return "redirect:visitor/" +  vId;
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // 200
        linkedGame.getPlayers().add(createdHuman);
        return "redirect:visitor/" +  vId;
//        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

    // @PutMapping(value = "/game/{gameId}/add/bot")
    public String addBotPLayerForAGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id,
            @RequestParam(name = "aiLevel", defaultValue = "average") String aiLevel,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, aiLevel, avatar)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)
                || AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.ERROR
                || AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.HUMAN
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return "redirect:visitor/"  + vId;
//            return ResponseEntity.badRequest().headers(headers).build();
        }

        // rules - AiLevel bot cannot be Human
        if (AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.HUMAN)
            // todo LOW split username and number
            return "redirect:visitor/"  + vId;
//            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();

        long gameId = Long.parseLong(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            return "redirect:visitor/"  + vId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        // logic
        int sequenceCalculated = playerRepository.countByGame(foundGame.get()) + 1;
        int fiches = (int) (Math.random() * 1000 + 1);

        Player createdAi = new Player(
                null, linkedGame, Role.BOT, fiches, sequenceCalculated,
                Avatar.fromLabelWithDefault(avatar), AiLevel.fromLabelWithDefault(aiLevel));
        createdAi = playerRepository.save(createdAi);
        if (createdAi.getPlayerId() == 0) {
            return "redirect:visitor/"  + vId;
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // 200
        linkedGame.getPlayers().add(createdAi);
        return "redirect:visitor/" + vId;
//        return ResponseEntity.ok().headers(headers).body(linkedGame);

    }

    // @PutMapping(value = "/game/{gameId}/accept/player{playerId}")
    public String acceptInvitationForAGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String gid,
            @PathVariable("playerId") String pid,
            @RequestParam(name = "fiches", defaultValue = "0") String fichesInput) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(gid, pid, fichesInput)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(gid)
                || !StringUtils.isNumeric(pid)
                || !StringUtils.isNumeric(fichesInput)
                || (Integer.parseInt(fichesInput) <= 0)) {
            // 400
            return "redirect:visitor/"  + vId;
//            return ResponseEntity.badRequest().headers(headers).build();
        }

        long gameId = Long.parseLong(gid);
        long playerId = Long.parseLong(pid);
        int fiches = Integer.parseInt(fichesInput);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return "redirect:visitor/"  + vId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return "redirect:visitor/"  + vId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Player linkedPlayer = foundPlayer.get();

        // create initiator
        Player updatedPlayer = linkedPlayer;
        updatedPlayer.setRole(Role.ACCEPTED);
        updatedPlayer.setFiches(fiches);
        updatedPlayer = playerRepository.save(updatedPlayer);
        if (updatedPlayer.getPlayerId() == 0) {
            return "redirect:visitor/"  + vId;
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        int index = linkedGame.getPlayers().indexOf(updatedPlayer);
        List<Player> updatedPlayers = linkedGame.getPlayers();
        updatedPlayers.add(index, updatedPlayer);

        // 200
        linkedGame.setPlayers(updatedPlayers);
        return "redirect:visitor/" + vId;
//        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

    // @PutMapping(value = "/game/{gameId}/decline/player{playerId}")
    public String declineInvitationForAGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @PathVariable("gameId") String gid,
            @PathVariable("playerId") String pid) {
        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(gid, pid)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(gid)
                || !StringUtils.isNumeric(pid)) {
            // 400
            return "redirect:invite/"  + gid;
        }

        long gameId = Long.parseLong(gid);
        long playerId = Long.parseLong(pid);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return "redirect:invite/"  + gid;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return "redirect:invite/"  + gid;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Player linkedPlayer = foundPlayer.get();

        // create initiator
        Player updatedPlayer = linkedPlayer;
        updatedPlayer.setRole(Role.REJECTED);
        updatedPlayer = playerRepository.save(updatedPlayer);
        if (updatedPlayer.getPlayerId() == 0) {
            return "redirect:invite/"  + gid;
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        int index = linkedGame.getPlayers().indexOf(updatedPlayer);
        List<Player> updatedPlayers = linkedGame.getPlayers();
        updatedPlayers.add(index, updatedPlayer);

        // 200
        linkedGame.setPlayers(updatedPlayers);
        return "redirect:invite/"  + gid;
//        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

//    // @PostMapping(value = "/cards/game/{id}/jokers/{jokers}")
//    public ResponseEntity startGame(
//@RequestHeader("visitorId") String vId,
//            @PathVariable("id") String id,
//            @PathVariable("jokers") String jokers
//    ) {
//
//        // header in response
//        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("")
//                .query("")
//                .buildAndExpand(id, jokers)
//                .toUri();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("URI", String.valueOf(uri));
//
//        // validations
//        if (!StringUtils.isNumeric(jokers) || !StringUtils.isNumeric(id)) {
//            // 400
//            return ResponseEntity.badRequest().headers(headers).build();
//        }
//        long gameId = Long.parseLong(id);
//        int jokersCount = Integer.parseInt(jokers);
//
//        Optional<Game> foundGame = gameRepository.findById(gameId);
//        Game linkedGame;
//        if (!foundGame.isPresent()) {
//            // 404
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
//        } else {
//        }
//        linkedGame = foundGame.get();
//
//        // logic
//        List<Card> createdCards = linkedGame.getCards();
//
//        for (Card createdCard : createdCards) {
//            cardRepository.save(createdCard);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(createdCards);
//    }
//

    @GetMapping("/game/{gameId}")
    public String getGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "gameId", id);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:setup/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // 3 - process
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());
        log.warn("GetMapping: setup/{visitorId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:setup/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }

    @DeleteMapping("/game/{gameId}")
    public String deleteGame(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "gameId", id);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // 3 - process
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // delete
        gameRepository.deleteById(flowDTO.getSuppliedGameId());
        flowDTO.setQasinoGame(null);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: visitor/{visitorId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }

    @GetMapping("/league/{leagueId}")
    public String getLeague(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("leagueId") String id
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "leagueId", id);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:league/" + id;
        }
        // get all entities
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: visitor/{visitorId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:league/" + id;
    }

    @PostMapping(value = "/league/{leagueName}/visitor/{visitorId}")
    public String createLeague(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @PathVariable("leagueName") String name,
            @PathVariable("visitorId") String visitorId
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("leagueName", name, "visitorId", visitorId);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();

            return "redirect:league/" + flowDTO.getQasinoResponse().getPageLeague().getSelectedLeague().getLeagueId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create - League for Visitor
        output = createNewLeagueAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:league/" + flowDTO.getQasinoResponse().getPageLeague().getSelectedLeague().getLeagueId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: visitor/{visitorId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:league/" + flowDTO.getQasinoResponse().getPageLeague().getSelectedLeague().getLeagueId();
    }

    @PutMapping(value = "/league/{leagueId}")
    public String updateLeague(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("leagueId") String id,
            @RequestParam(name = "leagueName", defaultValue = "") String leagueName
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "leagueId", id, "leagueName", leagueName);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:league/" + flowDTO.getQasinoResponse().getPageLeague().getSelectedLeague().getLeagueId();
        }
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:league/" + flowDTO.getQasinoResponse().getPageLeague().getSelectedLeague().getLeagueId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // update
        if (!StringUtils.isEmpty(flowDTO.getSuppliedLeagueName())) {
            int sequence = (int) (leagueRepository.countByName(leagueName) + 1);
            flowDTO.getQasinoGameLeague().setName(leagueName);
            flowDTO.getQasinoGameLeague().setNameSequence(sequence);
        }
        leagueRepository.save(flowDTO.getQasinoGameLeague());
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: visitor/{visitorId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());
        return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }

    @DeleteMapping("/league/{leagueId}")
    public String deleteLeague(
            Model model,
            @ModelAttribute QasinoResponse qasinoResponse,
            BindingResult result,
            Errors errors, RedirectAttributes ra,
            HttpServletResponse response,

            @RequestHeader("visitorId") String vId,
            @PathVariable("leagueId") String id
    ) {
        // 1 - map input
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "leagueId", id);
        // 2 - validate input
        if (!flowDTO.validateInput() || errors.hasErrors()) {
            log.warn("Errors exist!!: {}", errors);
            prepareQasinoResponse(response, flowDTO);
//            flowDTO.setAction("Username incorrect");
            model.addAttribute(flowDTO.getQasinoResponse());
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
        }
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
//            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // delete
        // TODO check if league does not have games any more..
        leagueRepository.deleteById(flowDTO.getSuppliedLeagueId());
        flowDTO.setQasinoGameLeague(null);
        // 4 - return response
        prepareQasinoResponse(response, flowDTO);
        model.addAttribute(flowDTO.getQasinoResponse());

        log.warn("GetMapping: visitor/{visitorId}");
//        log.warn("HttpServletResponse: {}", response.getHeaderNames());
//        log.warn("Model: {}", model);
//        log.warn("Errors: {}", errors);
        log.warn("get qasinoResponse: {}", flowDTO.getQasinoResponse());

        return "redirect:visitor/" + flowDTO.getQasinoResponse().getPageVisitor().getSelectedVisitor().getVisitorId();
    }
}


