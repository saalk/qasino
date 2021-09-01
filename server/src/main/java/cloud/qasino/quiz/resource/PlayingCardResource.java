package cloud.qasino.quiz.resource;

import cloud.qasino.quiz.entity.Quiz;
import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

// basic path /qasino
// basic header @RequestHeader(value "user", required = true) int userId" // else 400
//
// 200 - ok
// 201 - created
// 400 - bad request - error/reason "url ... not available"
// 404 - not found - error/message "invalid value x for y" + reason [missing]
// 412 - precondition failed = error/message - "violation of rule z"
// 500 - internal server error

@Slf4j
@RestController
public class PlayingQuizResource {

    private GameRepository gameRepository;
    private QuizRepository quizRepository;

    @Autowired
    public PlayingQuizResource(
            UserRepository userRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            TurnRepository turnRepository,
            QuizRepository quizRepository) {

        this.gameRepository = gameRepository;
        this.quizRepository = quizRepository;
    }

    // R image - can be tested
    @GetMapping(
            value = "/playingquizs/{playingquiz}/image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    // todo get image by quizid
    public @ResponseBody
    byte[] getPlayingQuizImageWithMediaType(@PathVariable("playingquiz") String quiz) throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("/resources/images/playingquiz/svg/diamonds-ten.svg");
        return IOUtils.toByteArray(in);
    }

    // C deck for game - can be tested
    @PostMapping(value = "/quizzes/games/{id}/jokers/{jokers}")
    public ResponseEntity setupGame(
            @PathVariable("id") String id,
            @PathVariable("jokers") String jokers
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, jokers)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(jokers) || !StringUtils.isNumeric(id)) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int gameId = Integer.parseInt(id);
        int jokersCount = Integer.parseInt(jokers);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        Game linkedGame;
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        } else {
        }
        linkedGame = foundGame.get();

        // logic
        List<Quiz> createdQuizs = linkedGame.getQuizs();

        for (Quiz createdQuiz : createdQuizs) {
            quizRepository.save(createdQuiz);
        }
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(createdQuizs);
    }

    // normal CRUD


}