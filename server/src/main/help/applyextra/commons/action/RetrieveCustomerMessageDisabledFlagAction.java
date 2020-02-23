package applyextra.commons.action;

import applyextra.commons.model.database.constant.ProcessReferenceKey;
import applyextra.operations.event.PersistProcessSpecificValue;
import applyextra.operations.event.RetrieveProcessSpecificValueAction;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * checks if the customer message flag is disabled. if true, does not send
 * customer letters
 * 
 * @author kq62io
 *
 */
@Component
@Lazy
public class RetrieveCustomerMessageDisabledFlagAction extends RetrieveProcessSpecificValueAction {

	@Override
	public Boolean perform(PersistProcessSpecificValue.ProcessSpecificDataActionDTO dto) {
		if (dto.getProcessSpecificValue(
				dto.getProcessSpecificValue(ProcessReferenceKey.DISABLE_CUSTOMER_MESSAGE.name())) != null) {
			return super.perform(dto);
		}
		return true;
	}

	@Override
	protected String getKey() {
		return ProcessReferenceKey.DISABLE_CUSTOMER_MESSAGE.name();
	}
}
