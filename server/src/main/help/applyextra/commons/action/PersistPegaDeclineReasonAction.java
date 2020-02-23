package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.dao.request.ProcessSpecificDataService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.constant.ProcessReferenceKey;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.model.database.entity.ProcessSpecificDataEntity;
import applyextra.commons.orchestration.Action;
import applyextra.operations.dto.MessageDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * persist the reason for decline of a customers request created at pega hotel
 * @deprecated there is no need of saving the pega reasons. tThey are already in pega
 * 
 * @author kq62io
 *
 */
@Component
@Lazy
@Slf4j
@Deprecated
public class PersistPegaDeclineReasonAction
		implements Action<PersistPegaDeclineReasonAction.PopulatePegaDeclineReasonDTO, EventOutput.Result> {

	@Resource
	private ProcessSpecificDataService processSpecificDataService;

	public EventOutput.Result perform(final PopulatePegaDeclineReasonDTO reasonDTO) {
		MessageDTO messageDTO = reasonDTO.getMessageDTO();
		final CreditCardRequestEntity request = reasonDTO.getCurrentCreditCardRequest();

		if (messageDTO != null && request != null) {
			final ProcessSpecificDataEntity pegaDeclineReasonsEntity = new ProcessSpecificDataEntity();
			pegaDeclineReasonsEntity.setCreationTime(new Date());
			pegaDeclineReasonsEntity.setReferenceKey(ProcessReferenceKey.PEGA_DECLINE_REASON.name());
			pegaDeclineReasonsEntity.setReferenceValue(
					messageDTO.getRejectionCode() != null ? messageDTO.getRejectionCode() : "decline reason invalid");
			pegaDeclineReasonsEntity.setRequestType(request.getRequestType());
			pegaDeclineReasonsEntity.setRequest(request);
			processSpecificDataService.addProcessSpecificData(pegaDeclineReasonsEntity);
			log.debug("customer message payload: " + messageDTO.toString() + " and request id : " + request.getId());
		}
		return EventOutput.Result.SUCCESS;
	}

	public interface PopulatePegaDeclineReasonDTO {
		MessageDTO getMessageDTO();

		CreditCardRequestEntity getCurrentCreditCardRequest();
	}

}
