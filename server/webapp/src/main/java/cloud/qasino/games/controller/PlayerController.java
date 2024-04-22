package cloud.qasino.games.controller;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static cloud.qasino.games.configuration.Constants.BASE_PATH;
import static cloud.qasino.games.configuration.Constants.ENDPOINT_PLAYER;

@Slf4j
@RestController
public class PlayerController {

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;

    @Autowired
    public PlayerController(
            GameRepository gameRepository,
            CardRepository cardRepository,
            PlayerRepository playerRepository) {

        this.gameRepository = gameRepository;
        this.cardRepository = cardRepository;
        this.playerRepository = playerRepository;
    }

    // CREATE, GET, PUT, DELETE for single entities
    // /api/player/{id} - GET, DELETE, PUT sequence, PUT avatar, ailevel - rules apply

    // todo LOW - make endpoints
    // /api/playingcard/{id} - GET, DELETE only - rules apply
    // /api/league/{id} - GET, DELETE, PUT name enddate or close direct - rules apply
    // /api/result/{id} - GET, DELETE, PUT name - rules apply

   // @GetMapping("/player/{playerId}")
    public ResponseEntity<Optional<Player>> getPlayer(
            @PathVariable("playerId") String id
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
        Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(id));
        if (foundPlayer.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundPlayer);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }
    }

    // tested
    // @PutMapping(value = "/player/{playerId}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable("playerId") String id,
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

        long playerId = Long.parseLong(id);
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

    // todo LOW does not work
    // @PutMapping(value = "/player/{playerId}/{order}")
    public ResponseEntity<Game> updateSequence(
            @PathVariable("playerId") String id,
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
        String[] orders = new String[]{"up", "down"};
        if (!StringUtils.isNumeric(id)
                || !StringUtils.isNumeric(id)
                || !Arrays.asList(orders).contains(order))
        {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        long playerId = Long.parseLong(id);
        int orderValue = Integer.parseInt(order);

        // logic get player
        Optional<Player> foundPlayer = playerRepository.findById(playerId);

        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Optional<Game> updatedGame = gameRepository.findById(foundPlayer.get().getGame().getGameId());
        if (order == "up" ) {
            // todo LOW ordering does not work
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

    // tested
    // @DeleteMapping("/player/{playerId}")
    public ResponseEntity<Player> deletePlayer(
            @PathVariable("playerId") String id
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
        long playerId = Long.parseLong(id);

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
