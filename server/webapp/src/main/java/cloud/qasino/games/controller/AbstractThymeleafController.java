package cloud.qasino.games.controller;

import cloud.qasino.games.action.old.CalculateQasinoStatisticsAction;
import cloud.qasino.games.action.old.DeterminePossibleEventsAction;
import cloud.qasino.games.action.old.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.old.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.old.MapQasinoGameTableFromDtoAction;
import cloud.qasino.games.action.old.MapQasinoResponseFromDtoAction;
import cloud.qasino.games.action.old.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.action.dto.CalculateStatisticsAction;
import cloud.qasino.games.action.dto.DetermineEventsAction;
import cloud.qasino.games.action.dto.FindAllDtosForUsernameAction;
import cloud.qasino.games.action.dto.MapQasinoFromDtosAction;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.QasinoFlowDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

import static cloud.qasino.games.utils.QasinoUtils.prettyPrint;

@Slf4j
public class AbstractThymeleafController {

    @Resource
    VisitorRepository visitorRepository;

    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    FindVisitorIdByAliasOrUsernameAction findVisitorIdByAliasOrUsernameAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateQasinoStatisticsAction calculateQasinoStatisticsAction;
    @Autowired
    MapQasinoResponseFromDtoAction mapQasinoResponseFromDtoAction;
    @Autowired
    MapQasinoGameTableFromDtoAction mapQasinoGameTableFromDtoAction;
    @Autowired
    DeterminePossibleEventsAction determinePossibleEventsAction;

    // @formatter:off
    @Autowired FindAllDtosForUsernameAction findDtos;
    @Autowired DetermineEventsAction determineEvents;
    @Autowired MapQasinoFromDtosAction mapQasino;
    @Autowired CalculateStatisticsAction calculateStatistics;

    public String getPricipalVisitorId(Principal principal) {
        Visitor visitor = visitorRepository.findByUsername(principal.getName());
        return String.valueOf(visitor.getVisitorId());
    }
    public void prepareQasinoResponse(HttpServletResponse response, QasinoFlowDto flowDto) {

        findVisitorIdByAliasOrUsernameAction.perform(flowDto);
        loadEntitiesToDtoAction.perform(flowDto);
        determinePossibleEventsAction.perform(flowDto);
        mapQasinoGameTableFromDtoAction.perform(flowDto);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDto);
        calculateQasinoStatisticsAction.perform(flowDto);
        mapQasinoResponseFromDtoAction.perform(flowDto);
        setHttpResponseHeader(response, flowDto);

    }
    public void prepareQasino(HttpServletResponse response, Qasino qasino) {

        findDtos.perform(qasino);
        determineEvents.perform(qasino);
        calculateStatistics.perform(qasino);
        mapQasino.perform(qasino);
        logQasino(qasino);

    }
    public void logQasino(Qasino qasino) {
        try {
//            log.warn("Qasino.getNavBarItems pretty print = {} ", prettyPrint(qasino.getNavBarItems()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getMessage pretty print = {} ", prettyPrint(qasino.getMessage()));
//            log.warn("----------------------------------------");
            log.warn("Qasino.getParams pretty print = {} ", prettyPrint(qasino.getParams()));
            log.warn("----------------------------------------");
            log.warn("Qasino.getCreation print = {} ", prettyPrint(qasino.getCreation()));
            log.warn("----------------------------------------");
            log.warn("Qasino.getVisitor pretty print = {} ", prettyPrint(qasino.getVisitor()));
            log.warn("----------------------------------------");
//            log.warn("Qasino.getGame pretty print = {} ", prettyPrint(qasino.getGame()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getPlaying pretty print = {} ", prettyPrint(qasino.getPlaying()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getResults pretty print = {} ", prettyPrint(qasino.getResults()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getInvitations pretty print = {} ", prettyPrint(qasino.getInvitations()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getLeague pretty print = {} ", prettyPrint(qasino.getLeague()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getEnumOverview pretty print = {} ", prettyPrint(qasino.getEnumOverview()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getStatistics pretty print = {} ", prettyPrint(qasino.getStatistics()));
        } catch (JsonProcessingException e) {
            try {
                var gson = new Gson();
                log.warn("Qasino gson (pretty print failed) = {} ", gson.toJson(qasino));
            } catch (StackOverflowError s){
                log.warn("Qasino gson and pretty print failed");
            }
        }
    }

    //    private HttpHeaders headers = new HttpHeaders();
//    private Object payloadData;
    private URI uri;

//    public void addKeyValueToHeader(String key, String value) {
//        this.headers.add(key, value);
//    }

    private void setHttpResponseHeader(HttpServletResponse response, QasinoFlowDto flowDto) {
        uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(flowDto.getPathVariables(), flowDto.getRequestParams())
                .toUri();
//        this.headers.add("URI", String.valueOf(uri));
        response.setHeader("Q-Uri", String.valueOf(uri));
        response.setHeader("Q-Visitor-Id", "-1");
        response.setHeader("Q-Game-Id", "-1");
        response.setHeader("Q-League-Id", "-1");
        response.setHeader("Q-Playing-Player-Id", "-1");
        response.setHeader("Q-Playing-Id", "-1");

        response.setHeader("Q-Error-Key", "");
        response.setHeader("Q-Error-Value", "");
        response.setHeader("Q-Error-Reason", "");
        response.setHeader("Q-Error-Message-Id", "");

        if (flowDto.getQasinoVisitor() != null) {
            response.setHeader("Q-Visitor-Id", String.valueOf(flowDto.getQasinoVisitor().getVisitorId()));
        }
        if (flowDto.getQasinoGame() != null) {
            response.setHeader("Q-Game-Id", String.valueOf(flowDto.getQasinoGame().getGameId()));
        }
        if (flowDto.getQasinoGameLeague() != null) {
            response.setHeader("Q-League-Id", String.valueOf(flowDto.getQasinoGameLeague().getLeagueId()));
        }
        if (flowDto.getPlayingPlayer() != null) {
            response.setHeader("Q-Playing-Player-Id", String.valueOf(flowDto.getPlayingPlayer().getPlayerId()));
        }
        if (flowDto.getActivePlaying() != null) {
            response.setHeader("Q-Playing-Id", String.valueOf(flowDto.getActivePlaying().getPlayingId()));
        }
//        if (flowDto.getHttpStatus() > 299) {

        // also add error to header
//            addKeyValueToHeader(flowDto.getErrorKey(), flowDto.getErrorValue());
//            addKeyValueToHeader("Error", flowDto.getErrorMessage());
        if (!flowDto.getErrorReason().isEmpty()) {
            // also add error to header
            response.setHeader("Q-Error-Key", flowDto.getErrorKey());
            response.setHeader("Q-Error-Value", flowDto.getErrorValue());
            response.setHeader("Q-Error-Reason", flowDto.getErrorReason());
            response.setHeader("Q-Error-Message-Id", flowDto.getErrorMessage());
        }

//            MultiValueMap<String, String> headers = flowDto.getHeaders();
//            headers.forEach((name, values) -> {
//                for (String value : values) {
//                    response.setHeader(name, value);
//                }
//            });
//        }
    }
}
