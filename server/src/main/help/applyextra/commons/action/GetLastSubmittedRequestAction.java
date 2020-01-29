package applyextra.commons.action;

import applyextra.commons.configuration.RequestType;
import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.Game;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Lazy
@Component
public class GetLastSubmittedRequestAction implements Action<GetLastSubmittedRequestAction.GetLastRequestActionDTO, EventOutput> {

    @Resource
    private CreditcardRequestService ccRequestService;

    @Override
    public EventOutput perform(final GetLastRequestActionDTO flowDto) {
        String iban = flowDto.getCreditcardRequest().getAccount().getIban();
        Game request = ccRequestService.getLastAuthorizedRequestByIban(flowDto.getContextType(), iban);
        flowDto.setLastRequest(request);
        return EventOutput.success();
    }

    public interface GetLastRequestActionDTO {
        Game getCreditcardRequest();

        void setLastRequest(final Game request);

        Game getLastRequest();

        RequestType getContextType();
    }
}
