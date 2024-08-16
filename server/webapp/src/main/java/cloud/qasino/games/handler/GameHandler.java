package cloud.qasino.games.handler;

import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.pattern.statemachine.QasinoStateMachine;
import cloud.qasino.games.response.QasinoResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

import static cloud.qasino.games.pattern.statemachine.event.GameEvent.START;

@Slf4j
@Component
public class GameHandler {

    @Resource
    private QasinoStateMachine qasinoStateMachine;

    public QasinoResponse qasinoAndVisitor(final Long visitorId) {
        log.warn("########## Start of initialize: " + LocalTime.now());
        log.warn("########## Start of initialize: " + LocalTime.now());

        Visitor request = new Visitor(); // pathvariables
        // validate and give 400 if needed
        Qasino flowDto = new Qasino();
        flowDto.getParams().setSuppliedVisitorId(visitorId);
//      flowDto.addQasinoRequest(request);

        qasinoStateMachine.handleEvent(START, flowDto);
        log.warn("########## Start of initialize: " + LocalTime.now());

//        return new QasinoResponseMapper().map(flowDto);
        return null;
    }

}

