package cloud.qasino.games.resource;

import cloud.qasino.games.statemachine.GameState;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// basic path /qasino
// basic header @RequestHeader(value "visitor", required = true) int visitorId" // else 400
//
// 200 - ok
// 201 - created
// 204 - no content - for deleted
// 400 - bad request - error/reason "url ... not available"
// 404 - not found - error/message "invalid value x for y" + reason [missing]
// 412 - precondition failed = error/message - "violation of rule z"
// 500 - internal server error

@Slf4j
@RestController
public class CRUDResource {

    private VisitorRepository visitorRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;

    @Autowired
    public CRUDResource(
            VisitorRepository visitorRepository,
            GameRepository gameRepository,
            CardRepository cardRepository,
            PlayerRepository playerRepository) {

        this.visitorRepository = visitorRepository;
        this.gameRepository = gameRepository;
        this.cardRepository = cardRepository;
        this.playerRepository = playerRepository;
    }

    // CREATE, GET, PUT, DELETE for single entities
    // /api/visitors/{id} - GET, DELETE, PUT visitorName, email only
    // /api/games/{id} - GET, DELETE, PUT type, style, ante - rules apply!
    // /api/players/{id} - GET, DELETE, PUT sequence, PUT avatar, ailevel - rules apply

    // todo LOW - make endpoints
    // /api/playingcards/{id} - GET, DELETE only - rules apply
    // /api/leagues/{id} - GET, DELETE, PUT name enddate or close direct - rules apply
    // /api/result/{id} - GET, DELETE, PUT name - rules apply

    // tested
    @GetMapping("/visitors/{id}")
    public ResponseEntity<Optional<Visitor>> getVisitor(
            @PathVariable String id
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
        Optional<Visitor> foundVisitor = visitorRepository.findById(Integer.parseInt(id));
        if (foundVisitor.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundVisitor);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }

    }

    // tested
    @PutMapping(value = "/visitors/{id}")
    public ResponseEntity<Visitor> updateVisitor(
            @PathVariable("id") String id,
            @RequestParam(name = "visitorName", defaultValue = "") String visitorName,
            @RequestParam(name = "email", defaultValue = "") String email
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(visitorName, email)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        int visitorId = Integer.parseInt(id);
        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        if (!foundVisitor.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Visitor updatedVisitor = foundVisitor.get();
        if (!StringUtils.isEmpty(visitorName)) {
            int sequence = (int) (visitorRepository.countByVisitorName(visitorName) + 1);
            updatedVisitor.setVisitorName(visitorName);
            updatedVisitor.setVisitorNameSequence(sequence);
        }
        if (!StringUtils.isEmpty(email)) {
            updatedVisitor.setEmail(email);
        }
        updatedVisitor = visitorRepository.save(updatedVisitor);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedVisitor);
    }

    // tested
    @DeleteMapping("/visitors/{id}")
    public ResponseEntity<Visitor> deleteVisitor(
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
        int visitorId = Integer.parseInt(id);
        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        if (!foundVisitor.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        visitorRepository.deleteById(visitorId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

    // tested
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

    // todo LOW work on all sqls, works for new
    @PutMapping(value = "/games/{id}/state/{state}")
    public ResponseEntity<Game> updateGameState(
            @PathVariable("id") String id,
            @PathVariable("state") String state
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
        if (!StringUtils.isNumeric(id)
        || (GameState.fromLabelWithDefault(state) == GameState.ERROR)) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();}

        int gameId = Integer.parseInt(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Game updateGame = foundGame.get();
        updateGame.setState(GameState.fromLabelWithDefault(state));
        gameRepository.save(updateGame);

        return ResponseEntity.ok().headers(headers).body(updateGame);
    }

    // tested todo LOW work on constraint of players delete first
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

    // tested
    @GetMapping("/players/{id}")
    public ResponseEntity<Optional<Player>> getPlayer(
            @PathVariable("id") String id
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(id)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)) {
            return ResponseEntity.badRequest().headers(headers).build();
        }

        // logic
        Optional<Player> foundPlayer = playerRepository.findById(Integer.parseInt(id));
        if (foundPlayer.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundPlayer);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }
    }

    // tested
    @PutMapping(value = "/players/{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable("id") String id,
            @RequestParam(name = "avatar", defaultValue = "") String avatar,
            @RequestParam(name = "aiLevel", defaultValue = "") String aiLevel
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, avatar, aiLevel)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)
                || AiLevel.fromLabelWithDefault(aiLevel) == AiLevel.ERROR
                || Avatar.fromLabelWithDefault(avatar) == Avatar.ERROR) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }

        int playerId = Integer.parseInt(id);
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        Player updatedPlayer = foundPlayer.get();
        if (!StringUtils.isEmpty(avatar)) {
            updatedPlayer.setAvatar(Avatar.fromLabelWithDefault(avatar));
        }
        if (!StringUtils.isEmpty(aiLevel)) {
            updatedPlayer.setAiLevel(AiLevel.fromLabelWithDefault(aiLevel));
        }
        updatedPlayer = playerRepository.save(updatedPlayer);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedPlayer);
    }

    // todo LOW does not work
    @PutMapping(value = "/players/{id}/{order}")
    public ResponseEntity<Game> updateSequence(
            @PathVariable("id") String id,
            @PathVariable("order") String order
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(id, order)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        String[] orders = new String[]{"up", "down"};
        if (!StringUtils.isNumeric(id)
                || !StringUtils.isNumeric(id)
                || !Arrays.asList(orders).contains(order))
        {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int playerId = Integer.parseInt(id);
        int orderValue = Integer.parseInt(order);

        // logic get player
        Optional<Player> foundPlayer = playerRepository.findById(playerId);

        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Optional<Game> updatedGame = gameRepository.findById(foundPlayer.get().getGame().getGameId());
        if (order == "up" ) {
            // todo LOW ordering does not work
            updatedGame.get().switchPlayers(-1, -1);
            gameRepository.save(updatedGame.get());
            return ResponseEntity.ok(updatedGame.get());
        } else {
            updatedGame.get().switchPlayers(1, 1);
            gameRepository.save(updatedGame.get());
            return ResponseEntity.ok(updatedGame.get());
        }
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
    }

    // tested
    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(
            @PathVariable("id") String id
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(id)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        int playerId = Integer.parseInt(id);

        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        playerRepository.deleteById(playerId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();

    }

    // R - can be tested
    @GetMapping(value = "/playingcards/playingcard/{playingcard}")
    public ResponseEntity getPlayingCardByCard(
            @PathVariable("playingcard") String card
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations

        // logic
        List<Card> foundCards = cardRepository.findByCard(card);
        return ResponseEntity.ok().headers(headers).body(foundCards);

    }

    // D - can be tested
    @DeleteMapping("/playingcards/games/{id}/playingcard/{playingcard}")
    public ResponseEntity deletePlayingCardByCard(
            @PathVariable("id") String id,
            @PathVariable("playingcard") String card
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

        // todo LOW this is not unique
        List<Card> foundCard = cardRepository.findByCard(card);
        if (false) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        //cardRepository.deleteById(foundCard.get().getPlayingCardId());
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

    // todo test
    @DeleteMapping("/playingcards/{id}")
    public ResponseEntity<Card> deletePlayingCardById(
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

        int playingCardId = Integer.parseInt(id);
        Optional<Card> foundPlayingCard = cardRepository.findById(playingCardId);
        if (!foundPlayingCard.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        cardRepository.deleteById(playingCardId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

}
