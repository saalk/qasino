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
import java.util.List;
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
        Player createdPlayer = playerRepository.save(new Player(linkedUser, linkedGame, sequenceCalculated,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN));
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

    // R - tested ok
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

    // U - tested ok
    @PutMapping(value = "/players/{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable("id") String id,
            @RequestParam(name = "avatar", defaultValue = "") String avatar,
            @RequestParam(name = "aiLevel", defaultValue = "") String aiLevel
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

    // U - todo can be tested
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
        int orderValue = Integer.parseInt(order);

        // logic get player
        Optional<Player> foundPlayer = playerRepository.findById(playerId);

        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        // todo logic this does not work
        Optional<Game> updatedGame = gameRepository.findById(foundPlayer.get().getGame().getGameId());
        if (orderValue >0 ) { // 1 = up, -1 means down
            updatedGame.get().switchPlayers(-1, -1);
            gameRepository.save(updatedGame.get());
            return ResponseEntity.ok(updatedGame.get());
        } else {
            updatedGame.get().switchPlayers(1, 1);
            gameRepository.save(updatedGame.get());
            return ResponseEntity.ok(updatedGame.get());
        }
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
    }

    // D - tested ok
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
