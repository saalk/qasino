package applyextra.commons.action;

import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.constant.CorrelationType;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.CorrelationEntityUtil;
import applyextra.operations.dto.CramDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class StoreCramCorrelationAction implements Action<StoreCramCorrelationAction.StoreCramCorrelationDTO,EventOutput.Result> {

    @Override
    public EventOutput.Result perform(StoreCramCorrelationDTO dto) {
        CorrelationEntityUtil.addCorrelation(dto.getCreditcardRequest(), dto.getCramDTO().getCramId(), CorrelationType.CRAM_ID);

        return EventOutput.Result.SUCCESS;
    }

    public interface StoreCramCorrelationDTO {
         CramDTO getCramDTO();
         CreditCardRequestEntity getCreditcardRequest();
    }
}
