package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.PlayingCard;
import cloud.qasino.card.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class PlayingCardController {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private EventRepository eventRepository;
    private PlayingCardRepository playingCardRepository;

    @Autowired
    public PlayingCardController(
            UserRepository userRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            EventRepository eventRepository,
            PlayingCardRepository playingCardRepository) {

        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.eventRepository = eventRepository;
        this.playingCardRepository = playingCardRepository;
    }

    // R image - can be tested
    @GetMapping(
            value = "/playingcards/{card}/image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    // todo get image by cardid
    public @ResponseBody
    byte[] getPlayingCardImageWithMediaType(@PathVariable("card") String card) throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("/resources/images/playingcard/svg/diamonds-ten.svg");
        return IOUtils.toByteArray(in);
    }

    // normal CRUD

    // L for specific Game with paging - can be tested
    @GetMapping(value = "/playingcards/games/{id}")
    public ResponseEntity getAllPlayingCard(
            @PathVariable("id") String id,
            // todo why page this ?
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "4") String max
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, page, max)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int gameId = Integer.parseInt(id);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game linkedGame = foundGame.get();

        // validations
        if (!StringUtils.isNumeric(page) || !StringUtils.isNumeric(max))
            return ResponseEntity.badRequest().headers(headers).build();
        int maximum = Integer.parseInt(max);
        int pages = Integer.parseInt(page);

        // logic
        Pageable pageable = PageRequest.of(pages, maximum, Sort.by(
                Order.asc("LOCATION"),
                Order.asc("HAND"),
                Order.asc("SEQUENCE")
        ));

        ArrayList playingCards = (ArrayList) playingCardRepository.findAllPlayingCardsByGameWithPage(pageable);

        return ResponseEntity.ok()
                .headers(headers)
                .body(playingCards);
    }

    // U add deck to game - can be tested
    @PutMapping(value = "/playingcards/games/{id}/jokers/{jokers}")
    public ResponseEntity setupGame(
            @PathVariable("id") String id,
            @PathVariable("jokers") String jokers
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, jokers)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(jokers) || !StringUtils.isNumeric(id)) {
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int gameId = Integer.parseInt(id);
        int jokersCount = Integer.parseInt(jokers);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        Game updatedGame = foundGame.get();

        // logic
        updatedGame.addDeck(jokersCount);

        for (PlayingCard createdPlayingCard : updatedGame.getPlayingCards()) {
            playingCardRepository.save(createdPlayingCard);
        }
        return ResponseEntity.ok().headers(headers).body(updatedGame);

    }

    // R - can be tested
    @GetMapping(value = "/playingcards/{card}")
    public ResponseEntity<Optional<PlayingCard>> getPlayingCard(
            @PathVariable("card") String card
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
        Optional<PlayingCard> foundPlayingCard = playingCardRepository.findByCard(card);
        if (foundPlayingCard.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundPlayingCard);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }

    }

    // D - can be tested
    @DeleteMapping("/playingcards/{card}")
    public ResponseEntity<PlayingCard> deletePlayingCard(
            @PathVariable("card") String card
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
/*        if (!StringUtils.isNumeric(id))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();*/
        //int playingCardId = Integer.parseInt(id);

        Optional<PlayingCard> foundPlayingCard = playingCardRepository.findByCard(card);
        if (!foundPlayingCard.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        playingCardRepository.deleteById(foundPlayingCard.get().getPlayingCardId());
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }
}