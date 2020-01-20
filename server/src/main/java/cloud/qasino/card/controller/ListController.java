package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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

@Slf4j
@RestController
public class ListController {

    private UserRepository userRepository;
    private LeagueRepository leagueRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private PlayingCardRepository playingCardRepository;
    private EventRepository eventRepository;
    private ResultsRepository resultsRepository;

    @Autowired
    public ListController(
            UserRepository userRepository,
            LeagueRepository leagueRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            PlayingCardRepository playingCardRepository,
            EventRepository eventRepository,
            ResultsRepository resultsRepository
    ) {

        this.userRepository = userRepository;
        this.leagueRepository = leagueRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.playingCardRepository = playingCardRepository;
        this.eventRepository = eventRepository;
        this.resultsRepository = resultsRepository;
    }

    // ListController - special POST and GET only for USER

    // tested
    @GetMapping(value = "/users/all")
    public ResponseEntity listUsersWithPaging(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "4") String max
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(page, max)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(page) || !StringUtils.isNumeric(max))
            return ResponseEntity.badRequest().headers(headers).build();
        int maximum = Integer.parseInt(max);
        int pages = Integer.parseInt(page);

        // logic
        Pageable pageable = PageRequest.of(pages, maximum, Sort.by(
                Order.asc("ALIAS"),
                Order.desc("ALIAS_SEQ")));

        ArrayList users = (ArrayList) userRepository.findAllUsersWithPage(pageable);

        return ResponseEntity.ok()
                .headers(headers)
                .body(users);
    }

    // todo HIGH test this
    @GetMapping(value = "/users/{id}/games/playing")
    public ResponseEntity listActiveGamesForUser(
            @PathVariable("id") String id,
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "4") String max
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(page, max)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(page)
                || !StringUtils.isNumeric(max)
                || !StringUtils.isNumeric(id)
    )
            return ResponseEntity.badRequest().headers(headers).build();
        int maximum = Integer.parseInt(max);
        int pages = Integer.parseInt(page);
        int userId = Integer.parseInt(id);


        // logic
        Pageable pageable = PageRequest.of(pages, maximum, Sort.by(
                Order.asc("TYPE"),
                Order.desc("CREATED")));

        List<Game> foundGames = gameRepository.findGamesByUserIdByStateWithPage(userId,
                "PLAYING", pageable);
        if (!(foundGames.size() > 0)) {
            return ResponseEntity.notFound().headers(headers).build();
        }
        return ResponseEntity.ok().headers(headers).body(foundGames);

    }

    // todo HIGH test this
    @GetMapping(value = "/games/{id}/players/all")
    public ResponseEntity getPlayersByGame(
            @PathVariable("id") String id) {

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
        int gameId = Integer.parseInt(id);

        // logic
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            return ResponseEntity.notFound().headers(headers).build();
        }
        List<Player> players = playerRepository.findByGameOrderBySequenceAsc(foundGame.get());
        return ResponseEntity.ok().headers(headers).body(players);
    }

}
