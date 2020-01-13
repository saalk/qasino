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
import cloud.qasino.card.statemachine.QasinoStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
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

    // C
    @PostMapping(value = "/games/{type}", params = {"style", "ante"})
    public ResponseEntity<Game> startGame(
            @PathVariable("type") String type,
            @RequestParam(name = "style", defaultValue = "") String style,
            @RequestParam(name = "ante", defaultValue = "20") Integer ante
    ) {
        Game startedGame = gameRepository.save(new Game(Type.valueOf(type), style, (int) ante));

        if (startedGame == null) {
            return ResponseEntity.notFound().build();
        } else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(startedGame.getGameId())
                    .toUri();
            return ResponseEntity.created(uri).body(startedGame);
        }
    }

    // R
    @GetMapping("/games/{id}")
    public ResponseEntity<Optional<Game>> getGame(
            @PathVariable("id") int id
    ) {
        if (!gameRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            Optional<Game> foundGame = gameRepository.findById(id);
            return ResponseEntity.ok(foundGame);
        }

    }

    // U
    @PutMapping(value = "/games/{id}", params = {"style", "ante"})
    public ResponseEntity<Game> updateGame(
            @PathVariable("id") int id,
            @RequestParam(name = "style",  defaultValue = "") String style,
            @RequestParam(name = "ante",  defaultValue = "20") Integer ante
    ) {
        Optional<Game> foundGame = gameRepository.findById(id);

        if (foundGame == null) {
            return ResponseEntity.notFound().build();
        } else {
            Game updateGame = foundGame.get();
            updateGame.setStyle(style);
            updateGame.setAnte((int) ante);
            gameRepository.save(updateGame);

            return ResponseEntity.ok(updateGame);
        }
    }

    // U special - update only the State
    @PutMapping(value = "/games/{id}/State/{state}")
    public ResponseEntity<Game> updateGame(
            @PathVariable("id") int id,
            @RequestParam(name = "state", defaultValue = "INITIALIZED") QasinoStateMachine.GameState state
    ) {
        Optional<Game> foundGame = gameRepository.findById(id);

        if (foundGame == null) {
            return ResponseEntity.notFound().build();
        } else {
            Game updateGame = foundGame.get();
            updateGame.setState(state);
            gameRepository.save(updateGame);

            return ResponseEntity.ok(updateGame);
        }
    }


    // D
    @DeleteMapping("/games/{id}")
    public ResponseEntity<Game> deleteGame(
            @PathVariable("id") int id
    ) {
        gameRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


