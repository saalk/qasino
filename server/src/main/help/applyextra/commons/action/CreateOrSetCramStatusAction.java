package applyextra.commons.action;

import applyextra.commons.event.AbstractFlowDTO;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.constant.CorrelationType;
import applyextra.commons.model.database.entity.CorrelationEntity;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.CorrelationEntityUtil;
import applyextra.operations.dto.CramDTO;
import applyextra.operations.dto.CramStatus;
import applyextra.operations.event.CreateCramRequestEvent;
import applyextra.operations.event.SetCramStatusEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Lazy
@Component
public class CreateOrSetCramStatusAction implements Action<CreateOrSetCramStatusAction.CreateOrSetCramStatusActionDTO, EventOutput.Result> {

    @Resource
    private SetCramStatusEvent setCramStatusEvent;
    @Resource
    private CreateCramRequestEvent createCramRequestEvent;

    @Override
    public EventOutput.Result perform(CreateOrSetCramStatusActionDTO flowDTO) {
        EventOutput eventOutput;
        flowDTO.getCramDTO().setStatus(CramStatus.SUBMITTED);

        CorrelationEntity correlation = CorrelationEntityUtil.getCorrelationByType(flowDTO.getCreditcardRequest().getCorrelations(), CorrelationType.CRAM_ID);
        if (correlation != null){
            flowDTO.getCramDTO().setCramId(correlation.getExternalReference());
            eventOutput = (EventOutput) setCramStatusEvent.perform((AbstractFlowDTO) flowDTO);
            if (EventOutput.Result.SUCCESS.equals(eventOutput.getResult())){
                flowDTO.getCramDTO().setConfirmResult(true);
            }
        } else {
            eventOutput = (EventOutput) createCramRequestEvent.perform((AbstractFlowDTO) flowDTO);
        }
        return eventOutput.getResult();
    }

    public interface CreateOrSetCramStatusActionDTO extends CreateCramRequestEvent.CreateCramRequestEventDTO, SetCramStatusEvent.SetCramStatusEventDTO {
        CramDTO getCramDTO();
        CreditCardRequestEntity getCreditcardRequest();
    }
}
