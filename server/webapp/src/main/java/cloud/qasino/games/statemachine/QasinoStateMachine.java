package cloud.qasino.games.statemachine;

import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.statemachine.event.interfaces.AbstractFlowDTO;
import cloud.qasino.games.statemachine.event.interfaces.Event;
import cloud.qasino.games.orchestration.OrchestrationConfig;
import cloud.qasino.games.orchestration.QasinoEventHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static cloud.qasino.games.database.entity.enums.game.GameState.*;
import static cloud.qasino.games.statemachine.event.GameEvent.START;
import static cloud.qasino.games.statemachine.event.EventOutput.Result.FAILURE;
import static cloud.qasino.games.statemachine.event.EventOutput.Result.SUCCESS;

@Component
public class QasinoStateMachine { // implements QasinoAsyncConfiguration.ASyncEventHandler {

    public static final OrchestrationConfig qasinoConfiguration = new OrchestrationConfig();

    static {
        // @formatter:off
        qasinoConfiguration
                .beforeEventPerform(LoadEntitiesToDtoAction.class)
                .afterEventPerform(LoadEntitiesToDtoAction.class)
                .onResult(Exception.class, ERROR)
                .rethrowExceptions();

        qasinoConfiguration
                .onState(INITIALIZED)
                .onEvent(START)
                .perform(LoadEntitiesToDtoAction.class)
                .onResult(FAILURE, ERROR)   //Move catches RunTime Exceptions. So we need this.
                .perform(LoadEntitiesToDtoAction.class)
                .perform(LoadEntitiesToDtoAction.class)
                .onResult(FAILURE, ERROR)
                .perform(LoadEntitiesToDtoAction.class)
                .onResult(SUCCESS, PREPARED);
    }

    // The Application Context is Spring's advanced container. Similar to BeanFactory,
    // it can load bean definitions, wire beans together, and dispense beans upon request.
    //
    // declared here and passed on to the eventHandler in order to do getBean() for every Action
    @Resource
    private ApplicationContext applicationContext;
    private QasinoEventHandler eventHandler;


    @PostConstruct
    public void init() {
        eventHandler = new QasinoEventHandler(qasinoConfiguration, applicationContext);
    }

    public <T extends AbstractFlowDTO> T handleEvent(Event event, T dto) {
        eventHandler.handleEvent(event, dto);
        return dto;
    }

}