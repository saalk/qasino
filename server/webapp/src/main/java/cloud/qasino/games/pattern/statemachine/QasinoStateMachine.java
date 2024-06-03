package cloud.qasino.games.pattern.statemachine;

import cloud.qasino.games.action.LoadEntitiesToDtoAction;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.interfaces.AbstractFlowDTO;
import cloud.qasino.games.pattern.statemachine.event.interfaces.Event;
import cloud.qasino.games.orchestration.OrchestrationConfig;
import cloud.qasino.games.orchestration.QasinoEventHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import static cloud.qasino.games.database.entity.enums.game.GameState.*;

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
                .onEvent(GameEvent.START)
                .perform(LoadEntitiesToDtoAction.class)
                .onResult(EventOutput.Result.FAILURE, ERROR)   //Move catches RunTime Exceptions. So we need this.
                .perform(LoadEntitiesToDtoAction.class)
                .perform(LoadEntitiesToDtoAction.class)
                .onResult(EventOutput.Result.FAILURE, ERROR)
                .perform(LoadEntitiesToDtoAction.class)
                .onResult(EventOutput.Result.SUCCESS, PREPARED);
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
