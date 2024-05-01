package cloud.qasino.games.controller;

import cloud.qasino.games.action.CountQasinoTotals;
import cloud.qasino.games.action.FindAllEntitiesForInputAction;
import cloud.qasino.games.action.MapQasinoResponseFromDto;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.event.EventOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import static cloud.qasino.games.configuration.Constants.DEFAULT_PAWN_SHIP_BOT;

@RestController
public class GameController {

    private VisitorRepository visitorRepository;
    private LeagueRepository leagueRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;

    EventOutput.Result output;

    @Autowired
    FindAllEntitiesForInputAction findAllEntitiesForInputAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CountQasinoTotals countQasinoTotals;
    @Autowired
    MapQasinoResponseFromDto mapQasinoResponseFromDto;

    @Autowired
    public GameController(
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

    @PostMapping(value = "/game/setup/{type}/visitor/{visitorId}")
    public ResponseEntity<Qasino> setupGameWithVisitorPlayer(
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
                "gameTrigger", "setup"
        );
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create game no league
        flowDTO.setQasinoGame(gameRepository.save(new Game(
                null,
                type,
                flowDTO.getQasinoVisitor().getVisitorId(),
                style,
                Integer.parseInt(ante))));
        // todo move to find all entities with if
        flowDTO.setSuppliedGameId(flowDTO.getSuppliedGameId());
        // create human player for visitor with role initiator
        Player createdHuman = new Player(
                flowDTO.getQasinoVisitor(),
                flowDTO.getQasinoGame(),
                Role.INITIATOR,
                flowDTO.getQasinoVisitor().getBalance(),
                1,
                Avatar.fromLabelWithDefault(avatar),
                AiLevel.HUMAN);
        flowDTO.setInitiatingPlayer(playerRepository.save(createdHuman));
        // build response
        findAllEntitiesForInputAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        countQasinoTotals.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PostMapping(value = "/game/setup/{type}/visitor/{visitorId}/bot/{aiLevel}")
    public ResponseEntity<Qasino> setupGameWithVisitorPlayerAndBot(
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
                "gameTrigger", "setup"
        );
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create game no league
        flowDTO.setQasinoGame(gameRepository.save(new Game(null, type,
                flowDTO.getQasinoVisitor().getVisitorId(), style, Integer.parseInt(ante))));
        // todo move to find all entities with if
        flowDTO.setSuppliedGameId(flowDTO.getSuppliedGameId());
        // create human player for visitor with role initiator
        Player createdHuman = new Player(
                flowDTO.getQasinoVisitor(),
                flowDTO.getQasinoGame(),
                Role.INITIATOR,
                flowDTO.getQasinoVisitor().getBalance(),
                1,
                Avatar.fromLabelWithDefault(avatar),
                AiLevel.HUMAN);
        flowDTO.setInitiatingPlayer(playerRepository.save(createdHuman));
        // rules - AiLevel bot cannot be HUMAN
        if (AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.HUMAN) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(409).headers(flowDTO.getHeaders()).build();
        }
        // create bot
        int fiches = (int) (Math.random() * DEFAULT_PAWN_SHIP_BOT + 1);
        Player createdAi = new Player(
                flowDTO.getQasinoVisitor(),
                flowDTO.getQasinoGame(),
                Role.BOT,
                fiches,
                2,
                flowDTO.getSuppliedAvatar(),
                flowDTO.getSuppliedAiLevel());
        playerRepository.save(createdAi);

