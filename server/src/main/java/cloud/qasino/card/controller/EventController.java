package cloud.qasino.card.controller;

import cloud.qasino.card.domain.qasino.Card;
import cloud.qasino.card.entity.Event;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
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

import java.util.ArrayList;
import java.util.List;

@RestController
public class EventController {

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
    public EventController(
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

    //SHUFFLE,     	// The Game is shuffled according to the Type
    @PostMapping(value = "/game/{gameid}/shuffle", params = {"jokers"})
    public String shuffleGame(
            @PathVariable("gameId") int gameId,

            @RequestParam("jokers") int jokers,
            BindingResult result,
            Model model) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + gameId));

        List<Card> cards = new ArrayList<>();
        cards = Card.newDeck(jokers);
        for (Card card : cards) {
            Event event = new Event(game, null, card.getCardId());
            event.setRoundNumber(0);
            event.setMoveNumber(0);
            event.setBet(0);
            eventRepository.save(event);
        }

        model.addAttribute("events", eventRepository.findAllForGameId(gameId));
        if (result.hasErrors()) {
            return "events";
        }
        return "events";
    }

    //PLAY,		 	// PLayers are playing the Game
    @PostMapping(value = "/game/{gameid}/player/{playerid}/move/{move}", params = {"roundNumber",
            "moveNumber", "bet"})
    public String startGame(
            @PathVariable("gameId") int gameId,
            @PathVariable("playerId") int playerId,
            @PathVariable("move") String move,

            @RequestParam("cardId") String cardId,
            @RequestParam("roundNumber") int roundNumber,
            @RequestParam("moveNumber") int moveNumber,
            @RequestParam("bet") int bet,
            BindingResult result,
            Model model) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + gameId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid player Id:" + playerId));


        Event event = new Event(game, player, cardId);
        event.setRoundNumber(roundNumber);
        event.setMoveNumber(moveNumber);
        event.setBet(bet);

        eventRepository.save(event);
        model.addAttribute("events", eventRepository.findAllForGameId(gameId));

        if (result.hasErrors()) {
            return "events";
        }

        return "event";
    }


    // additional CRUD methods
    // - winning a game

}

