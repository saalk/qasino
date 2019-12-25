package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.GameVariant;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.GameType;
import cloud.qasino.card.event.CustomSpringEvent;
import cloud.qasino.card.repository.GameRepository;
import cloud.qasino.card.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class GamesController {

    GameRepository gameRepository;

    UserRepository userRepository;

    @PostMapping(value="/playgame/{type}", params={"variant","ante"})
    public String startGame(
            @PathVariable("type") String gameType,
            @RequestParam("variant") String variant,
            @RequestParam("ante") int ante,
            BindingResult result,
            Model model) {

        Game game = new Game(GameType.valueOf(gameType));
        GameVariant gameVariant = new GameVariant(variant);

        game.setGameVariant(gameVariant);
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

