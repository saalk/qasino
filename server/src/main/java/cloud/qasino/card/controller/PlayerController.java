package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.entity.enums.Avatar;
import cloud.qasino.card.entity.enums.Type;
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
        int gameId = Integer.valueOf(id);
        // logic
        Boolean isHuman = BooleanUtils.toBooleanObject(human);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            return ResponseEntity.notFound().headers(headers).build();
        }

        ArrayList players = (ArrayList) playerRepository.findByGameOrderBySequenceAsc(foundGame.get());
        // todo add human
        return ResponseEntity.ok().headers(headers).body(players);
    }

    // C human only - can be tested
    @PostMapping(value = "players/games/{gameId}/users{userId}")
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
        Game linkedGame = null;
        if (foundGame.isPresent()) {
            linkedGame = foundGame.get();
        } else {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        Optional<User> foundUser = userRepository.findById(userId);
        User linkedUser = null;
        if (foundUser.isPresent()) {
            linkedUser = foundUser.get();
        } else {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        int sequenceCalculated = playerRepository.countByGame(linkedGame) + 1;
        Player createdPlayer = playerRepository.save(new Player(linkedUser, linkedGame, sequenceCalculated,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN));
        if (createdPlayer == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().headers(headers).body(createdPlayer);
        }
    }

     // C ai only - can be tested
    @PostMapping(value = "/players/games/{id}")
    public ResponseEntity addPlayerForGame(
            @PathVariable("id") String id,
            @RequestParam(name = "aiLevel", defaultValue = "medium") String aiLevel,
             @RequestParam(name = "avatar", defaultValue = "elf") String avatar) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, aiLevel)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)
                || AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.ERROR
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
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
        int sequenceCalculated = playerRepository.countByGame(foundGame.get()) + 1;

        Player createdPlayer = new Player(null, linkedGame, sequenceCalculated);
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

    // R - ok can be tested
    @GetMapping("/players/{id}")
    public ResponseEntity<Optional<Player>> getPlayer(
            @PathVariable("id") String id
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(id)
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
    @PutMapping(value = "/players/{id}", params = {"avatar", "aiLevel"})
    public ResponseEntity<Player> updatePlayer(
            @PathVariable("id") String id,
            @RequestParam(name = "avatar", defaultValue = "elf") String avatar,
            @RequestParam(name = "aiLevel", defaultValue = "medium") String aiLevel
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, avatar, aiLevel)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)
                || AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.ERROR
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

        int playerId = Integer.parseInt(id);
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Player updatedPlayer = foundPlayer.get();
        if (!StringUtils.isEmpty(avatar)) {
            updatedPlayer.setAvatar(Avatar.fromLabelWithDefault(avatar));
        }
        if (!StringUtils.isEmpty(aiLevel)) {
            updatedPlayer.setAiLevel(AiLevel.fromLabelWithDefault(aiLevel));
        }
        updatedPlayer = playerRepository.save(updatedPlayer);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedPlayer);
    }

    // U - can be tested
    @PutMapping(value = "/players/{id}/{order}")
    public ResponseEntity<Game> updateSequence(
            @PathVariable("id") String id,
            @PathVariable("order") String order
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(id, order)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if ((!StringUtils.isNumeric(id)
         || (!StringUtils.isNumeric(order)))){
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int playerId = Integer.parseInt(id);

        // logic get player
        Optional<Player> foundPlayer = playerRepository.findById(playerId);

        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        Optional<Game> updatedGame = gameRepository.findById(foundPlayer.get().getGame().getGameId());
        if (order.equalsIgnoreCase("1")) {
            updatedGame.get().switchPlayers(-1,-1);
            gameRepository.save(updatedGame.get());
            return ResponseEntity.ok(updatedGame.get());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
    }

    // D - ok can be tested
    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(
            @PathVariable("id") String id
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(id)
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
