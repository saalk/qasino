package cloud.qasino.games.resource;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.repository.*;
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
// basic header @RequestHeader(value "visitor", required = true) int visitorId" // else 400
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

    private VisitorRepository visitorRepository;
    private LeagueRepository leagueRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;
    private ResultsRepository resultsRepository;

    @Autowired
    public ListResource(
            VisitorRepository visitorRepository,
            LeagueRepository leagueRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository,
            ResultsRepository resultsRepository
    ) {

        this.visitorRepository = visitorRepository;
        this.leagueRepository = leagueRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.turnRepository = turnRepository;
        this.resultsRepository = resultsRepository;
    }

    // ListResource - special POST and GET only for VISITOR

    // tested
    @GetMapping(value = "/visitors/all")
    public ResponseEntity listVisitorsWithPaging(
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
                Order.asc("VisitorName"),
                Order.desc("VisitorName_SEQ")));

        ArrayList visitors = (ArrayList) visitorRepository.findAllVisitorsWithPage(pageable);

        return ResponseEntity.ok()
                .headers(headers)
                .body(visitors);
    }

    // tested
    @GetMapping(value = "/games/{state}/visitors/{id}")
    public ResponseEntity listActiveGamesForVisitor(
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
        long visitorId = Long.parseLong(id);

        // logic
        Pageable pageable = PageRequest.of(pages, maximum, Sort.by(
                Order.asc("TYPE"),
                Order.desc("CREATED")));
        List<Game> foundGames;
        switch (state) {
            case ("new"):
            default:
                foundGames = gameRepository.findAllNewGamesForVisitorWithPage(visitorId, pageable);
                break;
            // todo LOW finish the other states
/*            case("started"):
                foundGames = gameRepository.findAllStartedGamesForVisitorWithPage(visitorId, pageable);
                break;
            case("finished"):
                foundGames = gameRepository.findAllFinishedGamesForVisitorWithPage(visitorId, pageable);
                break;
            default:
                foundGames = gameRepository.findAllErrorGamesForVisitorWithPage(visitorId, pageable);
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
        long gameId = Long.parseLong(id);

        // logic
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            return ResponseEntity.notFound().headers(headers).build();
        }
        List<Player> players = playerRepository.findByGameOrderBySeatAsc(foundGame.get());
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
        long gameId = Long.parseLong(id);

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
                (ArrayList) cardRepository.findAllCardsByGameWithPage(linkedGame.getGameId(),
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
        long gameId = Long.parseLong(id);

        Optional<Game> foundGame = gameRepository.findById(gameId);
        if (!foundGame.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        ArrayList playingCards = (ArrayList) turnRepository.findByGame(foundGame.get());

        return ResponseEntity.ok()
                .headers(headers)
                .body(playingCards);
    }


}
