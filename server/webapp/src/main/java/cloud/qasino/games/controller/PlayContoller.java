package cloud.qasino.games.controller;

import cloud.qasino.games.action.CalculateHallOfFameAction;
import cloud.qasino.games.action.FindAllEntitiesForInputAction;
import cloud.qasino.games.action.IsGameConsistentForGameTrigger;
import cloud.qasino.games.action.IsGameFinished;
import cloud.qasino.games.action.IsTurnConsistentForTurnTrigger;
import cloud.qasino.games.action.MakeGamePlayableForGameType;
import cloud.qasino.games.action.MapQasinoResponseFromRetrievedDataAction;
import cloud.qasino.games.action.MapTableFromRetrievedDataAction;
import cloud.qasino.games.action.PlayNextTurnAndCardMovesForHuman;
import cloud.qasino.games.action.CalculateAndFinishGame;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.PlayFirstTurnAndInitialCardMovesForGameType;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayContoller {

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
    CalculateAndFinishGame calculateAndFinishGame;
    @Autowired
    MakeGamePlayableForGameType makeGamePlayableForGameType;
    @Autowired
    PlayFirstTurnAndInitialCardMovesForGameType playFirstTurnAndInitialCardMovesForGameType;
    @Autowired
    FindAllEntitiesForInputAction findAllEntitiesForInputAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateHallOfFameAction calculateHallOfFameAction;
    @Autowired
    MapQasinoResponseFromRetrievedDataAction mapQasinoResponseFromRetrievedDataAction;
    @Autowired
    MapTableFromRetrievedDataAction mapTableFromRetrievedDataAction;
    @Autowired
    PlayNextTurnAndCardMovesForHuman playNextTurnAndCardMovesForHuman;

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private CardRepository cardRepository;
    private TurnRepository turnRepository;
    private CardMoveRepository cardMoveRepository;

    @Autowired
    public PlayContoller(
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
    @PostMapping(value = "/game/{gameId}/play")
    public ResponseEntity<Qasino> startPlayingTheGame(
            @RequestHeader("visitorId") String vId,
//            @RequestHeader("turnPlayerId") String pId,
            @PathVariable("gameId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId",vId,"gameId", id, "gameTrigger", "play");
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = findAllEntitiesForInputAction.perform(flowDTO);
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
        makeGamePlayableForGameType.perform(flowDTO);
        mapTableFromRetrievedDataAction.perform(flowDTO);
        playFirstTurnAndInitialCardMovesForGameType.perform(flowDTO);
        // build response
        findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        mapTableFromRetrievedDataAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PostMapping(value = "/game/{gameId}/turn/{turnTrigger}")
    public ResponseEntity<Qasino> playerMakesAMoveForAGame(
            @RequestHeader("turnPlayerId") String pId,
            @PathVariable("gameId") String id,
            @PathVariable("turnTrigger") String trigger) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("turnPlayerId",pId,"gameId", id, "gameTrigger", "turn", "turnTrigger", trigger);
        if (!flowDTO.validateInput()) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // get all entities
        output = findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        mapTableFromRetrievedDataAction.perform(flowDTO);

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
        mapTableFromRetrievedDataAction.perform(flowDTO);
//        output = isPlayerHuman.perform(flowDTO);
        playNextTurnAndCardMovesForHuman.perform(flowDTO);
////      OR playNextTurnAndCardMovesForBot.perform(flowDTO);
//
        // todo find 500 at Optional<Card> previousCard
        updateFichesForPlayer.perform(flowDTO);
        output = isGameFinished.perform(flowDTO);
        if (output == EventOutput.Result.SUCCESS) {
            output = calculateAndFinishGame.perform(flowDTO);
        }

        // build response
        findAllEntitiesForInputAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        mapTableFromRetrievedDataAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(201)).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }
}

