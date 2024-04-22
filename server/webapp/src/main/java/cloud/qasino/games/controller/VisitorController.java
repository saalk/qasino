package cloud.qasino.games.controller;

import cloud.qasino.games.action.*;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.event.EventOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static cloud.qasino.games.configuration.Constants.BASE_PATH;
import static cloud.qasino.games.configuration.Constants.ENDPOINT_VISITOR;

@RestController
public class VisitorController {

    private VisitorRepository visitorRepository;

    EventOutput.Result output;

    @Autowired
    FindAllEntitiesForInputAction findAllEntitiesForInputAction;
    @Autowired
    HandleSecuredLoanAction handleSecuredLoanAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateHallOfFameAction calculateHallOfFameAction;
    @Autowired
    MapQasinoResponseFromRetrievedDataAction mapQasinoResponseFromRetrievedDataAction;

    @Autowired
    public VisitorController(
            VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    @GetMapping("/visitor/{visitorId}")
    public ResponseEntity<Qasino> getVisitor(
            @PathVariable("visitorId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", id);
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

    @PutMapping(value = "/visitor/{visitorId}")
    public ResponseEntity<Qasino> updateVisitor(
            @PathVariable("visitorId") String id,
            @RequestParam(name = "visitorName", defaultValue = "") String visitorName,
            @RequestParam(name = "email", defaultValue = "") String email
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
       flowDTO.setPathVariables("visitorId", id);
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
        if (!StringUtils.isEmpty(flowDTO.getSuppliedVisitorName())) {
            int sequence = (int) (visitorRepository.countByVisitorName(visitorName) + 1);
            flowDTO.getQasinoVisitor().setVisitorName(visitorName);
            flowDTO.getQasinoVisitor().setVisitorNameSequence(sequence);
        }
        if (!StringUtils.isEmpty(flowDTO.getSuppliedEmail())) {
            flowDTO.getQasinoVisitor().setEmail(email);
        }
        visitorRepository.save(flowDTO.getQasinoVisitor());
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    @DeleteMapping("/visitor/{visitorId}")
    public ResponseEntity<Qasino> deleteVisitor(
            @PathVariable("visitorId") String id
    ) {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setPathVariables("visitorId", id);
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
        visitorRepository.deleteById(flowDTO.getSuppliedVisitorId());
        flowDTO.setSuppliedVisitorId(0);
        flowDTO.setQasinoVisitor(null);
        // build response
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateHallOfFameAction.perform(flowDTO);
        mapQasinoResponseFromRetrievedDataAction.perform(flowDTO);
        flowDTO.prepareResponseHeaders();
        // delete 204 -> 200 otherwise no content in response body
        return ResponseEntity.status(HttpStatus.OK).headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

}