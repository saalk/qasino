package applyextra.actions;

import applyextra.model.ApplyExtraCardDTO;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.dto.CramStatus;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


/**
 * Checks if the response from the notification queue was authorized from Cram.
 */
@Lazy
@Component
public class AuthorizeApplyExtraCardFulfillmentAction implements Action<ApplyExtraCardDTO,EventOutput.Result> {


    @Override
    public EventOutput.Result perform(ApplyExtraCardDTO flowDTO) {
        CramStatus cramStatus = flowDTO.getCramDTO().getStatus();
        if (!CramStatus.AUTHORISED.equals(cramStatus)) {
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }


}
