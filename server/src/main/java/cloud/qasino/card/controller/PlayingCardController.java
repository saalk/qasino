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
            value = "/playingcards/{id}/image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] getPlayingCardImageWithMediaType() throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("/resources/images/playingcard/svg/diamonds-ten.svg");
        return IOUtils.toByteArray(in);
    }

    // C deck for game - can be tested
    @PostMapping(value = "/playingCards/games/{id}/jokers/{jokers}")
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
        Game linkedGame;
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        } else {
        }
        linkedGame = foundGame.get();

        // logic
        List<PlayingCard> createdPlayingCards = linkedGame.getPlayingCards();

        for (PlayingCard createdPlayingCard : createdPlayingCards) {
            playingCardRepository.save(createdPlayingCard);
        }
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(createdPlayingCards);
    }

    // normal CRUD

    // LP - can be tested
    @GetMapping(value = "/playingcards/games/{id}")
    public ResponseEntity getAllPlayingCard(
            @PathVariable("id") String id,
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "4") String max
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
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int gameId = Integer.parseInt(id);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        Game linkedGame;
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        } else {
        }
        linkedGame = foundGame.get();

        // validations
        if (!StringUtils.isNumeric(page) || !StringUtils.isNumeric(max))
            return ResponseEntity.badRequest().headers(headers).build();
        int maximum = Integer.parseInt(max);
        int pages = Integer.parseInt(page);

        // logic
        Pageable pageable = PageRequest.of(pages, maximum, Sort.by(
                Order.asc("ALIAS"),
                Order.desc("ALIAS_SEQ")));

        ArrayList playingCards = (ArrayList) playingCardRepository.findAllPlayingCardsByGameWithPage(pageable);

        return ResponseEntity.ok()
                .headers(headers)
                .body(playingCards);
    }

    // R - can be tested
    @GetMapping(value = "/playingcards/{id}")
    public ResponseEntity<Optional<PlayingCard>> getPlayingCard(
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
        Optional<PlayingCard> foundPlayingCard = playingCardRepository.findById(Integer.parseInt(id));
        if (foundPlayingCard.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundPlayingCard);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }

    }

    // D - can be tested
    @DeleteMapping("/playingCards/{id}")
    public ResponseEntity<PlayingCard> deletePlayingCard(
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
        Optional<PlayingCard> foundPlayingCard = playingCardRepository.findById(playingCardId);
        if (!foundPlayingCard.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        playingCardRepository.deleteById(playingCardId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }
}