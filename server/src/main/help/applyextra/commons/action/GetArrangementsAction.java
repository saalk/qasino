package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.exception.ResourceException;
import applyextra.api.parties.arrangement.PartyArrangementsP2PResourceClient;
import applyextra.api.parties.arrangement.PartyArrangementsResourceClient;
import applyextra.api.parties.arrangement.value.PartyArrangementBusinessRequest;
import applyextra.api.parties.arrangement.value.PartyArrangementBusinessResponse;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;


/**
 * Dont use this suppliedMove directly in the state machine configuration. It does not return the correct result for the eventhandler.
 */
@Lazy
@Slf4j
@Component
public class GetArrangementsAction implements Action<GetArrangementsAction.GetArrangementsActionDTO, PartyArrangementBusinessResponse> {

    @Resource
    private PartyArrangementsResourceClient resourceClient;
    @Resource
    private PartyArrangementsP2PResourceClient p2PResourceClient;

    @Override
    public PartyArrangementBusinessResponse perform(GetArrangementsActionDTO dto) {
        PartyArrangementBusinessRequest request = new PartyArrangementBusinessRequest();
        dto.addRequestCodesToRequest(request);

        PartyArrangementBusinessResponse businessResponse;
        try {
            //Set channelcontext as customer if present, otherwise use getCustomerId
            if (dto.getChannelContext() != null) {
                request.setChannelContext(dto.getChannelContext());
                businessResponse = resourceClient.execute(request);
            } else {
                request.setPartyId(dto.getCustomerId());
                businessResponse = p2PResourceClient.execute(request);
            }
        } catch (ResourceException e) {
            throw new WebApplicationException(e, javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
        }

        return businessResponse;
    }

    public interface GetArrangementsActionDTO  {
        String getCustomerId();
        ChannelContext getChannelContext();

        void addRequestCodesToRequest(PartyArrangementBusinessRequest request);
    }

}
