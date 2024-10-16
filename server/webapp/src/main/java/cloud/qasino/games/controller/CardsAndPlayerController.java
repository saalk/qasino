package cloud.qasino.games.controller;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestController
public class CardsAndPlayerController {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardsAndPlayerController(
            GameRepository gameRepository,
            CardRepository cardRepository,
            PlayerRepository playerRepository) {

        this.gameRepository = gameRepository;
        this.cardRepository = cardRepository;
        this.playerRepository = playerRepository;
    }

    @GetMapping(value = "card/{rankAndSuit}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody
    byte[] getPlayingCardImageWithMediaType(@PathVariable("rankAndSuit") String card) throws IOException {
        InputStream in = getClass().getResourceAsStream("/resources/images/playingcard/svg/diamonds-ten.svg");
        return IOUtils.toByteArray(in);
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

        if (foundPlayer.isEmpty()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Optional<Game> updatedGame = gameRepository.findById(foundPlayer.get().getGame().getGameId());
        if (order.equals("up")) {
            // todo LOW ordering does not work
            updatedGame.get().switchPlayers(-1, -1);
        } else {
            updatedGame.get().switchPlayers(1, 1);
        }
        gameRepository.save(updatedGame.get());
        return ResponseEntity.ok(updatedGame.get());
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
    }

}
