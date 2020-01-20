package cloud.qasino.card.controller;

import cloud.qasino.card.domain.qasino.Card;
import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.entity.*;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.entity.enums.playingcard.Location;
import cloud.qasino.card.entity.enums.playingcard.Rank;
import cloud.qasino.card.repositories.*;
import cloud.qasino.card.statemachine.QasinoStateMachine;
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
import java.util.Random;

// basic path /qasino
// basic header @RequestHeader(value "user", required = true) int userId" // else 400
//
// 200 - ok
// 201 - created
// 400 - bad request - error/reason "url ... not available"
// 404 - not found - error/message "invalid value x for y" + reason [missing]
// 412 - precondition failed = error/message - "violation of rule z"
// 500 - internal server error

@RestController
public class GamesController {

    private PlayingCardRepository playingCardRepository;
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    @Autowired
    public GamesController(
            PlayingCardRepository playingCardRepository,
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            UserRepository userRepository,
            EventRepository eventRepository) {

        this.playingCardRepository = playingCardRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    // GamesController - special POST and GET only for GAME - has state machine
    // /api/games/{type}/INIT/league/{id}/user/{id} -> POST game+league+deck+user // INITIATED
    // /api/games/{type}INIT/league/{id}/user/{id}/player/{aiLevel} -> POST +player // INITIATED

    // /api/games/{id}/SETUP/classifications -> PUT game bet fiches + classifications // PREPARED
    // /api/games/{id}/INVITE/user/{id} -> POST players // PENDING_INVITATIONS
    // /api/games/{id}/ACCEPT -> PUT player fiches // PREPARED
    // /api/games/{id}/INVITE/bot -> POST players // PREPARED
    // /api/games/{id}/WITHDRAW/bot -> DELETE players // PREPARED
    // /api/games/{id}/WITHDRAW/user{id} -> DELETE players // PREPARED
    // /api/game/{id}/SHUFFLE -> POST playingcards deal first card // PLAYING

    // C- tested ok
    @PostMapping(value = "/games/{type}")
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

        Game startedGame = gameRepository.save(new Game(null, type,
                style, ante));

        if (startedGame.getGameId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);
        }
    }

    // C Game and Players - tested
    @PostMapping(value = "/api/games/{type}/players/{aiLevel}")
    public ResponseEntity<Game> setupGame(
            @PathVariable("id") String id,
            @PathVariable("type") String type,
            @PathVariable("aiLevel") String aiLevel,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String ante,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, type, aiLevel, style, ante, avatar)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(ante)
                || !StringUtils.isNumeric(id)
                || Type.fromLabelWithDefault(type) == Type.ERROR
                || AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.ERROR
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR ){
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int userId = Integer.parseInt(id);

        Optional<User> foundUser = userRepository.findById(userId);
        User linkedUser;
        if (!foundUser.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        } else {
        }
        linkedUser = foundUser.get();

        // create game
        Game startedGame = gameRepository.save(new Game(null, type,
                style, Integer.parseInt(ante)));
        if (startedGame.getGameId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        Player createdAi = null;
        Player createdHuman = null;

        // create human and ai player
        createdHuman = playerRepository.save(new Player(linkedUser, startedGame,
                linkedUser.getBalance(), 1,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN, true));
        if (createdHuman.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
        createdAi = playerRepository.save(new Player(null, startedGame, linkedUser.getBalance(), 2,
                Avatar.fromLabelWithDefault(avatar), AiLevel.fromLabelWithDefault(aiLevel),false));
        if (createdAi.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        List<Player> newPlayers = new ArrayList<>();
        newPlayers.add(createdHuman);
        newPlayers.add(createdAi);
        startedGame.setPlayers(newPlayers);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);

    }


    // C human only - todo can be tested gives error 404??
    // todo check max
    @PostMapping(value = "/players/games/{gameId}/users{userId}")
    public ResponseEntity<Player> addHuman(
            @PathVariable("gameId") String gId,
            @PathVariable("userId") String uId,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(gId,uId, avatar)
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

        int gameId = Integer.parseInt(gId);
        int userId = Integer.parseInt(uId);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        Optional<User> foundUser = userRepository.findById(userId);
        if (!foundUser.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        User linkedUser = foundUser.get();

        int sequenceCalculated = (playerRepository.countByGame(linkedGame)) + 1;
        Player createdPlayer = playerRepository.save(new Player(linkedUser, linkedGame,
                sequenceCalculated, linkedUser.getBalance(),
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN,true));
        if (createdPlayer.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        return ResponseEntity.ok().headers(headers).body(createdPlayer);
    }

    // C ai only - tested ok
    // todo check max
    @PostMapping(value = "/players/games/{id}")
    public ResponseEntity addPlayerForGame(
            @PathVariable("id") String id,
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
            // todo LOW split alias and number
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();

        int gameId = Integer.parseInt(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        Game linkedGame = null;
        if (foundGame.isPresent()) {
            linkedGame = foundGame.get();
        } else {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        int sequenceCalculated = playerRepository.countByGame(foundGame.get()) + 1;
        int fiches = (int) (Math.random() * 1000 + 1);

        Player createdPlayer = new Player(null, linkedGame, sequenceCalculated, fiches);
        if (!StringUtils.isEmpty(aiLevel)) {
            createdPlayer.setAiLevel(AiLevel.fromLabelWithDefault(aiLevel));
        }
        if (!StringUtils.isEmpty(avatar)) {
            createdPlayer.setAvatar(Avatar.fromLabelWithDefault(avatar));
        }
        createdPlayer = playerRepository.save(createdPlayer);
        if (createdPlayer.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // 200
        return ResponseEntity.ok().headers(headers).body(createdPlayer);

    }
    //SHUFFLE,     	// The Game is shuffled according to the Type
    @PostMapping(value = "/game/{id}/shuffle", params = {"jokers"})
    public ResponseEntity shuffleGame(
            @PathVariable("id") String id,
            @RequestParam("jokers") String jokers){

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id,jokers)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id) || !StringUtils.isNumeric(jokers))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();

        int gameId = Integer.parseInt(id);
        int jokersToAdd = Integer.parseInt(jokers);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        List<Card> cards = new ArrayList<>();
        cards = Card.newDeck(jokersToAdd);
        int sequence = 0;
        for (Card card : cards) {
            Event event = new Event(foundGame.get(), null, card.getCardId());
            event.setRoundNumber(0);
            event.setMoveNumber(0);
            event.setBet(0);
            eventRepository.save(event);
            // todo go with event to the game engine
            // todo for now create playing cards

            sequence++;
            PlayingCard playingCard = new PlayingCard(card.getCardId(),foundGame.get(), null,
                    sequence,
                    Location.PILE);
            playingCardRepository.save(playingCard);
        }
        List<Event> events = eventRepository.findByGameId(foundGame.get().getGameId());
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(events);

    }
    // LG - tested ok
    @GetMapping(value = "/players/games/{id}")
    public ResponseEntity getPlayersByGame(
            @PathVariable("id") String id,
            @RequestParam(name = "human", defaultValue = "") String human
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, human)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)) {
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int gameId = Integer.parseInt(id);
        // logic
        //todo add boolean logic
        Boolean isHuman = (human.isEmpty() ? null : Boolean.parseBoolean(human));

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            return ResponseEntity.notFound().headers(headers).build();
        }

        List<Player> players = playerRepository.findByGameOrderBySequenceAsc(foundGame.get());
        return ResponseEntity.ok().headers(headers).body(players);
    }
}


