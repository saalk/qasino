package cloud.qasino.quiz.resource;

import cloud.qasino.quiz.statemachine.GameState;
import cloud.qasino.quiz.entity.Quiz;
import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.entity.Player;
import cloud.qasino.quiz.entity.User;
import cloud.qasino.quiz.entity.enums.player.AiLevel;
import cloud.qasino.quiz.entity.enums.player.Avatar;
import cloud.qasino.quiz.repository.GameRepository;
import cloud.qasino.quiz.repository.PlayerRepository;
import cloud.qasino.quiz.repository.QuizRepository;
import cloud.qasino.quiz.repository.UserRepository;
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
public class CRUDResource {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private QuizRepository quizRepository;

    @Autowired
    public CRUDResource(
            UserRepository userRepository,
            GameRepository gameRepository,
            QuizRepository quizRepository,
            PlayerRepository playerRepository) {

        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.quizRepository = quizRepository;
        this.playerRepository = playerRepository;
    }

    // CREATE, GET, PUT, DELETE for single entities
    // /api/users/{id} - GET, DELETE, PUT userName, email only
    // /api/games/{id} - GET, DELETE, PUT type, style, ante - rules apply!
    // /api/players/{id} - GET, DELETE, PUT sequence, PUT avatar, ailevel - rules apply

    // todo LOW - make endpoints
    // /api/playingquizs/{id} - GET, DELETE only - rules apply
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
            @RequestParam(name = "userName", defaultValue = "") String userName,
            @RequestParam(name = "email", defaultValue = "") String email
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(userName, email)
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
        if (!StringUtils.isEmpty(userName)) {
            int sequence = (int) (userRepository.countByUserName(userName) + 1);
            updatedUser.setUserName(userName);
            updatedUser.setUserNameSequence(sequence);
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

    // todo LOW work on all sqls, works for new
    @PutMapping(value = "/games/{id}/state/{state}")
    public ResponseEntity<Game> updateGameState(
            @PathVariable("id") String id,
            @PathVariable("state") String state
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
        if (!StringUtils.isNumeric(id)
        || (GameState.fromLabelWithDefault(state) == GameState.ERROR)) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();}

        int gameId = Integer.parseInt(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Game updateGame = foundGame.get();
        updateGame.setState(GameState.fromLabelWithDefault(state));
        gameRepository.save(updateGame);

        return ResponseEntity.ok().headers(headers).body(updateGame);
    }

    // tested todo LOW work on constraint of players delete first
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

    // todo LOW does not work
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
        String[] orders = new String[]{"up", "down"};
        if (!StringUtils.isNumeric(id)
                || !StringUtils.isNumeric(id)
                || !Arrays.asList(orders).contains(order))
        {
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

    // R - can be tested
    @GetMapping(value = "/playingquizs/playingquiz/{playingquiz}")
    public ResponseEntity getPlayingQuizByQuiz(
            @PathVariable("playingquiz") String quiz
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations

        // logic
        List<Quiz> foundQuizs = quizRepository.findByQuiz(quiz);
        return ResponseEntity.ok().headers(headers).body(foundQuizs);

    }

    // D - can be tested
    @DeleteMapping("/playingquizs/games/{id}/playingquiz/{playingquiz}")
    public ResponseEntity deletePlayingQuizByQuiz(
            @PathVariable("id") String id,
            @PathVariable("playingquiz") String quiz
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

        // todo LOW this is not unique
        List<Quiz> foundQuiz = quizRepository.findByQuiz(quiz);
        if (false) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        //quizRepository.deleteById(foundQuiz.get().getPlayingQuizId());
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

    // todo test
    @DeleteMapping("/playingquizs/{id}")
    public ResponseEntity<Quiz> deletePlayingQuizById(
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

        int playingQuizId = Integer.parseInt(id);
        Optional<Quiz> foundPlayingQuiz = quizRepository.findById(playingQuizId);
        if (!foundPlayingQuiz.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        quizRepository.deleteById(playingQuizId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

}
