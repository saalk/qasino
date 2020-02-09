package cloud.qasino.card.resource;

import cloud.qasino.card.action.*;
import cloud.qasino.card.dto.QasinoFlowDTO;
import cloud.qasino.card.dto.enums.Enums;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.repository.*;
import cloud.qasino.card.util.Systemout;
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

import static cloud.qasino.card.configuration.Constants.DEFAULT_PAWN_SHIP_HUMAN;

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

    EventOutput.Result output;

    @Autowired
    FindAllEntitiesForInputAction findAllEntitiesForInputAction;
    @Autowired
    FindUserIdByAliasAction findUserIdByAliasAction;
    @Autowired
    SignUpNewUserAction signUpNewUserAction;
    @Autowired
    HandleSecuredLoanAction handleSecuredLoanAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateHallOfFameAction calculateHallOfFameAction;
    @Autowired
    MapQasinoResponseFromRetrievedDataAction mapQasinoResponseFromRetrievedDataAction;

    @GetMapping(value = "/logon/{alias}")
    public ResponseEntity logon(
            @RequestHeader Map<String, String> headerData,
            @PathVariable Map<String, String> pathData,
            @RequestParam Map<String, String> paramData
    ) {

        // validations
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setHeaderData(headerData);
        flowDTO.setPathData(pathData);
        flowDTO.setParamData(paramData);
        flowDTO.setPayloadData(null);
        boolean processOk = flowDTO.validateInput();
        if (!processOk) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // logic
        output = findUserIdByAliasAction.perform(flowDTO);
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
        return ResponseEntity.ok().headers(flowDTO.getHeaders()).body(flowDTO.getQasino());
    }

    // tested
    @PostMapping(value = "/signup/{alias}")
    public ResponseEntity signup(
            @RequestHeader Map<String, String> headerData,
            @PathVariable Map<String, String> pathData,
            @RequestParam Map<String, String> paramData
    ) {

        // validations
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setHeaderData(headerData);
        flowDTO.setPathData(pathData);
        flowDTO.setParamData(paramData);
        flowDTO.setPayloadData(null);
        boolean processOk = flowDTO.validateInput();
        if (!processOk) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }

        // logic
        output = signUpNewUserAction.perform(flowDTO);
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

    // tested
    @PutMapping(value = "/users/pawnship")
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

        // logic
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

    // tested
    @PutMapping(value = "/users/repayloan")
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
        // logic
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

    @GetMapping(value = "/home")
    public ResponseEntity home() {
        // validate
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setHeaderData(null);
        flowDTO.setPathData(null);
        flowDTO.setParamData(null);
        flowDTO.setPayloadData(null);
        boolean processOk = flowDTO.validateInput();
        if (!processOk) {
            return ResponseEntity.status(HttpStatus.valueOf(flowDTO.getHttpStatus())).headers(flowDTO.getHeaders()).build();
        }
        // logic
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
}