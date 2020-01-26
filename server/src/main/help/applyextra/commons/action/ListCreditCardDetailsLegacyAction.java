package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.orchestration.Action;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.creditcardlegacy.listcreditcarddetails.ListCreditCardDetailsServiceOperationClient;
import nl.ing.sc.creditcardlegacy.listcreditcarddetails.value.CardDetail;
import nl.ing.sc.creditcardlegacy.listcreditcarddetails.value.ListCreditCardDetailsBusinessRequest;
import nl.ing.sc.creditcardlegacy.listcreditcarddetails.value.ListCreditCardDetailsBusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
@Lazy
public class ListCreditCardDetailsLegacyAction implements Action<ListCreditCardDetailsLegacyAction.ListCreditCardDetailsLegacyDTO, Boolean> {

	
	@Resource
    private ListCreditCardDetailsServiceOperationClient serviceOperationClient;
	@Override
	public Boolean perform(ListCreditCardDetailsLegacyDTO flowDto) {
		ListCreditCardDetailsBusinessRequest businessRequest = new ListCreditCardDetailsBusinessRequest();
		businessRequest.setPartyId(flowDto.getPartyId());
        ServiceOperationTask serviceTask = serviceOperationClient.execute(businessRequest);
        ServiceOperationResult serviceResult = serviceTask.getResult();
        if (serviceResult.isOk()) {
        	ListCreditCardDetailsBusinessResponse businessResponse = (ListCreditCardDetailsBusinessResponse)serviceResult.getOk().getResponse();
        	flowDto.setCardDetails(businessResponse.getCardDetails());
            return true;
        } else {
            log.error("Error in DNL_XRRB_CreditCardLegacy_001_ListCreditCardDetails_001 for requestId " + flowDto.getRequestId());
            throw new ActivityException(ListCreditCardDetailsServiceOperationClient.SERVICE_NAME, Math.abs(serviceResult.getError()
                    .getErrorCode()), // in case it's a negative error code which riaf does not accept
                    "Error in tibco service DNL_XRRB_CreditCardLegacy_001_ListCreditCardDetails_001 for requestId: " + flowDto.getRequestId(), serviceResult.getError()
                    .getException());
        }
	}

	public interface ListCreditCardDetailsLegacyDTO {
        String getPartyId();
        String getRequestId();
        void setCardDetails(List<CardDetail> cardDetails);
    }
}
