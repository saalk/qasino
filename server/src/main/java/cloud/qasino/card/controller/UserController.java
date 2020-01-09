package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.entity.enums.Type;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public UserController(
            UserRepository userRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository) {

        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    // C special - default Game and Players (human and bot)
    @PostMapping(value = "/users/{userId}/game/{type}", params = {"style", "ante"})
    public ResponseEntity<Game> setupGame(
            @PathVariable("userId") int userId,
            @PathVariable("type") String type,
            @RequestParam("style") String style,
            @RequestParam("ante") int ante
    ) {

        // check user
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        // create game
        Game startedGame = gameRepository.save(new Game(Type.valueOf(type), style, ante));
        if (startedGame == null) {
            return ResponseEntity.notFound().build();
        }

        // create human player
        Player createdPlayer = playerRepository.save(new Player(foundUser, startedGame, 1, null, AiLevel.HUMAN));
        if (createdPlayer == null) {
            return ResponseEntity.notFound().build();
        }

        // create ai player
        Player createdAi = playerRepository.save(new Player(null, startedGame, 2, null,
                AiLevel.MEDIUM));
        if (createdAi == null) {
            return ResponseEntity.notFound().build();
        } else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdPlayer.getPlayerId())
                    .toUri();

            return ResponseEntity.created(uri).body(startedGame);
        }

    }

    // normal CRUD

    // LP
    @GetMapping(value = "/users/all", params = {"max"})
    public ResponseEntity getAllUser(
            @RequestParam("max") int max
    ) {

        max = ((max < 0) || (max > 50)) ? 5 : max;
        Pageable pageable = PageRequest.of(0, max, Sort.by(
                Order.asc("alias"),
                Order.desc("aliasSequence")));

        ArrayList users = (ArrayList) userRepository.findAllUsersWithPage(Pageable.unpaged());
        return ResponseEntity.ok(users);
    }

    // C
    @PostMapping(value = "/users/{alias}", params = {"email"})
    public ResponseEntity<User> addUser(
            @PathVariable("alias") String alias,
            @RequestParam("email") String email
    ) {
        int sequence = (int) (userRepository.countByAlias(alias) + 1);
        User createdUser = userRepository.save(new User(alias, sequence, email));
        if (createdUser == null) {
            return ResponseEntity.notFound().build();
        } else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdUser.getUserId())
                    .toUri();
            return ResponseEntity.created(uri).body(createdUser);
        }
    }

    // R
    @GetMapping("/users/{id}")
    public ResponseEntity<Optional<User>> getUser(
            @PathVariable("id") int id
            //, Model model
    ) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            Optional<User> foundUser = userRepository.findById(id);
            return ResponseEntity.ok(foundUser);
        }
    }

    // U
    @PostMapping(value = "/users/{id}", params = {"alias", "email"})
    public ResponseEntity<User> updateUser(
            @PathVariable("id") int id,
            @RequestParam("alias") String alias,
            @RequestParam("email") String email
    ) {

        Optional<User> foundUser = userRepository.findById(id);

        int sequence = (int) (userRepository.countByAlias(alias) + 1);
        if (foundUser == null) {
            return ResponseEntity.notFound().build();
        } else {
            User updateUser = foundUser.get();
            updateUser.setAlias(alias);
            updateUser.setAliasSequence(sequence);
            updateUser.setEmail(email);
            userRepository.save(updateUser);

            return ResponseEntity.ok(updateUser);
        }
    }

    // D
    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(
            @PathVariable("id") int id
            // ,Model model
    ) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
