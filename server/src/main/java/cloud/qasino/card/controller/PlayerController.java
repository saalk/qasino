package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.entity.enums.Avatar;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    // L
    @GetMapping(value = "/players/{gameId}}", params = {"human"})
    public ResponseEntity getPlayersByGame(
            @PathVariable("gameId") int gameId,
            @RequestParam(name = "human", defaultValue = "true") Boolean human
    ) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + gameId));

        ArrayList players = (ArrayList) playerRepository.findByGameOrderBySequenceAsc(game);
        // todo add human
        return ResponseEntity.ok(players);
    }

    // C special - add a Player based on a User (always a human) to Game
    @PostMapping(value = "players/games/{gameId}/users{userId}", params = {"avatar"})
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

    // C special - add a PLayer not based on a User (always a bot) to Game
    @PostMapping(value = "/players/{aiLevel}/games/{gameId}", params={"avatar"})
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

    // R
    @GetMapping("/players/{id}")
    public ResponseEntity<Optional<Player>> getPlayer(
            @PathVariable("id") int id
    ) {
        if (!playerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            Optional<Player> foundPlayer = playerRepository.findById(id);
            return ResponseEntity.ok(foundPlayer);
        }
    }

    // U
    @PostMapping(value = "/players/{id}", params={"avatar", "aiLevel"})
    public ResponseEntity<Player> updatePlayer(
            @PathVariable("id") int id,
            @RequestParam(name = "avatar", defaultValue = "ELF") String avatar,
            @RequestParam(name = "aiLevel", defaultValue = "MEDIUM") String aiLevel
    ) {

        Optional<Player> foundPlayer = playerRepository.findById(id);

        if (foundPlayer == null) {
            return ResponseEntity.notFound().build();
        } else {

            Player updatePlayer = foundPlayer.get();
            updatePlayer.setAvatar(Avatar.fromLabel(avatar));
            updatePlayer.setAiLevel(AiLevel.fromLabel(aiLevel));
            playerRepository.save(updatePlayer);

            return ResponseEntity.ok(updatePlayer);
        }
    }

    // U special - move sequence up or down
    @PostMapping(value = "/players/{id}/{sequence}")
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

    // D
    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(
            @PathVariable("id") int id
            // ,Model model
    ) {
        playerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