        // build response
        findAllEntitiesForInputAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        countQasinoTotals.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PostMapping(value = "/game/setup/{type}/league/{leagueId}/visitor/{visitorId}")
    public ResponseEntity<Qasino> setupGameInLeagueWithVisitorPlayer(
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
                "gameTrigger", "setup"
        );
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create game with league
        flowDTO.setQasinoGame(gameRepository.save(new Game(
                flowDTO.getQasinoGameLeague(),
                type,
                flowDTO.getQasinoVisitor().getVisitorId(),
                style,
                Integer.parseInt(ante))));
        // todo move to find all entities with if
        flowDTO.setSuppliedGameId(flowDTO.getSuppliedGameId());
        // create human player for visitor with role initiator
        Player createdHuman = new Player(
                flowDTO.getQasinoVisitor(),
                flowDTO.getQasinoGame(),
                Role.INITIATOR,
                flowDTO.getQasinoVisitor().getBalance(),
                1,
                Avatar.fromLabelWithDefault(avatar),
                AiLevel.HUMAN);
        flowDTO.setInitiatingPlayer(playerRepository.save(createdHuman));
        // build response
        findAllEntitiesForInputAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        countQasinoTotals.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PostMapping(value = "/game/setup/{type}/league/{leagueId}/visitor/{visitorId}/bot/{aiLevel}")
    public ResponseEntity<Qasino> setupGameInLeagueWithVisitorPlayerAndBot(
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
                "gameTrigger", "setup"
        );
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // create game with league
        flowDTO.setQasinoGame(gameRepository.save(new Game(
                flowDTO.getQasinoGameLeague(),
                type,
                flowDTO.getQasinoVisitor().getVisitorId(),
                style,
                Integer.parseInt(ante))));
        // todo move to find all entities with if
        flowDTO.setSuppliedGameId(flowDTO.getSuppliedGameId());
        // create human player for visitor with role initiator
        Player createdHuman = new Player(
                flowDTO.getQasinoVisitor(),
                flowDTO.getQasinoGame(),
                Role.INITIATOR,
                flowDTO.getQasinoVisitor().getBalance(),
                1,
                Avatar.fromLabelWithDefault(avatar),
                AiLevel.HUMAN);
        flowDTO.setInitiatingPlayer(playerRepository.save(createdHuman));
        // rules - AiLevel bot cannot be HUMAN
        if (AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.HUMAN) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(409).headers(flowDTO.getHeaders()).build();
        }
        // create bot
        int fiches = (int) (Math.random() * DEFAULT_PAWN_SHIP_BOT + 1);
        playerRepository.save(new Player(
                flowDTO.getQasinoVisitor(),
                flowDTO.getQasinoGame(),
                Role.BOT,
                fiches,
                2,
                flowDTO.getSuppliedAvatar(),
                flowDTO.getSuppliedAiLevel()));
        // build response
        findAllEntitiesForInputAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        countQasinoTotals.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PutMapping(value = "/game/{gameId}/prepare/{type}/league/{leagueId}")
    public ResponseEntity<Qasino> prepareGameWithTypeLeagueStyleAnte(
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
                "gameTrigger", "prepare"

        );
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // rules - GameState must be in GameStateGroup 'setup' NEW, PENDING_INVITATIONS
        if (flowDTO.getQasinoGame() != null
                && !flowDTO.getQasinoGame().getState().getGroup().equals(GameStateGroup.SETUP)) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(flowDTO.getHeaders()).build();
        }
        // update game
        if (!(flowDTO.getSuppliedLeagueId() == 0)) {
            flowDTO.getQasinoGame().setLeague(flowDTO.getQasinoGameLeague());
        }
        if (!(flowDTO.getSuppliedType() == null)) {
            flowDTO.getQasinoGame().setType(flowDTO.getSuppliedType());
        }
        if (!StringUtils.isEmpty(flowDTO.getSuppliedStyle())) {
            flowDTO.getQasinoGame().setStyle(flowDTO.getSuppliedStyle());
        }
        if (!(flowDTO.getSuppliedAnte() <= 0)) {
            flowDTO.getQasinoGame().setAnte(flowDTO.getSuppliedAnte());
        }
        flowDTO.getQasinoGame().setState(GameState.PREPARED);
        gameRepository.save(flowDTO.getQasinoGame());
        // get all (updated) entities
        findAllEntitiesForInputAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        countQasinoTotals.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
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
            // todo LOW split visitorName and number
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
//    public ResponseEntity setupGame(
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

    // Game crud actions
    // @GetMapping("/game/{gameId}")
    public ResponseEntity<Optional<Game>> getGame(
            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)) {
            return ResponseEntity.badRequest().headers(headers).build();
        }

        // logic
        Optional<Game> foundGame = gameRepository.findById(Long.parseLong(id));
        if (foundGame.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundGame);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }

    }

    // todo LOW work on all sqls, works for new
    // @PutMapping(value = "/game/{gameId}/state/{state}")
    public ResponseEntity<Game> updateGameState(
            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id,
            @PathVariable("state") String state
    ) {
        // todo make string and add fromLabelWithDefault

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, state)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)
                || (GameState.fromLabelWithDefault(state) == GameState.ERROR)) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

        long gameId = Long.parseLong(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Game updateGame = foundGame.get();
        updateGame.setState(GameState.fromLabelWithDefault(state));
        gameRepository.save(updateGame);

        return ResponseEntity.ok().headers(headers).body(updateGame);
    }

    @DeleteMapping("/game/{gameId}")
    public ResponseEntity<Qasino> deleteGame(
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
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // delete
        gameRepository.deleteById(flowDTO.getSuppliedGameId());
        flowDTO.setQasinoGame(null);
        // build response
        findAllEntitiesForInputAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        countQasinoTotals.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        // delete 204 -> 200 otherwise no content in response body
        return ResponseEntity.status(HttpStatus.OK).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

}


