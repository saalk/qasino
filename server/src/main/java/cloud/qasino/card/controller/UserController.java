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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    // C Game and Players - tested
    @PostMapping(value = "/users/{id}/games/{type}/players/{aiLevel}")
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
        Game startedGame = gameRepository.save(new Game(Type.fromLabelWithDefault(type), style, Integer.parseInt(ante)));
        if (startedGame.getGameId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        Player createdAi = null;
        Player createdHuman = null;

        // create human and ai player
        createdHuman = playerRepository.save(new Player(linkedUser, startedGame, 1,
                Avatar.fromLabelWithDefault(avatar), AiLevel.HUMAN));
        if (createdHuman.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
        createdAi = playerRepository.save(new Player(linkedUser, startedGame, 2,
                Avatar.fromLabelWithDefault(avatar), AiLevel.fromLabelWithDefault(aiLevel)));
        if (createdAi.getPlayerId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }

        List<Player> newPlayers = new ArrayList<>();
        newPlayers.add(createdHuman);
        newPlayers.add(createdAi);
        startedGame.setPlayers(newPlayers);

        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);

    }

    // normal CRUD

    // LP - tested
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
            return ResponseEntity.badRequest().headers(headers).build();
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

    // C - tested
    @PostMapping(value = "/users/{alias}")
    public ResponseEntity addUser(
            @PathVariable("alias") String alias,
            @RequestParam(name = "email", defaultValue = "") String email
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(email)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        int sequence = (int) (userRepository.countByAlias(alias) + 1);
        User createdUser = userRepository.save(new User(alias, sequence, email));
        if (createdUser.getUserId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(createdUser);
    }

    // R - tested
    @GetMapping("/users/{id}")
    public ResponseEntity<Optional<User>> getUser(
            @PathVariable String id
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
        Optional<User> foundUser = userRepository.findById(Integer.parseInt(id));
        if (foundUser.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundUser);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }

    }

    // U - tested
    @PutMapping(value = "/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") String id,
            @RequestParam(name = "alias", defaultValue = "") String alias,
            @RequestParam(name = "email", defaultValue = "") String email
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(alias, email)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        int userId = Integer.parseInt(id);
        Optional<User> foundUser = userRepository.findById(userId);
        if (!foundUser.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        User updatedUser = foundUser.get();
        if (!StringUtils.isEmpty(alias)) {
            int sequence = (int) (userRepository.countByAlias(alias) + 1);
            updatedUser.setAlias(alias);
            updatedUser.setAliasSequence(sequence);
        }
        if (!StringUtils.isEmpty(email)) {
            updatedUser.setEmail(email);
        }
        updatedUser = userRepository.save(updatedUser);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedUser);
    }

    // D - tested
    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(
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
        int userId = Integer.parseInt(id);
        Optional<User> foundUser = userRepository.findById(userId);
        if (!foundUser.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        userRepository.deleteById(userId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }
}
