package cloud.qasino.games.controller;

import cloud.qasino.games.action.CalculateQasinoStatistics;
import cloud.qasino.games.action.CreateNewGameAction;
import cloud.qasino.games.action.CreateNewLeagueAction;
import cloud.qasino.games.action.IsGameConsistentForGameEvent;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDto;
import cloud.qasino.games.action.MapQasinoResponseFromDto;
import cloud.qasino.games.action.PrepareGameAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class GameAndLeagueController {

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
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateQasinoStatistics calculateQasinoStatistics;
    @Autowired
    CreateNewLeagueAction createNewLeagueAction;
    @Autowired
    MapQasinoResponseFromDto mapQasinoResponseFromDto;
    @Autowired
    IsGameConsistentForGameEvent isGameConsistentForGameEvent;
    @Autowired
    CreateNewGameAction createNewGameAction;
    @Autowired
    PrepareGameAction prepareGameAction;
    @Autowired
    MapQasinoGameTableFromDto mapQasinoGameTableFromDto;

    @Autowired
    public GameAndLeagueController(
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

    @PostMapping(value = "/game/start/{type}/visitor/{visitorId}")
    public ResponseEntity<QasinoResponse> startGameWithVisitorPlayer(
            @PathVariable("type") String type,
            @PathVariable("visitorId") String id,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables(
                "visitorId", id,
                "type", type,
                "style", style,
                "ante", ante,
                "avatar", avatar,
                "gameEvent", "start"
        );
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

        // create or update the game
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        createNewGameAction.perform(flowDTO);
        loadEntitiesToDtoAction.perform(flowDTO);
        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    @PostMapping(value = "/game/start/{type}/visitor/{visitorId}/bot/{aiLevel}")
    public ResponseEntity<QasinoResponse> startGameWithVisitorPlayerAndBot(
            @PathVariable("type") String type,
            @PathVariable("visitorId") String id,
            @PathVariable("aiLevel") String aiLevel,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {
        // validate
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

        // create or update the game
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        createNewGameAction.perform(flowDTO);
        loadEntitiesToDtoAction.perform(flowDTO);

        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    @PostMapping(value = "/game/start/{type}/league/{leagueId}/visitor/{visitorId}")
    public ResponseEntity<QasinoResponse> startGameInLeagueWithVisitorPlayer(
            @PathVariable("type") String type,
            @PathVariable("visitorId") String vId,
            @PathVariable("leagueId") String lId,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {
        // validate
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

        // create or update the game
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        createNewGameAction.perform(flowDTO);
        loadEntitiesToDtoAction.perform(flowDTO);

        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    @PostMapping(value = "/game/start/{type}/league/{leagueId}/visitor/{visitorId}/bot/{aiLevel}")
    public ResponseEntity<QasinoResponse> startGameInLeagueWithVisitorPlayerAndBot(
            @PathVariable("type") String type,
            @PathVariable("visitorId") String vId,
            @PathVariable("leagueId") String lId,
            @PathVariable("aiLevel") String aiLevel,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {
        // validate
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

        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // update game
        createNewGameAction.perform(flowDTO);
        loadEntitiesToDtoAction.perform(flowDTO);

        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    @PutMapping(value = "/game/{gameId}/validate/{type}/league/{leagueId}")
    public ResponseEntity<QasinoResponse> validateGameWithTypeLeagueStyleAnte(
            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String gid,
            @PathVariable("type") String type,
            @PathVariable("leagueId") Optional<String> lid,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante
    ) {
        // validate
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
        output = isGameConsistentForGameEvent.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
         // update game
        prepareGameAction.perform(flowDTO);
        loadEntitiesToDtoAction.perform(flowDTO);

        // get all (updated) entities
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    // @PutMapping(value = "/game/{gameId}/invite/visitor{visitorId}")
    public ResponseEntity<Game> inviteVisitorForAGame(
            @PathVariable("gameId") String gId,
            @PathVariable("visitorId") String uId,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(gId, uId, avatar)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(gId)
                || !StringUtils.isNumeric(uId)
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

        long gameId = Long.parseLong(gId);
        long visitorId = Long.parseLong(uId);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        if (!foundVisitor.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Visitor linkedVisitor = foundVisitor.get();

        int sequenceCalculated = (playerRepository.countByGame(linkedGame)) + 1;
        // create initiator
        Player createdHuman = new Player(
                linkedVisitor, linkedGame, Role.INVITED, 0, sequenceCalculated,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN);
        createdHuman = playerRepository.save(createdHuman);
        if (createdHuman.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // 200
        linkedGame.getPlayers().add(createdHuman);
        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

    // @PutMapping(value = "/game/{gameId}/add/bot")
    public ResponseEntity<Game> addBotPLayerForAGame(
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
            return ResponseEntity.badRequest().headers(headers).build();
        }

        // rules - AiLevel bot cannot be Human
        if (AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.HUMAN)
            // todo LOW split username and number
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();

        long gameId = Long.parseLong(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // 200
        linkedGame.getPlayers().add(createdAi);
        return ResponseEntity.ok().headers(headers).body(linkedGame);

    }

    // @PutMapping(value = "/game/{gameId}/accept/player{playerId}")
    public ResponseEntity<Game> acceptInvitationForAGame(
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
            return ResponseEntity.badRequest().headers(headers).build();
        }

        long gameId = Long.parseLong(gid);
        long playerId = Long.parseLong(pid);
        int fiches = Integer.parseInt(fichesInput);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Player linkedPlayer = foundPlayer.get();

        // create initiator
        Player updatedPlayer = linkedPlayer;
        updatedPlayer.setRole(Role.ACCEPTED);
        updatedPlayer.setFiches(fiches);
        updatedPlayer = playerRepository.save(updatedPlayer);
        if (updatedPlayer.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        int index = linkedGame.getPlayers().indexOf(updatedPlayer);
        List<Player> updatedPlayers = linkedGame.getPlayers();
        updatedPlayers.add(index, updatedPlayer);

        // 200
        linkedGame.setPlayers(updatedPlayers);
        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

    // @PutMapping(value = "/game/{gameId}/decline/player{playerId}")
    public ResponseEntity<Game> declineInvitationForAGame(
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
            return ResponseEntity.badRequest().headers(headers).build();
        }

        long gameId = Long.parseLong(gid);
        long playerId = Long.parseLong(pid);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Player linkedPlayer = foundPlayer.get();

        // create initiator
        Player updatedPlayer = linkedPlayer;
        updatedPlayer.setRole(Role.REJECTED);
        updatedPlayer = playerRepository.save(updatedPlayer);
        if (updatedPlayer.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        int index = linkedGame.getPlayers().indexOf(updatedPlayer);
        List<Player> updatedPlayers = linkedGame.getPlayers();
        updatedPlayers.add(index, updatedPlayer);

        // 200
        linkedGame.setPlayers(updatedPlayers);
        return ResponseEntity.ok().headers(headers).body(linkedGame);
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
    public ResponseEntity<QasinoResponse> getGame(
            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "gameId", id);
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities and build reponse
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    @DeleteMapping("/game/{gameId}")
    public ResponseEntity<QasinoResponse> deleteGame(
            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "gameId", id);
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
        gameRepository.deleteById(flowDTO.getSuppliedGameId());
        flowDTO.setQasinoGame(null);
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

    @GetMapping("/league/{leagueId}")
    public ResponseEntity<QasinoResponse> getLeague(
            @RequestHeader("visitorId") String vId,
            @PathVariable("leagueId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "leagueId", id);
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
        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    @PostMapping(value = "/league/{leagueName}/visitor/{visitorId}")
    public ResponseEntity<QasinoResponse> createLeague(
            @PathVariable("leagueName") String name,
            @PathVariable("visitorId") String visitorId
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("leagueName", name, "visitorId", visitorId);
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
        // create - League for Visitor
        output = createNewLeagueAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        loadEntitiesToDtoAction.perform(flowDTO);
        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    @PutMapping(value = "/league/{leagueId}")
    public ResponseEntity<QasinoResponse> updateLeague(
            @RequestHeader("visitorId") String vId,
            @PathVariable("leagueId") String id,
            @RequestParam(name = "leagueName", defaultValue = "") String leagueName
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "leagueId", id, "leagueName", leagueName);
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
        // update
        if (!StringUtils.isEmpty(flowDTO.getSuppliedLeagueName())) {
            int sequence = (int) (leagueRepository.countByName(leagueName) + 1);
            flowDTO.getQasinoGameLeague().setName(leagueName);
            flowDTO.getQasinoGameLeague().setNameSequence(sequence);
        }
        leagueRepository.save(flowDTO.getQasinoGameLeague());
        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasinoResponse());
    }

    @DeleteMapping("/league/{leagueId}")
    public ResponseEntity<QasinoResponse> deleteLeague(
            @RequestHeader("visitorId") String vId,
            @PathVariable("leagueId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId, "leagueId", id);
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
        // TODO check if league does not have games any more..
        leagueRepository.deleteById(flowDTO.getSuppliedLeagueId());
        flowDTO.setQasinoGameLeague(null);
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


