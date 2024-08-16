package cloud.qasino.games.orchestration;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.action.common.interfaces.ActionOutput;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.pattern.statemachine.event.interfaces.AbstractFlowDto;
import cloud.qasino.games.pattern.statemachine.event.interfaces.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;

import static cloud.qasino.games.pattern.statemachine.event.GameEvent.START;

@Slf4j
public class QasinoEventHandler {

    private OrchestrationConfig orchestrationConfig;
    private ApplicationContext applicationContext;
    private String label;


    public QasinoEventHandler(final OrchestrationConfig orchestrationConfig, final ApplicationContext applicationContext) {
        this.orchestrationConfig = orchestrationConfig;
        this.applicationContext = applicationContext;

        //TODO start RetryManager if any of the configured states permit retrials
    }

    public QasinoEventHandler(final OrchestrationConfig orchestrationConfig, final ApplicationContext applicationContext,
                              final String label) {
        this.orchestrationConfig = orchestrationConfig;
        this.applicationContext = applicationContext;
        this.label = label;
    }

    public <T extends AbstractFlowDto> T handleEvent(Event event, T flowDto) {
        log.warn((label != null ? label + ": " : "") + "handling event " + event);
        flowDto.setGameEvent(event);
        handleBeforeEventActions(flowDto);
        log.warn((label != null ? label + ": " : "") + "handling event " + event + " for state " + flowDto.getCurrentState());

        flowDto.setStartState(flowDto.getCurrentState());
        checkStateForEvent(event, flowDto);
        OrchestrationConfig.EventConfig eventConfig = orchestrationConfig.getEventConfig(getCurrentState(flowDto)
                , event);

        Collection<OrchestrationConfig.ActionConfig> actionConfigs = orchestrationConfig.getActionsForEvent(getCurrentState(flowDto)
                , event);

        if (event == START && !actionConfigs.isEmpty()) {
            flowDto.setEventHandlingResponse(null);
        }
        for (OrchestrationConfig.ActionConfig actionConfig : actionConfigs) {
            ActionResult actionResult = performAction(flowDto, actionConfig);
            if (transitionPerformed(flowDto, actionConfig, actionResult.transition)) {
                if (flowDto.getEventHandlingResponse() == null) {
                    flowDto.setEventHandlingResponse(actionResult.transition.getResponse());
                }
                if (actionConfig.getEvent().rethrowsExceptions() && actionResult.exception != null) {
                    throw actionResult.exception;
                }
                return flowDto;
            }
        }
        //no transition occurred
        if (eventConfig != null && flowDto.getEventHandlingResponse() == null) {
            flowDto.setEventHandlingResponse(eventConfig.getDefaultResponse());
        }
//        if (event != ENTER_STATE) { // ENTER_STATE is a nested call, callee will take care
        handleAfterEventActions(flowDto);
//        }
        return flowDto;
    }

    private GameState getCurrentState(AbstractFlowDto dto) {
        return dto.getCurrentState();
    }

    private void checkStateForEvent(Event event, AbstractFlowDto flowDto) {
        if (event == START) {
            return;
        }
        List<GameState> states = orchestrationConfig.getStatesPermittingEvent(event);
        GameState currentState = getCurrentState(flowDto);
        if (states.contains(currentState)) {
            return;
        }
        throw new IllegalStateException("state " + currentState + " is not permitted to handle event " +
                event);
    }

    private <T extends AbstractFlowDto> void handleAfterEventActions(final T flowDto) {
        for (OrchestrationConfig.ActionConfig event : orchestrationConfig.getAfterEventActions()) {
            ActionResult result = performAction(flowDto, event);
            if (result.exception != null) {
                throw result.exception;
            }
        }
    }


    private <T extends AbstractFlowDto> void handleBeforeEventActions(final T flowDto) {
        for (OrchestrationConfig.ActionConfig event : orchestrationConfig.getBeforeEventActions()) {
            ActionResult result = performAction(flowDto, event);
            if (result.exception != null) {
                throw result.exception;
            }
        }
    }

    private <T extends AbstractFlowDto> ActionResult performAction(final T flowDto, final
    OrchestrationConfig.ActionConfig actionConfig) {

        GenericLookupsAction action = applicationContext.getBean(actionConfig.getAction());
        if (action == null) {
            throw new IllegalStateException("No bean present for event " + actionConfig.getEvent() + ", make " +
                    "sure it's annotated with @Component");
        }
        log.warn("performing event " + action.getClass().getSimpleName());
        OrchestrationConfig.Transition resultingTransition;
        RuntimeException resultingException = null;
        try {
            //TODO  unchecked call
            Object output = action.perform((Qasino) flowDto);
            if (output instanceof ActionOutput) {
                output = ((ActionOutput) output).getResult();
            }
            log.warn("output of event " + action.getClass().getSimpleName() + " is " + output);
            resultingTransition = actionConfig.getTransitionForResult(output);
        } catch (RuntimeException e) {
            resultingException = e;
            log.error("Event " + action.getClass().getSimpleName() + " resulted in exception ", e);
            resultingTransition = actionConfig.getTransitionForException(e);
            if (resultingTransition == null) {
                resultingTransition = orchestrationConfig.getTransitionForException(e);
            }
            if (resultingTransition == null) {
                throw e;
            }
        }
        return new ActionResult(resultingTransition, resultingException);
    }

    private <T extends AbstractFlowDto> boolean transitionPerformed(final T flowDto, final OrchestrationConfig.ActionConfig actionConfig, final OrchestrationConfig.Transition transition) {
        if (transition != null) {
            GameState oldState = getCurrentState(flowDto);
            performTransition(transition, flowDto);
            GameState newState = getCurrentState(flowDto);
            log.warn("performed transition " + oldState + "->" + newState + " after event " + actionConfig.getAction().getSimpleName());
            handleAfterEventActions(flowDto);
            if (transition.getNextEvent() != null) {
                handleEvent(transition.getNextEvent(), flowDto);
            } else if (actionConfig.getCorrespondingState() != newState && orchestrationConfig.statePermitsEvent(newState,
                    START)) {
                handleEvent(START, flowDto);
            }
            return true;
        }
        return false;
    }

    private void performTransition(OrchestrationConfig.Transition transition,
                                   AbstractFlowDto flowDto) {

        flowDto.updateState(transition.getNextState());
    }

    private class ActionResult {
        private OrchestrationConfig.Transition transition;
        private RuntimeException exception;

        ActionResult(final OrchestrationConfig.Transition transition, final RuntimeException exception) {
            this.transition = transition;
            this.exception = exception;
        }
    }

}
