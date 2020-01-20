package cloud.qasino.card.controller;

import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.PlayingCard;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.PlayingCardRepository;
import cloud.qasino.card.repositories.UserRepository;
import cloud.qasino.card.statemachine.QasinoStateMachine;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// basic path /qasino
// basic header @RequestHeader(value "user", required = true) int userId" // else 400
//
// 200 - ok
// 201 - created
// 204 - no content - for deleted
// 400 - bad request - error/reason "url ... not available"
// 404 - not found - error/message "invalid value x for y" + reason [missing]
// 412 - precondition failed = error/message - "violation of rule z"
// 500 - internal server error

@Slf4j
@RestController
public class CRUDController {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private PlayingCardRepository playingCardRepository;

    @Autowired
    public CRUDController(
            UserRepository userRepository,
            GameRepository gameRepository,
            PlayingCardRepository playingCardRepository,
            PlayerRepository playerRepository) {

        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    // GET, PUT, DELETE for all entities
    // /api/users/{id} - GET, DELETE, PUT alias, email only
    // /api/games/{id} - GET, DELETE, PUT type, style, ante - rules apply!
    // /api/players/{id} - GET, DELETE, PUT sequence, PUT avatar, ailevel - rules apply

    // todo LOW - make endpoints
    // /api/playingcards/{id} - GET, DELETE only - rules apply
    // /api/leagues/{id} - GET, DELETE, PUT name enddate or close direct - rules apply
    // /api/result/{id} - GET, DELETE, PUT name - rules apply

    // tested
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

    // tested
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

    // tested
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


    // tested
    @GetMapping("/games/{id}")
    public ResponseEntity<Optional<Game>> getGame(
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
        Optional<Game> foundGame = gameRepository.findById(Integer.parseInt(id));
        if (foundGame.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundGame);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }

    }

    // tested TODO LOW can be test ante changes or empty
    @PutMapping(value = "/games/{id}")
    public ResponseEntity<Game> updateGame(
            @PathVariable("id") String id,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "") String inputAnte
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, style, inputAnte)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)
                || (!inputAnte.isEmpty() & !StringUtils.isNumeric(inputAnte))
        )
            // 400
            return ResponseEntity.badRequest().headers(headers).build();

        int gameId = Integer.parseInt(id);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Game updatedGame = foundGame.get();
        if (!StringUtils.isEmpty(style)) {
            updatedGame.setStyle(Style.fromLabelWithDefault(style).getLabel());
        }
        if (!StringUtils.isEmpty(inputAnte)) {
            int ante = Integer.parseInt(inputAnte);
            updatedGame.setAnte(ante);
        }
        updatedGame = gameRepository.save(updatedGame);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedGame);

    }

    // todo LOW can be tested work on error and default
    @PutMapping(value = "/games/{id}/state/{state}")
    public ResponseEntity<Game> updateGameState(
            @PathVariable("id") String id,
            @PathVariable("state") QasinoStateMachine.GameState state
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
        if (!StringUtils.isNumeric(id))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();

        int gameId = Integer.parseInt(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Game updateGame = foundGame.get();
        updateGame.setState(state);
        gameRepository.save(updateGame);

        return ResponseEntity.ok().headers(headers).body(updateGame);
    }

    // tested
    @DeleteMapping("/games/{id}")
    public ResponseEntity<Game> deleteGame(
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

        int gameId = Integer.parseInt(id);
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


    // tested
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

    // tested
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

    // todo MEDIUM can be tested
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

    // tested
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

    // tested
    @DeleteMapping("/playingCards/{id}")
    public ResponseEntity<PlayingCard> deletePlayingCard(
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

        int playingCardId = Integer.parseInt(id);
        Optional<PlayingCard> foundPlayingCard = playingCardRepository.findById(playingCardId);
        if (!foundPlayingCard.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        playingCardRepository.deleteById(playingCardId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

}
