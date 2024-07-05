package cloud.qasino.games.controller;

import cloud.qasino.games.action.CalculateQasinoStatisticsAction;
import cloud.qasino.games.action.DeterminePossibleEventsAction;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDtoAction;
import cloud.qasino.games.action.MapQasinoResponseFromDtoAction;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.QasinoFlowDto;
import cloud.qasino.games.response.QasinoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

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
    DeterminePossibleEventsAction determinePossibleEvents;

    public String prettyPrintJson(QasinoResponse qasinoResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(qasinoResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPricipalVisitorId(Principal principal) {
        Visitor visitor = visitorRepository.findByUsername(principal.getName());
        return String.valueOf(visitor.getVisitorId());
    }

    public void prepareQasinoResponse(HttpServletResponse response, QasinoFlowDto flowDto) {

        findVisitorIdByAliasOrUsernameAction.perform(flowDto);
        loadEntitiesToDtoAction.perform(flowDto);
        determinePossibleEvents.perform(flowDto);
        mapQasinoGameTableFromDtoAction.perform(flowDto);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDto);
        calculateQasinoStatisticsAction.perform(flowDto);
        mapQasinoResponseFromDtoAction.perform(flowDto);
        setHttpResponseHeader(response, flowDto);

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
