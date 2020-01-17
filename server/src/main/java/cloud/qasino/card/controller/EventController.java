package cloud.qasino.card.controller;

import cloud.qasino.card.domain.qasino.Card;
import cloud.qasino.card.entity.Event;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.PlayingCard;
import cloud.qasino.card.entity.enums.Location;
import cloud.qasino.card.repositories.EventRepository;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.PlayingCardRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class EventController {

    // endpoints for the following triggers

    // MAIN
    // SHUFFLE,     	// The Game is shuffled according to the Type
    // PLAY,		 	// PLayers are playing the Game doing actions

    // FINISH
    // ENDED,		 	// The Game is finished and the winner is known

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
    @PostMapping(value = "/game/{id}/shuffle", params = {"jokers"})
    public ResponseEntity shuffleGame(
            @PathVariable("id") String id,
            @RequestParam("jokers") String jokers){

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id,jokers)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id) || !StringUtils.isNumeric(jokers))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();

        int gameId = Integer.parseInt(id);
        int jokersToAdd = Integer.parseInt(jokers);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        List<Card> cards = new ArrayList<>();
        cards = Card.newDeck(jokersToAdd);
        int sequence = 0;
        for (Card card : cards) {
            Event event = new Event(foundGame.get(), null, card.getCardId());
            event.setRoundNumber(0);
            event.setMoveNumber(0);
            event.setBet(0);
            eventRepository.save(event);
            // todo go with event to the game engine
            // todo for now create playing cards
// todo add card !!!
            sequence++;
            PlayingCard playingCard = new PlayingCard(null,foundGame.get(), null, sequence, Location.PILE);
            playingCardRepository.save(playingCard);
        }
        List<Event> events = eventRepository.findByGameId(foundGame.get().getGameId());
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(events);

    }

    //PLAY,		 	// PLayers are playing the Game
    @PostMapping(value = "/game/{gId}/player/{pId}/location/{location}", params = {"cardId",
            "roundNumber","moveNumber","bet"})
    public ResponseEntity startGame(
            @PathVariable("gId") String gId,
            @PathVariable("pId") String pId,
            @PathVariable("location") String inputLocation,

            @RequestParam("cardId") String cardId,
            @RequestParam("roundNumber") String inputRoundNumber,
            @RequestParam("moveNumber") String inputMoveNumber,
            @RequestParam("bet") String inputBet){

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(gId,pId,inputLocation,cardId, inputRoundNumber, inputMoveNumber, inputBet)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(gId)
                || !StringUtils.isNumeric(pId)
                || (Location.fromLabelWithDefault(inputLocation) == Location.ERROR)
                || !StringUtils.isNumeric(inputRoundNumber)
                || !StringUtils.isNumeric(inputMoveNumber)
                || !StringUtils.isNumeric(inputBet)
                )
            // 400
            return ResponseEntity.badRequest().headers(headers).build();

        int gameId = Integer.parseInt(gId);
        int playerId = Integer.parseInt(pId);
        int roundNumber = Integer.parseInt(inputRoundNumber);
        int moveNumber = Integer.parseInt(inputMoveNumber);
        int bet = Integer.parseInt(inputBet);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundGame.isPresent() || !foundPlayer.isPresent() || !Card.isValidCard(cardId)) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic

        Event event = new Event(foundGame.get(), foundPlayer.get(), cardId);
        event.setRoundNumber(roundNumber);
        event.setMoveNumber(moveNumber);
        event.setLocation(Location.fromLabelWithDefault(inputLocation));
        event.setBet(bet);
        // todo go with event to the game engine

        eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(event);

    }


    // additional CRUD methods
    // - winning a game

}

