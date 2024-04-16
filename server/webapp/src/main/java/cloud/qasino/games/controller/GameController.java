package cloud.qasino.games.controller;

import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.repository.*;
import cloud.qasino.games.statemachine.GameState;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cloud.qasino.games.configuration.Constants.BASE_PATH;
import static cloud.qasino.games.configuration.Constants.DEFAULT_PAWN_SHIP_BOT;
import static cloud.qasino.games.configuration.Constants.ENDPOINT_GAME;
import static cloud.qasino.games.configuration.Constants.isNullOrEmpty;

// basic path /qasino
// basic header @RequestHeader(value "visitor", required = true) int visitorId" // else 400
//
// 200 - ok
// 201 - created
// 400 - bad request - error/reason "url ... not available"
// 404 - not found - error/message "invalid value x for y" + reason [missing]
// 412 - precondition failed = error/message - "violation of rule z"
// 500 - internal server error

@RestController
public class GameController {

    private VisitorRepository visitorRepository;
    private LeagueRepository leagueRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;

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

    // basic path /qasino
    // basic header @RequestHeader(value "visitor", required = true) int visitorId" // else 400
    //
    // 200 - ok
    // 201 - created
    // 400 - bad request - error/reason "url ... not available"
    // 404 - not found - error/message "invalid value x for y" + reason [missing]
    // 412 - precondition failed = error/message - "violation of rule z"
    // 500 - internal server error

    // /api/game/{id} - GET, DELETE, PUT type, style, ante - rules apply!

    // todo HIGH make endpoints
    // /api/game/{id}/ACCEPT -> PUT player fiches // PREPARED
    // /api/game/{id}/WITHDRAW/bot -> DELETE players // PREPARED
    // /api/game/{id}/WITHDRAW/visitor{id} -> DELETE players // PREPARED

    // Game lifecycle events

