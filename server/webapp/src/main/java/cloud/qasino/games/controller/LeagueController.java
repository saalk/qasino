package cloud.qasino.games.controller;

import cloud.qasino.games.action.CalculateHallOfFameAction;
import cloud.qasino.games.action.CreateNewLeagueAction;
import cloud.qasino.games.action.FindAllEntitiesForInputAction;
import cloud.qasino.games.action.MapQasinoResponseFromRetrievedDataAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LeagueController {

    private LeagueRepository leagueRepository;

    EventOutput.Result output;

    @Autowired
    FindAllEntitiesForInputAction findAllEntitiesForInputAction;
    @Autowired
    CreateNewLeagueAction createNewLeagueAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateHallOfFameAction calculateHallOfFameAction;
    @Autowired
    MapQasinoResponseFromRetrievedDataAction mapQasinoResponseFromRetrievedDataAction;

    @Autowired
    public LeagueController(
            LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    @GetMapping("/league/{leagueId}")
    public ResponseEntity<Qasino> getLeague(
            @PathVariable("leagueId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("leagueId", id);
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
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PostMapping(value = "/league/{leagueName}/visitor/{visitorId}")
    public ResponseEntity<Qasino> createLeague(
            @PathVariable("leagueName") String name,
            @PathVariable("visitorId") String visitorId
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("leagueName", name, "visitorId", visitorId);
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
        // create - League for Visitor
        output = createNewLeagueAction.perform(flowDTO);
        if (output == EventOutput.Result.FAILURE) {
            flowDTO.prepareResponseHeaders();
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // build response
        findAllEntitiesForInputAction.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @PutMapping(value = "/league/{leagueId}")
    public ResponseEntity<Qasino> updateLeague(
            @PathVariable("leagueId") String id,
            @RequestParam(name = "leagueName", defaultValue = "") String leagueName
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("leagueId", id,"leagueName",leagueName );
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
        // update
        if (!StringUtils.isEmpty(flowDTO.getSuppliedLeagueName())) {
            int sequence = (int) (leagueRepository.countByName(leagueName) + 1);
            flowDTO.getQasinoGameLeague().setName(leagueName);
            flowDTO.getQasinoGameLeague().setNameSequence(sequence);
        }
        leagueRepository.save(flowDTO.getQasinoGameLeague());
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @DeleteMapping("/league/{leagueId}")
    public ResponseEntity<Qasino> deleteLeague(
            @PathVariable("leagueId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("leagueId", id);
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
        // delete
        leagueRepository.deleteById(flowDTO.getSuppliedLeagueId());
        flowDTO.setQasinoGameLeague(null);
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        // delete 204 -> 200 otherwise no content in response body
        return ResponseEntity.status(HttpStatus.OK).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

}
