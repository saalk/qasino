package cloud.qasino.card.resource;

import cloud.qasino.card.statemachine.GameState;
import cloud.qasino.card.database.entity.Turn;
import cloud.qasino.card.database.entity.Game;
import cloud.qasino.card.database.entity.Player;
import cloud.qasino.card.database.entity.enums.move.Move;
import cloud.qasino.card.database.entity.enums.card.Location;
import cloud.qasino.card.database.repository.CardRepository;
import cloud.qasino.card.database.repository.TurnRepository;
import cloud.qasino.card.database.repository.GameRepository;
import cloud.qasino.card.database.repository.PlayerRepository;
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
public class TurnResource {

    // TurnResource - special POST for GAME, PLAYINGCARD, EVENT and RESULT has state machine
    // /api/move/game/{id}/DEAL first -> POST add jokers and update state // PLAYING
    // /api/move/game/{id}/HIGER|LOWER|PASS/power -> only for user self // PLAYING, FINSHED
    // /api/move/game/{id}/NEXT/player/{id} -> only for bot // PLAYING, FINISHED

    GameRepository gameRepository;
    PlayerRepository playerRepository;
    CardRepository cardRepository;
    TurnRepository turnRepository;

    @Autowired
    public TurnResource(
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository
    ) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.turnRepository = turnRepository;
    }

    @PostMapping(value = "move/{suppliedMove}/games/{gId}/players/{pId}/location/{location}")
    public ResponseEntity startGame(
            @PathVariable("suppliedMove") String inputAction,
            @PathVariable("gId") String gId,
            @PathVariable("pId") String pId,
            @PathVariable("location") String inputLocation,

            @RequestParam(value = "cardId", required = false) String cardId,
            @RequestParam(value = "roundNumber", required = false) String inputRoundNumber,
            @RequestParam(value = "moveNumber", required = false) String inputMoveNumber,
            @RequestParam(value = "bet", required = false) String inputBet){

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(inputAction, gId,pId,inputLocation,cardId, inputRoundNumber,
                        inputMoveNumber, inputBet)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(gId)
                || !StringUtils.isNumeric(pId)
                || (Location.fromLabelWithDefault(inputLocation) == Location.ERROR)
                || (Move.fromLabelWithDefault(inputAction) == Move.ERROR)
        //        || !StringUtils.isNumeric(inputRoundNumber)
        //        || !StringUtils.isNumeric(inputMoveNumber)
        //        || !StringUtils.isNumeric(inputBet)
                )
            // 400
            return ResponseEntity.badRequest().headers(headers).build();

        int gameId = Integer.parseInt(gId);
        int playerId = Integer.parseInt(pId);
        Move move = Move.fromLabelWithDefault(inputAction);
        Location location = Location.fromLabelWithDefault(inputLocation);
        //int roundNumber = Integer.parseInt(inputRoundNumber);
        //int moveNumber = Integer.parseInt(inputMoveNumber);
        //int bet = Integer.parseInt(inputBet);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundGame.isPresent() || !foundPlayer.isPresent()
        //        || !PlayingCard.isValidCardId(cardId)
        ) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // rules
        if (!   (foundGame.get().getState() == GameState.PLAYING )  ) {
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();
        }

        // logic

        Turn turn = new Turn(foundGame.get(), foundPlayer.get().getPlayerId());
        //move.setRoundNumber(roundNumber);
        //move.setMoveNumber(moveNumber);
        // turn.setLocation(Location.fromLabelWithDefault(inputLocation));
        //move.setBet(bet);
        // todo go with move to the game engine

        turnRepository.save(turn);
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(turn);

    }
}

