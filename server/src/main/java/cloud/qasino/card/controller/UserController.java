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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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

    // C special - default Game and Players (human and one bot)
    @PostMapping(value = "/users/{userId}/games/{type}/players/{aiLevel}",
            params = {"style", "ante"})
    public ResponseEntity<Game> setupGame(
            @PathVariable("userId") int userId,
            @PathVariable("type") String type,
            @PathVariable("aiLevel") String aiLevel,
            @RequestParam(name = "style", defaultValue = "") String style,
            @RequestParam(name = "ante", defaultValue = "20") Integer ante
    ) {

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/users/{userId}/games/{type}?style=&ante=")
                .buildAndExpand(userId, type, style, ante)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Mapping", "/users/{userId}/games/{type}?style=&ante=");

        Game startedGame = null;

        // check user
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound()
                    .location(uri)
                    .build();
        }
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        // create game
        startedGame = gameRepository.save(new Game(Type.valueOf(type), style, (int) ante));
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
            return ResponseEntity.created(uri)
                    .header("Custom-Header","/users/{userId}/games/{type}?style&ante")
                    .body(startedGame);
        }

    }

    // normal CRUD

    // LP
    @GetMapping(value = "/users")
    public ResponseEntity getAllUser(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "4") String max
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(page, max)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(page) || !StringUtils.isNumeric(max))
            return ResponseEntity.badRequest()cd ...headers(headers).build();
        int maximum = Integer.parseInt(max);
        int pages = Integer.parseInt(page);

        // logic
        Pageable pageable = PageRequest.of(pages, maximum, Sort.by(
                Order.asc("ALIAS"),
                Order.desc("ALIAS_SEQ")));

        ArrayList users = (ArrayList) userRepository.findAllUsersWithPage(pageable);

        return ResponseEntity.ok()
                .headers(headers)
                .body(users);
    }

    // C
    @PostMapping(value = "/users/{alias}", params = {"email"})
    public ResponseEntity addUser(
            @PathVariable("alias") String alias,
            @RequestParam(name = "email", defaultValue = "") String email
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
            @PathVariable String id
    ) {
        if (!StringUtils.isNumeric(id) || !userRepository.existsById(Integer.parseInt(id))) {
            return ResponseEntity.notFound().build();
        } else {
            Optional<User> foundUser = userRepository.findById(Integer.parseInt(id));
            return ResponseEntity.ok(foundUser);
        }
    }

    // U
    @PostMapping(value = "/users/{id}", params = {"alias", "email"})
    public ResponseEntity<User> updateUser(
            @PathVariable("id") int id,
            @RequestParam(name = "alias", defaultValue = "") String alias,
            @RequestParam(name = "email", defaultValue = "") String email
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
    ) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
