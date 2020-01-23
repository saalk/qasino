package cloud.qasino.card.resource;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ListResource {

    private UserRepository userRepository;
    private LeagueRepository leagueRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private PlayingCardRepository playingCardRepository;
    private EventRepository eventRepository;
    private ResultsRepository resultsRepository;

    @Autowired
    public ListResource(
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

    // ListResource - special POST and GET only for USER

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

    // tested
    @GetMapping(value = "/games/{state}/users/{id}")
    public ResponseEntity listActiveGamesForUser(
            @PathVariable("id") String id,
            @PathVariable("state") String state,
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "4") String max
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(id, state, page, max)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        String[] states = new String[]{"new", "started", "finished", "error"};
        if (!StringUtils.isNumeric(page)
                || !StringUtils.isNumeric(max)
                || !StringUtils.isNumeric(id)
                || !Arrays.asList(states).contains(state)
        ) {
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int maximum = Integer.parseInt(max);
        int pages = Integer.parseInt(page);
        int userId = Integer.parseInt(id);

        // logic
        Pageable pageable = PageRequest.of(pages, maximum, Sort.by(
                Order.asc("TYPE"),
                Order.desc("CREATED")));
        List<Game> foundGames;
        switch (state) {
            case ("new"):
            default:
                foundGames = gameRepository.findAllNewGamesForUserWithPage(userId, pageable);
                break;
            // todo LOW finish the other states
/*            case("started"):
                foundGames = gameRepository.findAllStartedGamesForUserWithPage(userId, pageable);
                break;
            case("finished"):
                foundGames = gameRepository.findAllFinishedGamesForUserWithPage(userId, pageable);
                break;
            default:
                foundGames = gameRepository.findAllErrorGamesForUserWithPage(userId, pageable);
                break;*/
        }
        if (!(foundGames.size() > 0)) {
            return ResponseEntity.notFound().headers(headers).build();
        }
        return ResponseEntity.ok().headers(headers).body(foundGames);

    }

    // tested - get only the list of players for a game
    @GetMapping(value = "/players/all/games/{id}")
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

    // todo LOW test
    @GetMapping(value = "/playingcards/all/games/{id}")
    public ResponseEntity getPlayingCardByGame(
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
        }
        linkedGame = foundGame.get();

        // validations
        if (!StringUtils.isNumeric(page) || !StringUtils.isNumeric(max))
            return ResponseEntity.badRequest().headers(headers).build();
        int maximum = Integer.parseInt(max);
        int pages = Integer.parseInt(page);

        // logic
        Pageable pageable = PageRequest.of(pages, maximum, Sort.by(
                Order.asc("LOCATION"),
                Order.asc("SEQUENCE")));

        ArrayList playingCards =
                (ArrayList) playingCardRepository.findAllPlayingCardsByGameWithPage(linkedGame.getGameId(),
                        pageable);

        return ResponseEntity.ok()
                .headers(headers)
                .body(playingCards);
    }

    // todo HIGH test
    @GetMapping(value = "/events/all/games/{id}")
    public ResponseEntity getEventsByGame(
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
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        }
        int gameId = Integer.parseInt(id);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        ArrayList playingCards = (ArrayList) eventRepository.findByGameId(gameId);

        return ResponseEntity.ok()
                .headers(headers)
                .body(playingCards);
    }


}
