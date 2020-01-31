package cloud.qasino.card.action;

import applyextra.commons.configuration.RequestType;
import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.state.CreditCardsStateMachine;
import nl.ing.riaf.core.util.JNDIUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Action that load the customers Historical Requests for the Given request types.
 * This will filter out the current request the customer is doing.
 */
@Lazy
@Component
public class LoadCustomersHistoricalRequestsAction implements Action<LoadCustomersHistoricalRequestsAction.LoadCustomersHistoricalRequestsActionDTO, EventOutput.Result> {

    @Resource
    private CreditcardRequestService requestService;
    @Resource
    private JNDIUtil jndiUtil;

    private final static String JNDI_PARAMETER_HISTORICAL_CONTEXT = "threshold/pendingrequests/daysInPast";

    @Override
    public EventOutput.Result perform(LoadCustomersHistoricalRequestsActionDTO dto) {

        List<CreditCardRequestEntity> allLastRequests = requestService.getCurrentRequestsForCustomerByState(
                dto.getCustomerId(),
                dto.getRequestTypes(),
                dto.getLastFulfilledRequestStates()
        );
        if (allLastRequests == null || allLastRequests.isEmpty()) { return EventOutput.Result.SUCCESS; }

        final List<CreditCardRequestEntity> requestsFromLastDay = getRequestFromLastDay(allLastRequests, dto.getRequestId());
        dto.getLastRequests().addAll(requestsFromLastDay);
        return EventOutput.Result.SUCCESS;
    }

    private List<CreditCardRequestEntity> getRequestFromLastDay (final List<CreditCardRequestEntity> allRequests,
                                                                 final String requestId) {
        final long daysToSubtract = jndiUtil.getJndiValueWithDefault(JNDI_PARAMETER_HISTORICAL_CONTEXT, 1);
        final LocalDateTime thresholdTime = LocalDateTime.now().minusDays(daysToSubtract);
        return allRequests.stream()
                .filter(lastRequest ->
                        LocalDateTime.ofInstant(lastRequest.getUpdateTime().toInstant(), ZoneId.systemDefault())
                        .isAfter(thresholdTime))
                .filter(lastRequest -> !requestId.equals(lastRequest.getId()))
                .collect(Collectors.toList());
    }

    public interface LoadCustomersHistoricalRequestsActionDTO {
        String getCustomerId();
        List<RequestType> getRequestTypes();
        List<CreditCardsStateMachine.State> getLastFulfilledRequestStates();
        List<CreditCardRequestEntity> getLastRequests();
        String getRequestId(); // Current RequestId.
    }
}
