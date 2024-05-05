package cloud.qasino.games.controller;

import cloud.qasino.games.action.CalculateQasinoStatistics;
import cloud.qasino.games.action.IsPlayerHuman;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.IsGameConsistentForGameTrigger;
import cloud.qasino.games.action.IsGameFinished;
import cloud.qasino.games.action.IsTurnConsistentForTurnTrigger;
import cloud.qasino.games.action.PlayGameForType;
import cloud.qasino.games.action.MapQasinoResponseFromDto;
import cloud.qasino.games.action.MapQasinoGameTableFromDto;
import cloud.qasino.games.action.PlayNextBotTurnAction;
import cloud.qasino.games.action.PlayNextHumanTurnAction;
import cloud.qasino.games.action.CalculateAndFinishGameAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.PlayFirstTurnAction;
import cloud.qasino.games.action.StopGameAction;
import cloud.qasino.games.action.UpdateFichesForPlayer;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.event.EventOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TurnAndCardMoveContoller {

    EventOutput.Result output;

    @Autowired
    IsGameConsistentForGameTrigger isGameConsistentForGameTrigger;
    @Autowired
    IsTurnConsistentForTurnTrigger isTurnConsistentForTurnTrigger;
    @Autowired
    IsGameFinished isGameFinished;
    @Autowired
    UpdateFichesForPlayer updateFichesForPlayer;
    @Autowired
    CalculateAndFinishGameAction calculateAndFinishGameAction;
    @Autowired
    PlayGameForType playGameForType;
    @Autowired
    PlayFirstTurnAction playFirstTurnAction;
    @Autowired
    StopGameAction stopGameAction;
    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateQasinoStatistics calculateQasinoStatistics;
    @Autowired
    MapQasinoResponseFromDto mapQasinoResponseFromDto;
    @Autowired
    MapQasinoGameTableFromDto mapQasinoGameTableFromDto;
    @Autowired
    PlayNextHumanTurnAction playNextHumanTurnAction;
    @Autowired
    IsPlayerHuman isPlayerHuman;
    @Autowired
    PlayNextBotTurnAction playNextBotTurnAction;

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;
    private CardMoveRepository cardMoveRepository;

    @Autowired
    public TurnAndCardMoveContoller(
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository,
            CardMoveRepository cardMoveRepository
    ) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
        this.turnRepository = turnRepository;
        this.cardMoveRepository = cardMoveRepository;
    }

    // POST - gametrigger PLAY add cards -> can only be done by visitor
    // -> gamestate STARTED
    @PutMapping(value = "/game/{gameId}/shuffle")
    public ResponseEntity<Qasino> startShuffleAndDealingTheGame(
            @RequestHeader("visitorId") String vId,
//            @RequestHeader("turnPlayerId") String pId,
            @PathVariable("gameId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId",vId,"gameId", id, "gameTrigger", "shuffle");
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // logic
        output = isGameConsistentForGameTrigger.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        playGameForType.perform(flowDTO);
        mapQasinoGameTableFromDto.perform(flowDTO);
        playFirstTurnAction.perform(flowDTO);
        // get all entities and build reponse
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // build response
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PostMapping(value = "/game/{gameId}/turn/{turnTrigger}")
    public ResponseEntity<Qasino> playerMakesAMoveForAGame(
            @RequestHeader("visitorId") String vId,
            @RequestHeader("turnPlayerId") String pId,
            @PathVariable("gameId") String id,
            @PathVariable("turnTrigger") String trigger) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId",vId, "turnPlayerId",pId,"gameId", id, "gameTrigger", "turn", "turnTrigger", trigger);
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities and build reponse
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        mapQasinoGameTableFromDto.perform(flowDTO);

        // logic
        output = isGameConsistentForGameTrigger.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        output = isTurnConsistentForTurnTrigger.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
//        output = canPlayerStillPlay.perform(flowDTO); // for now stop after one round
        mapQasinoGameTableFromDto.perform(flowDTO);
        output = isPlayerHuman.perform(flowDTO);
        if (output == EventOutput.Result.SUCCESS) {
            playNextHumanTurnAction.perform(flowDTO);
        } else {
            playNextBotTurnAction.perform(flowDTO);
        }
        updateFichesForPlayer.perform(flowDTO);
        output = isGameFinished.perform(flowDTO);
        if (output == EventOutput.Result.SUCCESS) {
            output = calculateAndFinishGameAction.perform(flowDTO);
        }

        // get all entities and build reponse
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PutMapping(value = "/game/{gameId}/stop")
    public ResponseEntity<Qasino> stopPlayingTheGame(
            @RequestHeader("visitorId") String vId,
//            @RequestHeader("turnPlayerId") String pId,
            @PathVariable("gameId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", vId,"gameId", id, "gameTrigger", "stop");
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // logic
        output = isGameConsistentForGameTrigger.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        stopGameAction.perform(flowDTO);
        if (output == EventOutput.Result.SUCCESS) {
            output = calculateAndFinishGameAction.perform(flowDTO);
        }

        // get all entities and build reponse
        output = loadEntitiesToDtoAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

}

