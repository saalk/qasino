package cloud.qasino.quiz.statemachine;

import cloud.qasino.quiz.action.FindAllEntitiesForInputAction;
import cloud.qasino.quiz.event.interfaces.AbstractFlowDTO;
import cloud.qasino.quiz.event.interfaces.Event;
import cloud.qasino.quiz.orchestration.OrchestrationConfig;
import cloud.qasino.quiz.orchestration.QasinoEventHandler;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static cloud.qasino.quiz.statemachine.GameState.*;
import static cloud.qasino.quiz.event.EventEnum.LIST;
import static cloud.qasino.quiz.event.EventOutput.Result.FAILURE;
import static cloud.qasino.quiz.event.EventOutput.Result.SUCCESS;

public class QasinoStateMachine { // implements QasinoAsyncConfiguration.ASyncEventHandler {

    public static final OrchestrationConfig qasinoConfiguration = new OrchestrationConfig();

    static {
        // @formatter:off
        qasinoConfiguration
                .beforeEventPerform(FindAllEntitiesForInputAction.class)
                .afterEventPerform(FindAllEntitiesForInputAction.class)
                .onResult(Exception.class, ERROR)
                .rethrowExceptions();

        qasinoConfiguration
                .onState(NEW)
                .onEvent(LIST)
                .perform(FindAllEntitiesForInputAction.class)
                .onResult(FAILURE, ERROR)   //Move catches RunTime Exceptions. So we need this.
                .perform(FindAllEntitiesForInputAction.class)
                .perform(FindAllEntitiesForInputAction.class)
                .onResult(FAILURE, ERROR)
                .perform(FindAllEntitiesForInputAction.class)
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
