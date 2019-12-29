package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.entity.enums.Type;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GamesController {

    // endpoints for the following triggers

    // PRE
    //SELECT_GAME,	// User has selected a Type of Game with default style
    //ADD_DETAILS,	// Player chooses the Style of the Game (optional trigger)
    //ADD_BOTS,		// One or more bots are added to the Game

    // FINISH
    // ABANDON      	// The main player has quit the game before finishing

    GameRepository gameRepository;
    PlayerRepository playerRepository;

    @Autowired
    public GamesController(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @PostMapping(value="/playgame/{type}", params={"style","ante"})
    public String startGame(
            @PathVariable("type") String gameType,
            @RequestParam("style") String style,
            @RequestParam("ante") int ante,
            BindingResult result,
            Model model) {

        Game game = new Game(Type.valueOf(gameType));
        Style localStyle = new Style(style);

        game.setStyle(localStyle.getStyle());
        game.setAnte(ante);

        gameRepository.save(game);
        model.addAttribute("games", gameRepository.findAll());

        if (result.hasErrors()) {
            return "play-player";
        }

        return "add-player";
    }


    // additional CRUD methods
    // - winning a game

}

