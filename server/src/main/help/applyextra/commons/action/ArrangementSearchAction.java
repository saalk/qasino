package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import nl.ing.api.party.domain.Arrangement;
import applyextra.api.arrangement.ArrangementSearchP2PResourceClient;
import applyextra.api.arrangement.ArrangementSearchResourceClient;
import applyextra.api.arrangement.value.ArrangementSearchBusinessRequest;
import applyextra.api.arrangement.value.ArrangementSearchBusinessResponse;
import applyextra.api.exception.ResourceException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import java.util.List;

@Component
@Lazy
@Slf4j
public class ArrangementSearchAction implements Action<ArrangementSearchAction.ArrangementSearchEventDTO,EventOutput> {
    @Resource
    private ArrangementSearchResourceClient resourceClient;
    @Resource
    private ArrangementSearchP2PResourceClient p2PResourceClient;

    private static final String IBAN_ARRANGEMENT_TYPE = "4";

    public EventOutput perform(ArrangementSearchEventDTO flowDTO) {
        ArrangementSearchBusinessRequest request = new ArrangementSearchBusinessRequest();

        request.setArrangementId(flowDTO.getArrangementId());
        request.setArrangementType(IBAN_ARRANGEMENT_TYPE);
        request.setChannelContext(flowDTO.getChannelContext());

        ArrangementSearchBusinessResponse response;
        try {
            if (flowDTO.getChannelContext() != null){
                response = resourceClient.execute(request);
            } else {
                response = p2PResourceClient.execute(request);
            }

        } catch (ResourceException e) {
            throw new WebApplicationException(e, javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
        }
        flowDTO.setMainAccountCreditcardArrangements(response.getMainAccountCreditcardArrangements());

        return EventOutput.success();
    }

    public interface ArrangementSearchEventDTO {
        ChannelContext getChannelContext();
        String getArrangementId();
        void setMainAccountCreditcardArrangements(List<Arrangement> arrangement);
    }
}
