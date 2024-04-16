package cloud.qasino.games.controller;

import cloud.qasino.games.action.*;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
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

    // basic path /qasino
    // basic header @RequestHeader(value "visitor", required = true) int visitorId" // else 400
    //
    // 200 - ok
    // 201 - created
    // 400 - bad request - error/reason "url ... not available"
    // 404 - not found - error/message "invalid value x for y" + reason [missing]
    // 412 - precondition failed = error/message - "violation of rule z"
    // 500 - internal server error

    @Autowired
    public VisitorController(
            VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;

    }

    // visitor financial actions
    @PutMapping(value = "/visitor/pawnship")
    public ResponseEntity pawnship(
            @RequestHeader Map<String, String> headerData,
            @PathVariable Map<String, String> pathData,
            @RequestParam Map<String, String> paramData
    ) {
        // validate
        pathData = new HashMap<>();
        pathData.put("pawnship", "true");
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setHeaderData(headerData);
        flowDTO.setPathData(pathData);
        flowDTO.setParamData(paramData);
        flowDTO.setPayloadData(null);
        boolean processOk = flowDTO.validateInput();
        if (!processOk) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        // actions
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

    @PutMapping(value = "/visitor/repayloan")
    public ResponseEntity repayloan(
            @RequestHeader Map<String, String> headerData,
            @PathVariable Map<String, String> pathData,
            @RequestParam Map<String, String> paramData
    ) {
        // validate
        pathData = new HashMap<>();
        pathData.put("repayloan", "true");
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setHeaderData(headerData);
        flowDTO.setPathData(pathData);
        flowDTO.setParamData(paramData);
        flowDTO.setPayloadData(null);
        boolean processOk = flowDTO.validateInput();
        if (!processOk) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        // actions
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

    // vistor CRUD
    // /api/visitor/{visitorId} - GET, DELETE, PUT visitorName, email only

    @GetMapping("/visitor/{visitorId}")
    public ResponseEntity<Optional<Visitor>> getVisitor(
            @PathVariable("visitorId") String id
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id)) {
            return ResponseEntity.badRequest().headers(headers).build();
        }

        // CRUD
        Optional<Visitor> foundVisitor = visitorRepository.findById(Long.parseLong(id));
        if (foundVisitor.isPresent()) {
            return ResponseEntity.ok().headers(headers).body(foundVisitor);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }

    }

    // tested
    @PutMapping(value = "/visitor/{visitorId}")
    public ResponseEntity<Visitor> updateVisitor(
            @PathVariable("visitorId") String id,
            @RequestParam(name = "visitorName", defaultValue = "") String visitorName,
            @RequestParam(name = "email", defaultValue = "") String email
    ) {

        // header in response
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .query("")
                .buildAndExpand(visitorName, email)
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("URI", String.valueOf(uri));

        // validations
        if (!StringUtils.isNumeric(id))
            // 400
            return ResponseEntity.badRequest().headers(headers).build();
        long visitorId = Long.parseLong(id);
        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        if (!foundVisitor.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // CRUD
        Visitor updatedVisitor = foundVisitor.get();
        if (!StringUtils.isEmpty(visitorName)) {
            int sequence = (int) (visitorRepository.countByVisitorName(visitorName) + 1);
            updatedVisitor.setVisitorName(visitorName);
            updatedVisitor.setVisitorNameSequence(sequence);
        }
        if (!StringUtils.isEmpty(email)) {
            updatedVisitor.setEmail(email);
        }
        updatedVisitor = visitorRepository.save(updatedVisitor);

        // 200
        return ResponseEntity.ok().headers(headers).body(updatedVisitor);
    }

    // tested
    @DeleteMapping("/visitor/{visitorId}")
    public ResponseEntity<Visitor> deleteVisitor(
            @PathVariable("visitorId") String id
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
        long visitorId = Long.parseLong(id);
        Optional<Visitor> foundVisitor = visitorRepository.findById(visitorId);
        if (!foundVisitor.isPresent()) {
            // 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
        }

        // CRUD
        visitorRepository.deleteById(visitorId);
        // delete 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

}