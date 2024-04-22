package cloud.qasino.games.controller;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class CardController {

    private GameRepository gameRepository;
    private CardRepository cardRepository;

    @Autowired
    public CardController(
            GameRepository gameRepository,
            CardRepository cardRepository) {

        this.gameRepository = gameRepository;
        this.cardRepository = cardRepository;
    }

    @DeleteMapping("/card/{cardId}/game/{gameId}")
    public ResponseEntity deletePlayingCardByCard(
            @PathVariable("gameId") String id,
            @PathVariable("cardId") String card
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

    @DeleteMapping("/card/{cardId}")
    public ResponseEntity<Card> deletePlayingCardById(
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
            value = "/card/{cardId}/image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] getPlayingCardImageWithMediaType(@PathVariable("cardId") String card) throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("/resources/images/playingcard/svg/diamonds-ten.svg");
        return IOUtils.toByteArray(in);
    }
}