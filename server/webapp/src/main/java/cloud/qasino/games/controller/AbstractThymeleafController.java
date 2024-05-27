package cloud.qasino.games.controller;

import cloud.qasino.games.action.CalculateQasinoStatistics;
import cloud.qasino.games.action.FindVisitorIdByAliasOrUsernameAction;
import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.action.MapQasinoGameTableFromDto;
import cloud.qasino.games.action.MapQasinoResponseFromDto;
import cloud.qasino.games.action.SetStatusIndicatorsBaseOnRetrievedDataAction;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

public class AbstractThymeleafController {

    @Resource
    VisitorRepository visitorRepository;

//    EventOutput.Result output;

//    private VisitorRepository visitorRepository;
//    private GameRepository gameRepository;
//    private PlayerRepository playerRepository;
//    private CardRepository cardRepository;
//    private TurnRepository turnRepository;
//    private ResultsRepository resultsRepository;
    @Autowired
    LoadEntitiesToDtoAction loadEntitiesToDtoAction;
    @Autowired
    FindVisitorIdByAliasOrUsernameAction findVisitorIdByAliasOrUsernameAction;
    @Autowired
    SetStatusIndicatorsBaseOnRetrievedDataAction setStatusIndicatorsBaseOnRetrievedDataAction;
    @Autowired
    CalculateQasinoStatistics calculateQasinoStatistics;
    @Autowired
    MapQasinoResponseFromDto mapQasinoResponseFromDto;
    @Autowired
    MapQasinoGameTableFromDto mapQasinoGameTableFromDto;

    public String getPricipalVisitorId(Principal principal) {
        Visitor visitor = visitorRepository.findByUsername(principal.getName());
        return String.valueOf(visitor.getVisitorId());
    }

    public void prepareQasinoResponse(HttpServletResponse response, QasinoFlowDTO flowDTO) {

        findVisitorIdByAliasOrUsernameAction.perform(flowDTO);
        loadEntitiesToDtoAction.perform(flowDTO);
        mapQasinoGameTableFromDto.perform(flowDTO);
        setStatusIndicatorsBaseOnRetrievedDataAction.perform(flowDTO);
        calculateQasinoStatistics.perform(flowDTO);
        mapQasinoResponseFromDto.perform(flowDTO);
        setHttpResponseHeader(response, flowDTO);

    }

    //    private HttpHeaders headers = new HttpHeaders();
//    private Object payloadData;
    private URI uri;

//    public void addKeyValueToHeader(String key, String value) {
//        this.headers.add(key, value);
//    }

    private void setHttpResponseHeader(HttpServletResponse response, QasinoFlowDTO flowDTO) {
        uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("")
                .buildAndExpand(flowDTO.getPathVariables(), flowDTO.getRequestParams())
                .toUri();
//        this.headers.add("URI", String.valueOf(uri));
        response.setHeader("Q-Uri", String.valueOf(uri));
        response.setHeader("Q-Visitor-Id", "-1");
        response.setHeader("Q-Game-Id", "-1");
        response.setHeader("Q-League-Id", "-1");
        response.setHeader("Q-Turn-Player-Id", "-1");
        response.setHeader("Q-Turn-Id", "-1");

        response.setHeader("Q-Error-Key", "");
        response.setHeader("Q-Error-Value", "");
        response.setHeader("Q-Error-Reason", "");
        response.setHeader("Q-Error-Message-Id", "");

        if (flowDTO.getQasinoVisitor() != null) {
            response.setHeader("Q-Visitor-Id", String.valueOf(flowDTO.getQasinoVisitor().getVisitorId()));
        }
        if (flowDTO.getQasinoGame() != null) {
            response.setHeader("Q-Game-Id", String.valueOf(flowDTO.getQasinoGame().getGameId()));
        }
        if (flowDTO.getQasinoGameLeague() != null) {
            response.setHeader("Q-League-Id", String.valueOf(flowDTO.getQasinoGameLeague().getLeagueId()));
        }
        if (flowDTO.getTurnPlayer() != null) {
            response.setHeader("Q-Turn-Player-Id", String.valueOf(flowDTO.getTurnPlayer().getPlayerId()));
        }
        if (flowDTO.getActiveTurn() != null) {
            response.setHeader("Q-Turn-Id", String.valueOf(flowDTO.getActiveTurn().getTurnId()));
        }
//        if (flowDTO.getHttpStatus() > 299) {

            // also add error to header
//            addKeyValueToHeader(flowDTO.getErrorKey(), flowDTO.getErrorValue());
//            addKeyValueToHeader("Error", flowDTO.getErrorMessage());
            if (!flowDTO.getErrorReason().isEmpty()) {
                // also add error to header
                response.setHeader("Q-Error-Key", flowDTO.getErrorKey());
                response.setHeader("Q-Error-Value", flowDTO.getErrorValue());
                response.setHeader("Q-Error-Reason", flowDTO.getErrorReason());
                response.setHeader("Q-Error-Message-Id", flowDTO.getErrorMessage());
            }

//            MultiValueMap<String, String> headers = flowDTO.getHeaders();
//            headers.forEach((name, values) -> {
//                for (String value : values) {
//                    response.setHeader(name, value);
//                }
//            });
//        }
    }
}
