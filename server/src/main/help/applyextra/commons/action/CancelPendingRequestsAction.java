package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.state.CreditCardsStateMachine.State;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * the pending requests in all states from INTITIATED to SUBMITTED must be cancelled before starting the new request
 * 
 * @author kq62io
 *
 */
@Slf4j
@Lazy
@Component
@Deprecated // No tests written, dont dare to change it, made a new one which closes based on customerId and not RequestID
public class CancelPendingRequestsAction
        implements Action<CancelPendingRequestsAction.CancelPendingRequestsActionDTO, EventOutput> {

    
    @Resource
    private CreditcardRequestService ccRequestService;

    @Override
    public EventOutput perform(final CancelPendingRequestsActionDTO flowDto) {
        String customerId = flowDto.getCreditcardRequest().getCustomerId();
        String creditCardNumber = flowDto.getCreditcardRequest()
                .getAccount()
                .getCreditCard()
                .getCreditCardNumber();

        log.debug("pending requests for credit playingcard selected: requestorId..." + customerId + "creditCardNumber..."
                + creditCardNumber + "requestType..." + flowDto.getCreditcardRequest().getRequestType());

        List<CreditCardRequestEntity> requestToUpdate = updatePendingRequestList(customerId, creditCardNumber, flowDto.requestTypesToCancel(), flowDto.getPendingStates());

        if (!requestToUpdate.isEmpty()) {
            log.debug("list of requests pending for selected playingcard: " + requestToUpdate.toString());
            ccRequestService.updateCreditCardRequestList(requestToUpdate, State.CANCELLED);
        }
        return EventOutput.success();
    }

    private List<CreditCardRequestEntity> updatePendingRequestList(String requestorId, String creditCardNumber,
            List<RequestType> requestTypesToCancel, final List<State> states) {
        List<CreditCardRequestEntity> requestToUpdate = new ArrayList<>();

        List<CreditCardRequestEntity> currentRequestsForRequestorByState = ccRequestService
                .getCurrentRequestsForRequestorByState(requestorId, requestTypesToCancel, states);
        log.debug("all the requests pending for selected playingcard: " + currentRequestsForRequestorByState);

        for (CreditCardRequestEntity cardsRequests : currentRequestsForRequestorByState) {
            if (cardsRequests.getAccount()
                    .getCreditCard()
                    .getCreditCardNumber() != null) {
                if (StringUtils.equalsIgnoreCase(cardsRequests.getAccount()
                        .getCreditCard()
                        .getCreditCardNumber(), creditCardNumber)) {
                    requestToUpdate.add(cardsRequests);
                }

            }
        }
        return requestToUpdate;
    }

    public interface CancelPendingRequestsActionDTO {

        List<State> pendingStates = new ArrayList<>();

        CreditCardRequestEntity getCreditcardRequest();
        
        List<RequestType> requestTypesToCancel();

        RequestType getContextType();
        
        default List<State> getPendingStates() {
            return pendingStates;
        }
    }
}
