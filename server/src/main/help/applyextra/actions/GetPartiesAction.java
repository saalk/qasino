package applyextra.actions;

import applyextra.configuration.Constants;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.event.ListPartiesByIbanAndDobEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Move to get the partyId. This suppliedMove connects to the ListPartiesByIbanAndDobEvent.
 *
 */
@Lazy
@Component
public class GetPartiesAction implements Action<GetPartiesAction.GetPartiesDTO, EventOutput> {

    @Resource
    private ListPartiesByIbanAndDobEvent listPartiesByIbanAndDobEvent;

    private static final Integer TYPE = 63;
    private static final String SERVICE_NAME = "ListParties";
    private static final String MEMBER_ACCOUNT_STRING = "M";

    @Override
    public EventOutput perform(GetPartiesDTO getPartiesDTO) {
        String secondaryAccountNumber = getPartiesDTO.getProcessSpecificValue(Constants.SECONDARY_ACCOUNT_NUMBER);
        //If IBAN is empty, throw and exceptions
        if (StringUtils.isEmpty(secondaryAccountNumber)) {
            throw new ActivityException(SERVICE_NAME, "Secondary accountNumber is null", null);
        }

        getPartiesDTO.setType(TYPE);
        getPartiesDTO.setValue(MEMBER_ACCOUNT_STRING+secondaryAccountNumber);
        EventOutput result = listPartiesByIbanAndDobEvent.fireEvent(getPartiesDTO);

        if (result.isFailure()){
            throw new ActivityException(SERVICE_NAME, "Cannot fetch the list of parties", null);
        }
        //Get the party id for the party id which is not equal to the requestId.
        String partyId  = getPartiesDTO.getParties().get(0).getId();

        if (StringUtils.isEmpty(partyId)) {
            throw new ActivityException(SERVICE_NAME, "Party ID is equal to the request ID", null);
        }

        getPartiesDTO.setPartyId(partyId);
        return EventOutput.success();
    }

    public interface GetPartiesDTO extends ListPartiesByIbanAndDobEvent.ListPartiesEventDTO {
        void setType(Integer type);
        void setValue(String value);
        List<ListPartiesByIbanAndDobEvent.SimpleParty> getParties();
        String getRequestId();
        void setPartyId(String partyId);
        String getProcessSpecificValue(String key);
    }

}
