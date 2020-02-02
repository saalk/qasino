package cloud.qasino.card.resource;

import cloud.qasino.card.dto.Enums;
import cloud.qasino.card.dto.Counters;
import cloud.qasino.card.dto.QasinoFlowDTO;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

import static cloud.qasino.card.configuration.Constants.DEFAULT_PAWN_SHIP_HUMAN;
import static cloud.qasino.card.statemachine.GameState.*;

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
public class QasinoResource {

    GameRepository gameRepository;
    UserRepository userRepository;
    PlayerRepository playerRepository;
    CardRepository cardRepository;
    TurnRepository turnRepository;
    LeagueRepository leagueRepository;

    @Autowired
    public QasinoResource(
            UserRepository userRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            LeagueRepository leagueRepository,
            TurnRepository turnRepository
    ) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.leagueRepository = leagueRepository;
        this.turnRepository = turnRepository;
    }

    // tested
    @GetMapping(value = "/enums")
    public ResponseEntity<Enums> enums(
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

    @GetMapping(value = "/logon/{alias}")
    public ResponseEntity logon(
            @RequestHeader Map<String, String> headerData,
            @PathVariable Map<String, String> pathData,
            @RequestParam Map<String, String> paramData
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(pathData, paramData)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));
        System.out.println(pathData);

        // validations
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        System.out.println(flowDTO);
        boolean processOk = flowDTO.processInput(headerData, pathData, paramData,null);
        if (!processOk) {
            headers.add(flowDTO.getErrorKey(),flowDTO.getErrorValue());
            headers.add("message",flowDTO.getErrorMessage());
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(headers).build();
        }

        // logic
        Optional<User> foundUser = userRepository.findUserByAliasAndAliasSequence(flowDTO.getSuppliedAlias(), 1);
        if (!foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        return ResponseEntity.ok().headers(headers).body(foundUser.get());
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

    // tested
    @PutMapping(value = "/users/{id}/pawnship")
    public ResponseEntity<User> pawnship(
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
        int userId = Integer.parseInt(id);
        Optional<User> foundUser = userRepository.findById(userId);
        if (!foundUser.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // logic
        User updatedUser = foundUser.get();
        int pawn = User.pawnShipValue(DEFAULT_PAWN_SHIP_HUMAN);
        if (!updatedUser.pawnShip(pawn)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).body(updatedUser);
        }
        updatedUser = userRepository.save(updatedUser);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedUser);
    }

    // tested
    @PutMapping(value = "/users/{id}/repayloan")
    public ResponseEntity<User> repayloan(
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
        int userId = Integer.parseInt(id);
        Optional<User> foundUser = userRepository.findById(userId);
        if (!foundUser.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }
        User updatedUser = foundUser.get();

        // logic
        if (!updatedUser.repayLoan()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(headers).body(updatedUser);
        }
        updatedUser = userRepository.save(updatedUser);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedUser);
    }

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

        Counters counters = new Counters();
        Counters.Totals totals = new Counters.Totals();

        totals.setLeagues((int) leagueRepository.count());
        totals.setUsers((int) userRepository.count());
        totals.setGames((int) gameRepository.count());
        totals.setPlayers((int) playerRepository.count());
        totals.setCards((int) cardRepository.count());

        Counters.GameSubTotals gameSubTotals = new Counters.GameSubTotals();
        gameSubTotals.setNewGames((int) gameRepository.countByStates(cardGamesNewValues));
        gameSubTotals.setStartedGames((int) gameRepository.countByStates(cardGamesStartedValues));
        gameSubTotals.setFinishedGames((int) gameRepository.countByStates(cardGamesStartedValues));
        gameSubTotals.setErrorGames((int) gameRepository.countByStates(cardGamesStartedValues));
        totals.setSubTotalsGames(gameSubTotals);

        return ResponseEntity.ok().headers(headers).body(totals);
    }


}

