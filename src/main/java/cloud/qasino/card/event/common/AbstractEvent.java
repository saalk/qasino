package cloud.qasino.card.event.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractEvent {

    @Getter
    protected List<AbstractValidation> validations = new ArrayList<>();

    protected abstract EventOutput execution(Object... eventOutput);

    public EventOutput fireEvent(Object... eventOutput) {
        checkValidation(eventOutput);
        return execution(eventOutput);
    }

    public void fireChainedEvent(AbstractFlowDTO dto) {
        checkValidation(dto);
        final EventOutput result = execution(dto);
        
        String message = String.format("AbstractEvent fireChainedEvent is dto is: %s", dto);
        log.info(message);
        
        AbstractEvent next = dto.getNextInFlow();
        if (result.isSuccess()) {
            if(result.getTrigger() != null) {
                message = String.format("AbstractEvent fireChainedEvent transition has trigger: %s", result.getTrigger());
                log.info(message);
                dto.transition(result.getTrigger());
            } else {
                message = String.format("AbstractEvent fireChainedEvent transition has no trigger", result.getTrigger());
                log.info(message);
                //dto.transition(Trigger.OK);
            }
            if (next != null) {
                message = String.format("AbstractEvent fireChainedEvent is next is: %s", next);
                log.info(message);
                next.fireChainedEvent(dto);
            }
        } else {
            //dto.transition(Trigger.NOT_OK);
        }
    }

    protected void checkValidation(Object... inputValue) {
        for (AbstractValidation validation : this.validations) {
            validation.validate(inputValue);
        }
    }

}
