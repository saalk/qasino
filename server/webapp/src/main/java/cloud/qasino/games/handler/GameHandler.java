package cloud.qasino.games.handler;

import cloud.qasino.games.request.QasinoRequest;
import cloud.qasino.games.response.QasinoResponse;
import cloud.qasino.games.response.QasinoResponseMapper;
import cloud.qasino.games.statemachine.QasinoStateMachine;
import cloud.qasino.games.dto.QasinoFlowDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalTime;

import static cloud.qasino.games.statemachine.event.GameEvent.START;

@Slf4j
@Component
public class GameHandler {

    @Resource
    private QasinoStateMachine qasinoStateMachine;

    public QasinoResponse qasinoAndVisitor(final Long visitorId) {
        log.info("########## Start of initialize: " + LocalTime.now());
        log.info("########## Start of initialize: " + LocalTime.now());

        QasinoRequest request = new QasinoRequest(); // pathvariables
        // validate and give 400 if needed
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setSuppliedVisitorId(visitorId);
//      flowDTO.addQasinoRequest(request);

        qasinoStateMachine.handleEvent(START, flowDTO);
        log.info("########## Start of initialize: " + LocalTime.now());

        return new QasinoResponseMapper().map(flowDTO);
    }

}

