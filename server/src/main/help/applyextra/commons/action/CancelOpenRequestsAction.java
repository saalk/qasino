package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.constant.CorrelationType;
import applyextra.commons.model.database.entity.CorrelationEntity;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.state.CreditCardsStateMachine.State;
import applyextra.commons.util.CorrelationEntityUtil;
import applyextra.operations.dto.CramDTO;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.SetCustomerRequestStatusServiceOperationClient;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.value.SetCustomerRequestStatusBusinessRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static applyextra.operations.dto.CramStatus.CANCELLED;

@Slf4j
@Component
public class CancelOpenRequestsAction implements Action<CancelOpenRequestsAction.CancelOpenRequestsActionDTO, EventOutput.Result> {

    @Resource private CreditcardRequestService ccRequestService;
    @Resource private SetCustomerRequestStatusServiceOperationClient setCustomerRequestStatusServiceOperationClient; // CRAM

    @Override
    public EventOutput.Result perform(CancelOpenRequestsActionDTO dto) {
        log.debug("Cancelling old requests which have not been authorized.");
        final CreditCardRequestEntity currentRequest = dto.getCreditcardRequest();
        final List<CreditCardRequestEntity> requestToCancel = ccRequestService
                .getCurrentRequestsForCustomerByState(
                        currentRequest.getCustomerId(),
                        dto.requestTypesToCancel(),
                        dto.getPendingStates()
                );

        return requestToCancel == null || requestToCancel.isEmpty()
                ? EventOutput.Result.SUCCESS
                : checkAndCancelRequests(dto, requestToCancel);
    }

    private EventOutput.Result checkAndCancelRequests(CancelOpenRequestsActionDTO dto, List<CreditCardRequestEntity> requestToCancel) {
        List<CreditCardRequestEntity> requestToUpdate = requestToCancel.stream()
                .filter(request -> !dto.getCreditcardRequest().getId().equals(request.getId()))
                .peek(request -> {
                    if (request.getCurrentState().equals(dto.getStatesToCancelCramRequests())) {
                        log.debug("list of requests pending for customer: " + request.getCustomerId());
                        cancelCramRequest(request);
                    }
                }).collect(Collectors.toList());

        if (!requestToUpdate.isEmpty()) {
            log.debug("size list of requests pending for customer: " + requestToUpdate.size());
            ccRequestService.updateCreditCardRequestList(requestToUpdate, State.CANCELLED);
        }
        return EventOutput.Result.SUCCESS;

    }

    private void cancelCramRequest(CreditCardRequestEntity request) {
        CorrelationEntity correlationEntity = CorrelationEntityUtil.getCorrelationByType(request.getCorrelations(), CorrelationType.CRAM_ID);
        if (correlationEntity != null) {
            log.info("Cancelling request at CRAM for requestId: {}", request.getId());
            final SetCustomerRequestStatusBusinessRequest businessRequest = new SetCustomerRequestStatusBusinessRequest();
            businessRequest.setReferenceId(correlationEntity.getExternalReference());
            businessRequest.setStatusType(CANCELLED.getStatusString());
            setCustomerRequestStatusServiceOperationClient.execute(businessRequest);
        }
    }


    public interface CancelOpenRequestsActionDTO {
        CreditCardRequestEntity getCreditcardRequest();
        List<RequestType> requestTypesToCancel();
        RequestType getContextType();
        CramDTO getCramDTO();

        default List<State> getPendingStates() { return new ArrayList<>(); }
        default State getStatesToCancelCramRequests() { return State.SUBMITTED; }
    }
}
