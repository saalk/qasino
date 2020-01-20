package cloud.qasino.card.controller;

import cloud.qasino.card.domain.qasino.Card;
import cloud.qasino.card.entity.Event;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.enums.playingcard.Location;
import cloud.qasino.card.repositories.EventRepository;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.PlayingCardRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

@RestController
public class EventController {

    // EventController - special POST for GAME, PLAYINGCARD, EVENT and RESULT has state machine
    // /api/event/game/{id}/HIGER|LOWER|PASS/power -> only for user self // PLAYING, FINSHED
    // /api/event/game/{id}/NEXT/player/{id} -> only for bot // PLAYING, FINISHED


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

    @PostMapping(value = "/game/{gId}/player/{pId}/location/{location}")
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
        if (!foundGame.isPresent() || !foundPlayer.isPresent() || !Card.isValidCardId(cardId)) {
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

