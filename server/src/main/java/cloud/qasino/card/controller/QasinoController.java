package cloud.qasino.card.controller;

import cloud.qasino.card.dto.Enums;
import cloud.qasino.card.dto.Totals;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
public class QasinoController {

    // QasinoController - special GET & POST and PUT on USER
    // todo HIGH make endpoint
    // /api/halloffame
    // - statistics
    //   - games per status counters for all + per period
    //   - users per game played
    // - halloffame top10 users per type and period
    //
    // /api/enums
    // - enums per entity
    // - classifications per game type
    //
    // todo HIGH make endpoint
    // /api/leagues
    // - active leagues and games
    //
    // /api/logon?alias -> GET
    // /api/signup?alias -> POST
    // /api/pawnship -> PUT
    // /api/repayloan -> PUT

    GameRepository gameRepository;
    UserRepository userRepository;
    PlayerRepository playerRepository;
    PlayingCardRepository playingCardRepository;
    EventRepository eventRepository;
    LeagueRepository leagueRepository;

    @Autowired
    public QasinoController(
            UserRepository userRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            PlayingCardRepository playingCardRepository,
            LeagueRepository leagueRepository,
            EventRepository eventRepository
    ) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.playingCardRepository = playingCardRepository;
        this.leagueRepository = leagueRepository;
        this.eventRepository = eventRepository;
    }

    // TODO LOW develop further
    @GetMapping(value = "/halloffame")
    public ResponseEntity halloffame(
    ) {
        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        Totals totals = new Totals();
        totals.setLeagues((int) leagueRepository.count());
        totals.setUsers((int) userRepository.count());
        totals.setGames((int) gameRepository.count());
        totals.setPlayers((int) playerRepository.count());
        totals.setPlayingCards((int) playingCardRepository.count());

        return ResponseEntity.ok().headers(headers).body(totals);
    }

    // tested
    @GetMapping(value = "/logon/{alias}")
    public ResponseEntity logon(
            @PathVariable("alias") String alias
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(alias)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        User foundUser = userRepository.findUserByAliasAndAliasSequence(alias, 1);
        if (foundUser.getUserId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(foundUser);
    }

    // tested
    @GetMapping(value = "/enums")
    public ResponseEntity enums(
    ) {
        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        Enums enums = new Enums();
        return ResponseEntity.ok().headers(headers).body(enums);
    }

    // tested
    @PostMapping(value = "/signup/{alias}")
    public ResponseEntity signup(
            @PathVariable("alias") String alias,
            @RequestParam(name = "email", defaultValue = "") String email
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(email)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        int sequence = (int) (userRepository.countByAlias(alias) + 1);

        // rules
        if (sequence > 1)
            // todo LOW split alias and number
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).build();

        User createdUser = userRepository.save(new User(alias, sequence, email));
        if (createdUser.getUserId() == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(createdUser);
    }

}

