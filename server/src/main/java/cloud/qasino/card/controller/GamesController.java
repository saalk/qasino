package cloud.qasino.card.controller;

import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.PlayingCard;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.entity.enums.Avatar;
import cloud.qasino.card.entity.enums.Type;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.UserRepository;
import cloud.qasino.card.statemachine.QasinoStateMachine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
public class GamesController {

    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private UserRepository userRepository;

    @Autowired
    public GamesController(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            UserRepository userRepository) {

        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    // special CRUD - endpoints for the following triggers


    // normal CRUD

    // C- tested ok
    @PostMapping(value = "/games/{type}")
    public ResponseEntity<Game> startGame(
            @PathVariable("type") String type,
            @RequestParam(name = "style", defaultValue = " ") String style,
            @RequestParam(name = "ante", defaultValue = "20") String inputAnte
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(type, style, inputAnte)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(inputAnte)
                || Type.fromLabelWithDefault(type) == Type.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int ante = Integer.parseInt(inputAnte);

        Game startedGame = gameRepository.save(new Game(Type.fromLabelWithDefault(type), style, ante));

        if (startedGame.getGameId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(startedGame);
        }
    }

    // R - tested ok
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

    // U - tested ok, no update of type here // todo test ante changes or empty
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

    // U special - update only the State - can be tested todo work on error and default ?
    @PutMapping(value = "/games/{id}/state/{state}")
    public ResponseEntity<Game> updateGame(
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

    // D - tested ok not possible when players are related
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
}


