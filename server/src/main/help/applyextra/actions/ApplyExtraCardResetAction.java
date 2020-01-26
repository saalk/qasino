package applyextra.actions;

import applyextra.model.ApplyExtraCardDTO;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class ApplyExtraCardResetAction implements Action<ApplyExtraCardDTO, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(ApplyExtraCardDTO dto) {

        dto.setBeneficiaryId(null);

        return EventOutput.Result.SUCCESS;
    }
}