    @PostMapping(value = "/game/new/{type}")
    public ResponseEntity<Game> startGame(
            @PathVariable("type") String type,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String inputAnte
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(type, style, inputAnte)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(inputAnte)
                || Type.fromLabelWithDefault(type) == Type.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int ante = Integer.parseInt(inputAnte);

        Game startedGame = gameRepository.save(new Game(null, type, 0,
                style, ante));

        if (startedGame.getGameId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);
        }
    }

    @PostMapping(value = "/game/new/{type}/visitor/{visitorId}")
    public ResponseEntity<Game> setupInitGameWithVisitor(
            @PathVariable("type") String type,
            @PathVariable("visitorId") String uId,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(type, uId, style, ante, avatar)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(ante)
                || !StringUtils.isNumeric(uId)
                || Type.fromLabelWithDefault(type) == Type.ERROR
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

        long visitorId = Long.parseLong(uId);
        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        if (!foundVisitor.isPresent())
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        Visitor linkedVisitor = foundVisitor.get();

        // create game no league
        Game startedGame = gameRepository.save(new Game(null, type,
                linkedVisitor.getVisitorId(), style, Integer.parseInt(ante)));
        if (startedGame.getGameId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // create initiator
        Player createdHuman = new Player(
                linkedVisitor, startedGame, Role.INITIATOR,linkedVisitor.getBalance(), 1,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN);
        createdHuman = playerRepository.save(createdHuman);
        if (createdHuman.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        List<Player> newPlayers = new ArrayList<>();
        newPlayers.add(createdHuman);
        startedGame.setPlayers(newPlayers);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);
    }

    @PostMapping(value = "/game/new/{type}/visitor/{visitorId}/player/{aiLevel}")
    public ResponseEntity<Game> setupInitGameWithVisitorAndPlayer(
            @PathVariable("type") String type,
            @PathVariable("visitorId") String uId,
            @PathVariable("aiLevel") String aiLevel,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(type, uId, aiLevel, style, ante, avatar)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(ante)
                || !StringUtils.isNumeric(uId)
                || Type.fromLabelWithDefault(type) == Type.ERROR
                || AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.ERROR
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

        // rules
        if (AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.HUMAN)
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();

        long visitorId = Long.parseLong(uId);
        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        if (!foundVisitor.isPresent())
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        Visitor linkedVisitor = foundVisitor.get();

        // create game no league
        Game startedGame = gameRepository.save(new Game(null, type,
                linkedVisitor.getVisitorId(), style, Integer.parseInt(ante)));
        if (startedGame.getGameId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // create initiator
        Player createdHuman = new Player(
                linkedVisitor, startedGame, Role.INITIATOR,linkedVisitor.getBalance(), 1,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN);
        createdHuman = playerRepository.save(createdHuman);
        if (createdHuman.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // create bot
        int fiches = (int) (Math.random() * DEFAULT_PAWN_SHIP_BOT + 1);
        Player createdAi = new Player(
                null, startedGame, Role.BOT,fiches, 2,
                Avatar.fromLabelWithDefault(avatar), AiLevel.fromLabelWithDefault(aiLevel));
        createdAi = playerRepository.save(createdAi);
        if (createdAi.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        List<Player> newPlayers = new ArrayList<>();
        newPlayers.add(createdHuman);
        newPlayers.add(createdAi);
        startedGame.setPlayers(newPlayers);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);
    }

    @PostMapping(value = "/game/new/{type}/league/{leagueId}/visitor/{visitorId}")
    public ResponseEntity<Game> setupInitGameInLeagueWithVisitor(
            @PathVariable("type") String type,
            @PathVariable("visitorId") String uId,
            @PathVariable("leagueId") String lId,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(type, lId, uId, style, ante, avatar)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(ante)
                || !StringUtils.isNumeric(uId)
                || !StringUtils.isNumeric(lId)
                || Type.fromLabelWithDefault(type) == Type.ERROR
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

        long visitorId = Long.parseLong(uId);
        long leagueId = Long.parseLong(lId);
        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        Optional<League> foundLeague = leagueRepository.findById(leagueId);
        if (!foundVisitor.isPresent() || !foundLeague.isPresent())
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        Visitor linkedVisitor = foundVisitor.get();
        League linkedLeague = foundLeague.get();

        // create game with league
        Game startedGame = gameRepository.save(new Game(linkedLeague, type,
                linkedVisitor.getVisitorId(), style, Integer.parseInt(ante)));
        if (startedGame.getGameId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // create initiator
        Player createdHuman = new Player(
                linkedVisitor, startedGame, Role.INITIATOR,linkedVisitor.getBalance(), 1,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN);
        createdHuman = playerRepository.save(createdHuman);
        if (createdHuman.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        List<Player> newPlayers = new ArrayList<>();
        newPlayers.add(createdHuman);
        startedGame.setPlayers(newPlayers);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);
    }

    @PostMapping(value = "/game/new/{type}/league/{leagueId}/visitor/{visitorId}/player/{aiLevel}")
    public ResponseEntity<Game> setupInitGameInLEagueWithVisitorAndPlayer(
            @PathVariable("type") String type,
            @PathVariable("visitorId") String uId,
            @PathVariable("leagueId") String lId,
            @PathVariable("aiLevel") String aiLevel,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(type, uId, aiLevel, style, ante, avatar)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(ante)
                || !StringUtils.isNumeric(uId)
                || !StringUtils.isNumeric(lId)
                || Type.fromLabelWithDefault(type) == Type.ERROR
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

        // rules
        if (AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.HUMAN)
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();

        long visitorId = Long.parseLong(uId);
        long leagueId = Long.parseLong(lId);
        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        Optional<League> foundLeague = leagueRepository.findById(leagueId);
        if (!foundVisitor.isPresent() || !foundLeague.isPresent())
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        Visitor linkedVisitor = foundVisitor.get();
        League linkedLeague = foundLeague.get();

        // create game with league
        Game startedGame = gameRepository.save(new Game(linkedLeague, type, linkedVisitor.getVisitorId(),
                style, Integer.parseInt(ante)));
        startedGame.shuffleGame(0);
        List<Card> cards =
                cardRepository.saveAll(startedGame.getCards());
        if (startedGame.getGameId() == 0 || isNullOrEmpty(cards)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // create initiator
        Player createdHuman = new Player(
                linkedVisitor, startedGame, Role.INITIATOR,linkedVisitor.getBalance(), 1,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN);
        createdHuman = playerRepository.save(createdHuman);
        if (createdHuman.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // create bot // todo LOW generated its won fiches
        Player createdAi = new Player(
                null, startedGame, Role.BOT,linkedVisitor.getBalance(), 2,
                Avatar.fromLabelWithDefault(avatar), AiLevel.fromLabelWithDefault(aiLevel));
        createdAi = playerRepository.save(createdAi);
        if (createdAi.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        List<Player> newPlayers = new ArrayList<>();
        newPlayers.add(createdHuman);
        newPlayers.add(createdAi);
        startedGame.setPlayers(newPlayers);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);
    }

    @PutMapping(value = "/game/{gameId}/prepare/{type}/league/{leagueId}")
    public ResponseEntity<Game> updateGameWithTypeLeagueStyleAnte(
            @PathVariable("gameId") String gid,
            @PathVariable("type") String type,
            @PathVariable("leagueId") String lid,
            @RequestParam(name = "style", defaultValue = "") String style,
            @RequestParam(name = "ante", defaultValue = "") String inputAnte
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(gid, style, inputAnte)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(gid)
                || (!inputAnte.isEmpty() & !StringUtils.isNumeric(inputAnte))
        )
            // 400
            return ResponseEntity.badRequest().headers(headers).build();

        long gameId = Long.parseLong(gid);
        int ante = Integer.parseInt(inputAnte);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game updatedGame = foundGame.get();

        // rules
        if (!   ((updatedGame.getState() == GameState.NEW) ||
                (updatedGame.getState() == GameState.PREPARED ))  ) {
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();
        }

        // logic
        if (!StringUtils.isEmpty(style)) {
            updatedGame.setStyle(Style.fromLabelWithDefault(style).getLabel());
        }
        if (!StringUtils.isEmpty(inputAnte) || (ante <= 0)) {
            updatedGame.setAnte(ante);
        }
        updatedGame = gameRepository.save(updatedGame);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedGame);

    }

    @PostMapping(value = "/game/{gameId}/invite/visitor{visitorId}")
    public ResponseEntity<Game> inviteHumanPLayerForGame(
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
                linkedVisitor, linkedGame, Role.INVITED,0, sequenceCalculated,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN);
        createdHuman = playerRepository.save(createdHuman);
        if (createdHuman.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // 200
        linkedGame.getPlayers().add(createdHuman);
        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

    @PostMapping(value = "/game/{gameId}/invite/bot")
    public ResponseEntity<Game> inviteBotPLayerForGame(
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

        // rules
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
                null, linkedGame, Role.BOT,fiches, sequenceCalculated,
                Avatar.fromLabelWithDefault(avatar), AiLevel.fromLabelWithDefault(aiLevel));
        createdAi = playerRepository.save(createdAi);
        if (createdAi.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // 200
        linkedGame.getPlayers().add(createdAi);
        return ResponseEntity.ok().headers(headers).body(linkedGame);

    }

    @PostMapping(value = "/game/{gameId}/accept/player{playerId}")
    public ResponseEntity<Game> acceptHumanPLayerForGame(
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
                || (Integer.parseInt(fichesInput) <=0 )){
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
        updatedPlayers.add(index,updatedPlayer);

        // 200
        linkedGame.setPlayers(updatedPlayers);
        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }

    @PostMapping(value = "/game/{gameId}/decline/player{playerId}")
    public ResponseEntity<Game> declineHumanPLayerForGame(
            @PathVariable("gameId") String gid,
            @PathVariable("playerId") String pid){
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
                || !StringUtils.isNumeric(pid)){
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
        updatedPlayers.add(index,updatedPlayer);

        // 200
        linkedGame.setPlayers(updatedPlayers);
        return ResponseEntity.ok().headers(headers).body(linkedGame);
    }


//    @PostMapping(value = "/cards/game/{id}/jokers/{jokers}")
//    public ResponseEntity setupGame(
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

    @PutMapping(value = "/game/{gameId}/play")
    public ResponseEntity<Game> updateGameState(
            @PathVariable("gameId") String id
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();}

        long gameId = Long.parseLong(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // todo HIGH make rules and validate

        // logic
        Game updateGame = foundGame.get();
        updateGame.setState(GameState.PLAYING);
        gameRepository.save(updateGame);

        return ResponseEntity.ok().headers(headers).body(updateGame);
    }

    // Game crud actions
    @GetMapping("/game/{gameId}")
    public ResponseEntity<Optional<Game>> getGame(
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
    @PutMapping(value = "/game/{gameId}/state/{state}")
    public ResponseEntity<Game> updateGameState(
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
            return ResponseEntity.badRequest().headers(headers).build();}

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

    // tested todo LOW work on constraint of players delete first
    @DeleteMapping("/game/{gameId}")
    public ResponseEntity<Game> deleteGame(
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
        if (!StringUtils.isNumeric(id))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();

        long gameId = Long.parseLong(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        gameRepository.deleteById(gameId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

}


