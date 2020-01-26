package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.dao.request.ProcessSpecificDataService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.constant.ProcessReferenceKey;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.model.database.entity.ProcessSpecificDataEntity;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * persist the customer message flag which enables back office to do changes for
 * cards without sending communication to customer
 * 
 * @author kq62io
 *
 */
@Component
@Lazy
@Slf4j
public class PersistDisableCustomerMessageFlagAction
		implements Action<PersistDisableCustomerMessageFlagAction.DisableCustomerMessageFlagDTO, EventOutput.Result> {

	@Resource
	private ProcessSpecificDataService processSpecificDataService;

	public EventOutput.Result perform(final DisableCustomerMessageFlagDTO dto) {
		final CreditCardRequestEntity currentRequest = dto.getCurrentCreditCardRequest();
		final boolean disableCustomerMessages = dto.isDisableCustomerMessages();
		if (disableCustomerMessages && currentRequest != null) {
			populateDisableCustomerMessage(currentRequest, disableCustomerMessages);
			return EventOutput.Result.SUCCESS;
		} else {
			populateDisableCustomerMessage(currentRequest, disableCustomerMessages);
			return EventOutput.Result.SUCCESS;
		}

	}

	private void populateDisableCustomerMessage(final CreditCardRequestEntity currentRequest,
			final boolean disableCustomerMessages) {
		final ProcessSpecificDataEntity processSpecificData = new ProcessSpecificDataEntity();
		processSpecificData.setCreationTime(new Date());
		processSpecificData.setReferenceKey(ProcessReferenceKey.DISABLE_CUSTOMER_MESSAGE.name());
		processSpecificData.setReferenceValue(String.valueOf(disableCustomerMessages));
		processSpecificData.setRequestType(currentRequest.getRequestType());
		processSpecificData.setRequest(currentRequest);
		processSpecificDataService.addProcessSpecificData(processSpecificData);
		log.debug("disable customer message flag is : " + disableCustomerMessages + " and request id : "
				+ currentRequest.getId());
	}

	public interface DisableCustomerMessageFlagDTO {
		boolean isDisableCustomerMessages();

		CreditCardRequestEntity getCurrentCreditCardRequest();
	}

}
