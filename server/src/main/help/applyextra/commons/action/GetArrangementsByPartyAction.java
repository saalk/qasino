package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import nl.ing.api.party.domain.Arrangement;
import applyextra.api.exception.ResourceException;
import applyextra.api.parties.arrangement.PartyArrangementsP2PResourceClient;
import applyextra.api.parties.arrangement.value.PartyArrangementBusinessRequest;
import applyextra.api.parties.arrangement.value.PartyArrangementBusinessResponse;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.audit.impl.AuditDelegateHelper;
import applyextra.commons.helper.GraphiteHelper;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.ConstantsUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Deprecated
@Slf4j
@Lazy
@Component
public class GetArrangementsByPartyAction implements Action<GetArrangementsByPartyAction.GetArrangementsByPartyActionDTO, Boolean> {

    @Resource
    private PartyArrangementsP2PResourceClient p2PResourceClient;
    @Resource
    private GraphiteHelper graphiteHelper;
    @Resource
    private AuditDelegateHelper auditDelegateHelper;

    public Boolean perform(GetArrangementsByPartyActionDTO flowDTO) {
        PartyArrangementBusinessRequest businessRequest = new PartyArrangementBusinessRequest();
        businessRequest.getCategoryCodes().add(ConstantsUtil.CREDITCARDS_CATEGORY_CODE);
        businessRequest.getCategoryCodes().add(ConstantsUtil.PAYMENTS_CATEGORY_CODE);
        businessRequest.getStatusCodes().add(ConstantsUtil.ACTIVE_STATUS_CODE);
        PartyArrangementBusinessResponse businessResponse;
        try {
            businessRequest.setPartyId(flowDTO.getCustomerId());
            businessResponse = p2PResourceClient.execute(businessRequest);
        } catch (ResourceException e) {
            log.error("Error while calling gArrangementsAPI for the messageId=\""+flowDTO.getMessageId()+"\"", e);
            graphiteHelper.customCounter(ConstantsUtil.G_ARRANGEMENTS_COUNTER, ConstantsUtil.FAILURE_REQUESTS_COUNTER, 1 );
            throw new ActivityException("Infrastructure Exception for the message "+flowDTO.getMessageId()," with exception :"+e.getMessage());
        }
        if (businessResponse != null) {
            log.debug("looking for cc arrangements retrieved from gArragements : {} with messageId "+flowDTO.getMessageId(), businessResponse.getCreditCardArrangements().size(), businessResponse.getCreditCardArrangements());
            auditDelegateHelper.logMessage(ConstantsUtil.SERVICE_NAME_G_ARRANGEMENTS, "Move successful for the messageId", flowDTO.getMessageId());
            flowDTO.getCreditcardArrangements().addAll(businessResponse.getCreditCardArrangements());
            graphiteHelper.customCounter(ConstantsUtil.G_ARRANGEMENTS_COUNTER, ConstantsUtil.SUCCESS_REQUESTS_COUNTER, 1 );
            return true;
        } else {
            log.error("ListArrangements", "Cannot fetch role list from list arrangements for the messageId=\""+flowDTO.getMessageId()+"\"");
            graphiteHelper.customCounter(ConstantsUtil.G_ARRANGEMENTS_COUNTER, ConstantsUtil.FAILURE_REQUESTS_COUNTER, 1 );
            throw new ActivityException("ListArrangements", "Cannot fetch role list from list arrangements for the messageId "+flowDTO.getMessageId(), (Throwable)null);
        }
    }

    public interface GetArrangementsByPartyActionDTO {
        String getCustomerId();
        String getMessageId();
        List<Arrangement> getCreditcardArrangements();
    }
}
