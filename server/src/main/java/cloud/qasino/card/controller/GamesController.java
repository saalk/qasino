package cloud.qasino.card.controller;

import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.enums.Type;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class GamesController {

    GameRepository gameRepository;
    PlayerRepository playerRepository;

    @Autowired
    public GamesController(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    // special CRUD - endpoints for the following triggers

    // PRE

    //SELECT_GAME,	// User has selected a Type of Game with default style
    //ADD_DETAILS,	// Player chooses the Style of the Game (optional trigger)
    //ADD_BOTS,		// One or more bots are added to the Game


    // FINISH

    // ABANDON      	// The main player has quit the game before finishing


    // normal CRUD

    @PostMapping(value = "/games/{type}", params = {"style", "ante"})
    public String startGame(
            @PathVariable("type") String type,
            @RequestParam("style") String style,
            @RequestParam("ante") int ante,
            BindingResult result,
            Model model) {

        Game game = new Game(Type.valueOf(type));
        Style localStyle = new Style(style);
        game.setStyle(localStyle.getStyle());
        game.setAnte(ante);
        gameRepository.save(game);

        model.addAttribute("games", gameRepository.findAll());

        if (result.hasErrors()) {
            return "index";
        }

        return "games";
    }

    @GetMapping("/games/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + id));

        model.addAttribute("games", game);
        return "games";
    }

    @PostMapping(value = "/games/{id}", params = {"style", "ante"})
    public String updateGame(
            @PathVariable("id") int id,
            @RequestParam("style") String style,
            @RequestParam("ante") int ante,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "games";
        }

        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + id));

        game.setStyle(style);
        game.setAnte(ante);

        gameRepository.save(game);
        model.addAttribute("games", gameRepository.findAll());
        return "index";
    }

    @DeleteMapping("/games/{id}")
    public String deleteGame(@PathVariable("id") int id, Model model) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + id));
        gameRepository.delete(game);
        model.addAttribute("games", gameRepository.findAll());
        return "index";
    }
}


