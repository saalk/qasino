package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.entity.enums.Avatar;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RestController
public class PlayerController {

    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private UserRepository userRepository;

    @Autowired
    public PlayerController(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            UserRepository userRepository) {

        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    // normal CRUD

    // LG - can be tested
    @GetMapping(value = "/players/games/{id}}", params = {"human"})
    public ResponseEntity getPlayersByGame(
            @PathVariable("id") String id,
            @RequestParam(name = "human") String human
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

        // logic
        Boolean isHuman = BooleanUtils.toBooleanObject(human);
        Optional<Game> foundGame = gameRepository.findById(Integer.valueOf(id));
        if (!foundGame.isPresent()) {
           return ResponseEntity.notFound().headers(headers).build();
        }

        ArrayList players = (ArrayList) playerRepository.findByGameOrderBySequenceAsc(foundGame.get());
        // todo add human
        return ResponseEntity.ok().headers(headers).body(players);
    }

    // todo C special - add a Player based on a User (always a human) to Game
    @PostMapping(value = "players/games/{gameId}/users{userId}")
    public ResponseEntity<Player> addHuman(
            @PathVariable("gameId") int gameId,
            @PathVariable("userId") int userId,
            @RequestParam(name = "avatar", defaultValue = "ELF") String avatar) {
        Game foundGame = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + gameId));

        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        int sequenceCalculated = playerRepository.countByGame(foundGame) + 1;
        Player createdPlayer = playerRepository.save(new Player(foundUser, foundGame, sequenceCalculated,
                Avatar.fromLabel(avatar),AiLevel.HUMAN));
        if (createdPlayer == null) {
            return ResponseEntity.notFound().build();
        } else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdPlayer.getPlayerId())
                    .toUri();

            return ResponseEntity.created(uri).body(createdPlayer);
        }
    }

    // todo C special - add a PLayer not based on a User (always a bot) to Game
    @PostMapping(value = "/players/{aiLevel}/games/{gameId}")
    public ResponseEntity<Player> AddBot(
            @PathVariable("aiLevel") String aiLevel,
            @PathVariable("gameId") int gameId,
            @RequestParam(name = "avatar", defaultValue = "ELF") String avatar
        ) {

        Game foundGame = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + gameId));

        int sequenceCalculated = playerRepository.countByGame(foundGame) + 1;

        Player createdPlayer = playerRepository.save(new Player(null, foundGame, sequenceCalculated,Avatar.fromLabel(avatar),
                AiLevel.fromLabel(aiLevel)));
        if (createdPlayer.getPlayerId() < 1) {
            return ResponseEntity.notFound().build();
        } else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdPlayer.getPlayerId())
                    .toUri();

            return ResponseEntity.created(uri).body(createdPlayer);
        }
    }

    // C - can be tested
    @PostMapping(value = "/players/games/{id}")
    public ResponseEntity addPlayerForGame(
            @PathVariable("id") String id,
            @RequestParam(name = "aiLevel", defaultValue = "MEDIUM") String aiLevel
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, aiLevel)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id) ){
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

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
        Player createdPlayer = new Player(null, linkedGame, 1);
        if (!StringUtils.isEmpty(aiLevel)) {
            createdPlayer.setAiLevel(AiLevel.fromLabel(aiLevel));
        }
        createdPlayer = playerRepository.save(createdPlayer);
        if (createdPlayer.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        // 200
        return ResponseEntity.ok().headers(headers).body(createdPlayer);

    }

    // R - ok can be tested
    @GetMapping("/players/{id}")
    public ResponseEntity<Optional<Player>> getPlayer(
            @PathVariable("id") String id
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
        Optional<Player> foundPlayer = playerRepository.findById(Integer.parseInt(id));
        if (foundPlayer.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundPlayer);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }
    }

    // U - ok can be tested
    @PutMapping(value = "/players/{id}", params={"avatar", "aiLevel"})
    public ResponseEntity<Player> updatePlayer(
            @PathVariable("id") String id,
            @RequestParam(name = "avatar", defaultValue = "ELF") String avatar,
            @RequestParam(name = "aiLevel", defaultValue = "MEDIUM") String aiLevel
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(avatar, aiLevel)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        int playerId = Integer.parseInt(id);
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Player updatedPlayer = foundPlayer.get();
        if (!StringUtils.isEmpty(avatar)) {
            updatedPlayer.setAvatar(Avatar.fromLabel(avatar));
        }
        if (!StringUtils.isEmpty(aiLevel)) {
            updatedPlayer.setAiLevel(AiLevel.fromLabel(aiLevel));
        }
        updatedPlayer = playerRepository.save(updatedPlayer);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedPlayer);
    }

    // todo U special - move sequence up or down
    @PutMapping(value = "/players/{id}/{sequence}")
    public ResponseEntity<Player> updateSequence(
            @PathVariable("id") int id,
            @PathVariable("sequence") String sequence
    ) {

        Optional<Player> foundPlayer = playerRepository.findById(id);

        if (foundPlayer == null) {
            return ResponseEntity.notFound().build();
        } else {

            Player updatePlayer = foundPlayer.get();
            // todo check actions and update others
            if (sequence.startsWith("U")) {
                updatePlayer.setSequence(updatePlayer.getSequence() - 1);
            } else {
                updatePlayer.setSequence(updatePlayer.getSequence() + 1);
            }

            playerRepository.save(updatePlayer);
            return ResponseEntity.ok(updatePlayer);
        }
    }

    // D - ok can be tested
    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(
            @PathVariable("id") String id
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
        int playerId = Integer.parseInt(id);
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        playerRepository.deleteById(playerId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();

    }
}
