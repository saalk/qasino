package cloud.qasino.games.controller;

import cloud.qasino.games.action.common.CalculateStatisticsAction;
import cloud.qasino.games.action.common.DetermineEventsAction;
import cloud.qasino.games.action.common.MapQasinoFromDtosAction;
import cloud.qasino.games.action.common.load.LoadPrincipalDtoAction;
import cloud.qasino.games.database.service.VisitorService;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.model.VisitorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.security.Principal;

import static cloud.qasino.games.utils.QasinoUtils.prettyPrint;

@Slf4j
public class AbstractThymeleafController {

    @Resource
    VisitorService visitorService;

    // @formatter:off
    @Autowired LoadPrincipalDtoAction loadVisitor;
    @Autowired DetermineEventsAction determineEvents;
    @Autowired MapQasinoFromDtosAction mapQasino;
    @Autowired CalculateStatisticsAction calculateStatistics;

    public String getPricipalVisitorId(Principal principal) {
        VisitorDto visitor = visitorService.findByUsername(principal.getName());
        return String.valueOf(visitor.getVisitorId());
    }
    public void prepareQasino(HttpServletResponse response, Qasino qasino) {

        loadVisitor.perform(qasino);
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
//            log.warn("Qasino.getParams pretty print = {} ", prettyPrint(qasino.getParams()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getCreation print = {} ", prettyPrint(qasino.getCreation()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getVisitor pretty print = {} ", prettyPrint(qasino.getVisitor()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getGame pretty print = {} ", prettyPrint(qasino.getGame()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getPlaying pretty print = {} ", prettyPrint(qasino.getPlaying()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getResults pretty print = {} ", prettyPrint(qasino.getResults()));
//            log.warn("----------------------------------------");
//            log.warn("Qasino.getInvitations pretty print = {} ", prettyPrint(qasino.getInvitations()));
//            log.warn("----------------------------------------");
            log.warn("Qasino.getLeague pretty print = {} ", prettyPrint(qasino.getLeague()));
            log.warn("----------------------------------------");
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

    private void setHttpResponseHeader(HttpServletResponse response, Qasino qasino) {
//        uri = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("")
//                .buildAndExpand(qasino.getPathVariables(), qasino.getRequestParams())
//                .toUri();
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

        if (qasino.getVisitor() != null) {
            response.setHeader("Q-Visitor-Id", String.valueOf(qasino.getVisitor().getVisitorId()));
        }
        if (qasino.getGame() != null) {
            response.setHeader("Q-Game-Id", String.valueOf(qasino.getGame().getGameId()));
        }
        if (qasino.getLeague() != null) {
            response.setHeader("Q-League-Id", String.valueOf(qasino.getLeague().getLeagueId()));
        }
        if (qasino.getPlaying().getCurrentPlayer() != null) {
            response.setHeader("Q-Playing-Player-Id", String.valueOf(qasino.getPlaying().getCurrentPlayer().getPlayerId()));
        }
        if (qasino.getPlaying() != null) {
            response.setHeader("Q-Playing-Id", String.valueOf(qasino.getPlaying().getPlayingId()));
        }
//        if (flowDto.getHttpStatus() > 299) {

        // also add error to header
//            addKeyValueToHeader(flowDto.getErrorKey(), flowDto.getErrorValue());
//            addKeyValueToHeader("Error", flowDto.getErrorMessage());
        if (!qasino.getMessage().getErrorReason().isEmpty()) {
            // also add error to header
            response.setHeader("Q-Error-Key", qasino.getMessage().getErrorKey());
            response.setHeader("Q-Error-Value", qasino.getMessage().getErrorValue());
            response.setHeader("Q-Error-Reason", qasino.getMessage().getErrorReason());
            response.setHeader("Q-Error-Message-Id", qasino.getMessage().getErrorMessage());
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
