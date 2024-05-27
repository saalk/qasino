package cloud.qasino.games.controller;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class CardsAndPlayerController {

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;

    @Autowired
    public CardsAndPlayerController(
            GameRepository gameRepository,
            CardRepository cardRepository,
            PlayerRepository playerRepository) {

        this.gameRepository = gameRepository;
        this.cardRepository = cardRepository;
        this.playerRepository = playerRepository;
    }

    @DeleteMapping("card/{rankSuit}/game/{gameId}")
    public ResponseEntity deletePlayingCardForGame(
            @RequestHeader("visitorId") String vId,
            @PathVariable("gameId") String id,
            @PathVariable("rankSuit") String rankSuit
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

        long gameId = Long.parseLong(id);
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // todo LOW this is not unique
        List<Card> foundCard = cardRepository.findByRankSuit(rankSuit);
        if (false) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        //cardRepository.deleteById(foundCard.get().getPlayingCardId());
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

    //    @DeleteMapping("/card/{cardId}")
    public ResponseEntity<Card> deletePlayingCardById(
            @RequestHeader("visitorId") String vId,
            @PathVariable("cardId") String id
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

        long playingCardId = Long.parseLong(id);
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

    @GetMapping(
            value = "card/{rankAndSuit}/image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] getPlayingCardImageWithMediaType(
            @PathVariable("rankAndSuit") String card
    ) throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("/resources/images/playingcard/svg/diamonds-ten.svg");
        return IOUtils.toByteArray(in);
    }

    @GetMapping("player/{playerId}")
    public ResponseEntity<Optional<Player>> getPlayer(
            @RequestHeader("visitorId") String vId,
            @PathVariable("playerId") String id
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
        Optional<Player> foundPlayer = playerRepository.findById(Long.parseLong(id));
        if (foundPlayer.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundPlayer);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }
    }

    @PutMapping(value = "player/{playerId}")
    public ResponseEntity<Player> updatePlayer(
            @RequestHeader("visitorId") String vId,
            @PathVariable("playerId") String id,
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

        long playerId = Long.parseLong(id);
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

    @PutMapping(value = "player/{playerId}/{order}")
    public ResponseEntity<Game> updateSequence(
            @RequestHeader("visitorId") String vId,
            @PathVariable("playerId") String id,
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
                || !Arrays.asList(orders).contains(order)) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        long playerId = Long.parseLong(id);
        int orderValue = Integer.parseInt(order);

        // logic get player
        Optional<Player> foundPlayer = playerRepository.findById(playerId);

        if (!foundPlayer.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Optional<Game> updatedGame = gameRepository.findById(foundPlayer.get().getGame().getGameId());
        if (order == "up") {
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

    @DeleteMapping("player/{playerId}")
    public ResponseEntity<Player> deletePlayer(
            @RequestHeader("visitorId") String vId,
            @PathVariable("playerId") String id
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
        long playerId = Long.parseLong(id);

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
}
