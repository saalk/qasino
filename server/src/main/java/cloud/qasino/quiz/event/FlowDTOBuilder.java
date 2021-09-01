package cloud.qasino.quiz.event;/*
package cloud.qasino.quiz.move.dto;


import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.statemachine.QasinoStateMachine;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class FlowDTOBuilder<T extends AbstractFlowDTO> implements ApplicationContextAware {

    @Getter
    private final T flowDTO;
    private ApplicationContext applicationContext;

    public FlowDTOBuilder(T flowDTO) {
        this.flowDTO = flowDTO;
    }

    public FlowDTOBuilder<T> addEvent(Class<? extends AbstractEvent> eventClass) {
        final AbstractEvent eventBean = applicationContext.getBean(eventClass);
        // addEvent call in AbstractFlowDTO
        flowDTO.addEvent(eventBean);
        String message = String.format("FlowDTOBuilder in addEvent is: %s", eventBean);
        log.info(message);
        return this;
    }

    public FlowDTOBuilder<T> addStateMachine(QasinoStateMachine stateMachine) {
        flowDTO.setStateMachine(stateMachine);
        String message = String.format("FlowDTOBuilder in addStateMachine is: %s", stateMachine);
        log.info(message);
        return this;
    }

    public FlowDTOBuilder<T> addContext(Game context) {
        flowDTO.setGameContext(context);
        String message = String.format("FlowDTOBuilder in addContext is: %s", context);
        log.info(message);
        return this;
    }

    @SuppressWarnings("unchecked")
    public T build() {
        String message = String.format("FlowDTOBuilder in build flowDTO is: %s", flowDTO);
        log.info(message);
        return flowDTO;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}

*/
