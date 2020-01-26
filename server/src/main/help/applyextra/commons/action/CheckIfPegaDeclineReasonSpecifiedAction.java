package applyextra.commons.action;

import applyextra.commons.orchestration.Action;
import applyextra.operations.dto.MessageDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * checks if the pega request reason was others or not specified. if false, do
 * not send customer letters
 * @deprecated the implementation of {@link AbstractSendCustomerMessageAction#skip} should take
 * cares of it
 * 
 * @author kq62io
 *
 */
@Component
@Lazy
@Deprecated
public class CheckIfPegaDeclineReasonSpecifiedAction
		implements Action<CheckIfPegaDeclineReasonSpecifiedAction.CheckIfPegaDeclineReasonSpecifiedActionDTO, Boolean> {

	private static final String PEGA_DECLINE_REASON_OTHERS = "Others";

	@Override
	public Boolean perform(CheckIfPegaDeclineReasonSpecifiedAction.CheckIfPegaDeclineReasonSpecifiedActionDTO dto) {
		if (dto.getMessageDTO() != null && dto.getMessageDTO().getRejectionCode() != null
				&& PEGA_DECLINE_REASON_OTHERS.equalsIgnoreCase(dto.getMessageDTO().getRejectionCode())) {
			return false;
		}
		return true;
	}

	public interface CheckIfPegaDeclineReasonSpecifiedActionDTO {
		MessageDTO getMessageDTO();
	}

}
