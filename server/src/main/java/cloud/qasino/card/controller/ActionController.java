package cloud.qasino.card.controller;

import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.enums.Type;
import cloud.qasino.card.repositories.EventRepository;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.PlayingCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActionController {

    // endpoints for the following triggers

    // MAIN
    //SHUFFLE,     	// The Game is shuffled according to the Type
    //PLAY,		 	// PLayers are playing the Game

    // FINISH
    //ENDED,		 	// The Game is finished and the winner	is known

    GameRepository gameRepository;
    PlayerRepository playerRepository;
    PlayingCardRepository playingCardRepository;
    EventRepository eventRepository;

    @Autowired
    public ActionController(
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            PlayingCardRepository playingCardRepository,
            EventRepository eventRepository
    ) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.playingCardRepository = playingCardRepository;
        this.eventRepository = eventRepository;
    }

    @PostMapping(value="/action/{gameid}", params={"style","ante"})
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

