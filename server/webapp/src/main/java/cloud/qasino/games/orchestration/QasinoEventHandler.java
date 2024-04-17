package cloud.qasino.games.orchestration;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.action.interfaces.ActionOutput;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.event.interfaces.AbstractFlowDTO;
import cloud.qasino.games.event.interfaces.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;

import static cloud.qasino.games.event.EventEnum.ENTER_STATE;

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

    public <T extends AbstractFlowDTO> T handleEvent(Event event, T flowDTO) {
        log.debug((label != null ? label + ": " : "") + "handling event " + event);
        flowDTO.setCurrentEvent(event);
        handleBeforeEventActions(flowDTO);
        log.debug((label != null ? label + ": " : "") + "handling event " + event + " for state " + flowDTO.getCurrentState());

        flowDTO.setStartState(flowDTO.getCurrentState());
        checkStateForEvent(event, flowDTO);
        OrchestrationConfig.EventConfig eventConfig = orchestrationConfig.getEventConfig(getCurrentState(flowDTO)
                , event);

        Collection<OrchestrationConfig.ActionConfig> actionConfigs = orchestrationConfig.getActionsForEvent(getCurrentState(flowDTO)
                , event);

        if (event == ENTER_STATE && !actionConfigs.isEmpty()) {
            flowDTO.setEventHandlingResponse(null);
        }
        for (OrchestrationConfig.ActionConfig actionConfig : actionConfigs) {
            ActionResult actionResult = performAction(flowDTO, actionConfig);
            if (transitionPerformed(flowDTO, actionConfig, actionResult.transition)) {
                if (flowDTO.getEventHandlingResponse() == null) {
                    flowDTO.setEventHandlingResponse(actionResult.transition.getResponse());
                }
                if (actionConfig.getEvent().rethrowsExceptions() && actionResult.exception != null) {
                    throw actionResult.exception;
                }
                return flowDTO;
            }
        }
        //no transition occurred
        if (eventConfig != null && flowDTO.getEventHandlingResponse() == null) {
            flowDTO.setEventHandlingResponse(eventConfig.getDefaultResponse());
        }
//        if (event != ENTER_STATE) { // ENTER_STATE is a nested call, callee will take care
        handleAfterEventActions(flowDTO);
//        }
        return flowDTO;
    }

    private GameState getCurrentState(AbstractFlowDTO dto) {
        return dto.getCurrentState();
    }

    private void checkStateForEvent(Event event, AbstractFlowDTO flowDto) {
        if (event == ENTER_STATE) {
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

    private <T extends AbstractFlowDTO> void handleAfterEventActions(final T flowDTO) {
        for (OrchestrationConfig.ActionConfig event : orchestrationConfig.getAfterEventActions()) {
            ActionResult result = performAction(flowDTO, event);
            if (result.exception != null) {
                throw result.exception;
            }
        }
    }


    private <T extends AbstractFlowDTO> void handleBeforeEventActions(final T flowDTO) {
        for (OrchestrationConfig.ActionConfig event : orchestrationConfig.getBeforeEventActions()) {
            ActionResult result = performAction(flowDTO, event);
            if (result.exception != null) {
                throw result.exception;
            }
        }
    }

    private <T extends AbstractFlowDTO> ActionResult performAction(final T flowDTO, final
    OrchestrationConfig.ActionConfig actionConfig) {

        Action action = applicationContext.getBean(actionConfig.getAction());
        if (action == null) {
            throw new IllegalStateException("No bean present for event " + actionConfig.getEvent() + ", make " +
                    "sure it's annotated with @Component");
        }
        log.debug("performing event " + action.getClass().getSimpleName());
        OrchestrationConfig.Transition resultingTransition;
        RuntimeException resultingException = null;
        try {
            //TODO  unchecked call
            Object output = action.perform(flowDTO);
            if (output instanceof ActionOutput) {
                output = ((ActionOutput) output).getResult();
            }
            log.debug("output of event " + action.getClass().getSimpleName() + " is " + output);
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

    private <T extends AbstractFlowDTO> boolean transitionPerformed(final T flowDTO, final OrchestrationConfig.ActionConfig actionConfig, final OrchestrationConfig.Transition transition) {
        if (transition != null) {
            GameState oldState = getCurrentState(flowDTO);
            performTransition(transition, flowDTO);
            GameState newState = getCurrentState(flowDTO);
            log.debug("performed transition " + oldState + "->" + newState + " after event " + actionConfig.getAction().getSimpleName());
            handleAfterEventActions(flowDTO);
            if (transition.getNextEvent() != null) {
                handleEvent(transition.getNextEvent(), flowDTO);
            } else if (actionConfig.getCorrespondingState() != newState && orchestrationConfig.statePermitsEvent(newState,
                    ENTER_STATE)) {
                handleEvent(ENTER_STATE, flowDTO);
            }
            return true;
        }
        return false;
    }

    private void performTransition(OrchestrationConfig.Transition transition,
                                   AbstractFlowDTO flowDTO) {

        flowDTO.updateState(transition.getNextState());
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
