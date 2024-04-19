package cloud.qasino.games.controller;

import cloud.qasino.games.action.CalculateHallOfFameAction;
import cloud.qasino.games.action.FindAllEntitiesForInputAction;
import cloud.qasino.games.action.FindVisitorIdByVisitorNameAction;
import cloud.qasino.games.action.HandleSecuredLoanAction;
import cloud.qasino.games.action.MapQasinoResponseFromRetrievedDataAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.SignUpNewVisitorAction;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.event.EventOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
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

@RestController
//@Api(tags = {WebConfiguration.QASINO_TAG})
public class QasinoController {

    EventOutput.Result output;

    private VisitorRepository visitorRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;
    private ResultsRepository resultsRepository;
    @Autowired
    FindAllEntitiesForInputAction findAllEntitiesForInputAction;
    @Autowired
    FindVisitorIdByVisitorNameAction findVisitorIdByVisitorNameAction;
    @Autowired
    SignUpNewVisitorAction signUpNewVisitorAction;
    @Autowired
    HandleSecuredLoanAction handleSecuredLoanAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateHallOfFameAction calculateHallOfFameAction;
    @Autowired
    MapQasinoResponseFromRetrievedDataAction mapQasinoResponseFromRetrievedDataAction;

    @Autowired
    public QasinoController(
            VisitorRepository visitorRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository) {

        this.visitorRepository = visitorRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.turnRepository = turnRepository;
    }

    @GetMapping(value = {"/home","/home/{visitorId}"} )
    public ResponseEntity<Qasino> home(
            @PathVariable("visitorId") Optional<String> id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        if (id.isPresent()) flowDTO.setPathVariables("visitorId",id.get());
        if (!flowDTO.validateInput()) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // list - get Qasino either with or without visitor
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PostMapping(value = "/signup/{visitorName}")
    public ResponseEntity<Qasino> signup(
            @PathVariable("visitorName") String name,
            @RequestParam(name = "email", defaultValue = "") String email

    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorName",name, "email", "value2");
        if (!flowDTO.validateInput()) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // signup - get Qasino with new visitor
        output = signUpNewVisitorAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        return ResponseEntity.created(flowDTO.getUri()).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

     @GetMapping(value = "/logon/{visitorName}")
    public ResponseEntity<Qasino> logon(
            @PathVariable("visitorName") String name
    ) {
        // validate
         // validate
         QasinoFlowDTO flowDTO = new QasinoFlowDTO();
         flowDTO.setPathVariables("visitorName",name);
        if (!flowDTO.validateInput()) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // logon - get Qasino and all games for visitor
        output = findVisitorIdByVisitorNameAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        return ResponseEntity.created(flowDTO.getUri()).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    // visitor financial actions

    // Generic : pages, maxPerPage,
    // Visitor : visitorName, email
    // Player  : role, fiches, avatar, aiLevel
    // Game    : trigger, gameStateGroup, type, style, ante, jokers
    // League  : leagueName
    // Play    : move, location, bet
    // @PutMapping(value = "/pawn/{visitorId}")
    public ResponseEntity<Qasino> visitorPawnsHisShip(
            @PathVariable("visitorId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId",id);
        if (!flowDTO.validateInput()) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // pawn - get Qasino and all games for visitor with loan
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        output = handleSecuredLoanAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    // @PutMapping(value = "/repayloan/{visitorId}")
    public ResponseEntity<Qasino> visitorRepaysHisLoan(
            @PathVariable("visitorId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId",id);
        if (!flowDTO.validateInput()) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // repay - get Qasino and all games for visitor with repayed loan
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        output = handleSecuredLoanAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @GetMapping(value = "/admin/visitors")
    public ResponseEntity listVisitorsWithPaging(
            @RequestParam(name = "pages", defaultValue = "0") String page,
            @RequestParam(name = "maxPerPage", defaultValue = "4") String max
    ) {
        // validate
         QasinoFlowDTO flowDTO = new QasinoFlowDTO();
         flowDTO.setPathVariables("pages",page,"maxPerPage",max);
         if (!flowDTO.validateInput()) {
             return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
         }

        // logic
        Pageable pageable = PageRequest.of(flowDTO.getSuppliedPages(), flowDTO.getSuppliedMaxPerPage(), Sort.by(
                Sort.Order.asc("\"visitor_name\""),
                Sort.Order.desc("\"visitor_name_seq\"")
                ));
        List<Visitor> visitors = visitorRepository.findAllVisitorsWithPage(pageable);
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(visitors);

    }

    // tested - get only the list of players for a game
    // @GetMapping(value = "/player/all/game/{id}")
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
    // @GetMapping(value = "/playingcard/all/game/{id}")
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
                Sort.Order.asc("LOCATION"),
                Sort.Order.asc("SEQUENCE")));

        ArrayList playingCards =
                (ArrayList) cardRepository.findAllCardsByGameWithPage(linkedGame.getGameId(),
                        pageable);

        return ResponseEntity.ok()
                .headers(headers)
                .body(playingCards);
    }

    // todo HIGH test
    // @GetMapping(value = "/events/all/game/{id}")
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