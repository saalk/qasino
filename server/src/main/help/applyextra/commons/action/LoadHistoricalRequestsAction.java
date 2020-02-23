package applyextra.commons.action;

import applyextra.commons.configuration.RequestType;
import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.state.CreditCardsStateMachine;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This suppliedMove is meant to load all the requests that a customer has done in the current year.
 *
 */
@Component
@Lazy
public class LoadHistoricalRequestsAction implements Action<LoadHistoricalRequestsAction.LoadHistoricalRequestsDTO,EventOutput.Result>{

    @Resource
    private CreditcardRequestService requestService;

    @Override
    public EventOutput.Result perform(LoadHistoricalRequestsDTO dto) {
        final Calendar calendar = Calendar.getInstance();
        CreditCardRequestEntity lastRequest = requestService.getLastRequestByCustomerId(
                dto.getCustomerId(), dto.getLastFulfilledRequestTypes(), dto.getLastFulfilledRequestStates());
        dto.setLastFulfilledRequest(lastRequest);
        calendar.set(calendar.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        final Date beginYear = calendar.getTime();
        List<CreditCardRequestEntity> historicalFulfilledRequests = requestService.getHistoricalFulfilledRequests(
                dto.getCustomerId(), dto.getHistoricalFulfilledRequestTypes(), dto.getHistoricalFulfilledRequestStates(), beginYear);
        if (historicalFulfilledRequests != null && !historicalFulfilledRequests.isEmpty())
            dto.getHistoricalFulfilledRequests().addAll(historicalFulfilledRequests);
        return EventOutput.Result.SUCCESS;
    }

    public interface LoadHistoricalRequestsDTO{
        /**
         * Customer Id
         * @return Customer id
         */
        String getCustomerId();

        /**
         * Possible types of the last authorized requests
         * @return Possible types of the last authorized requests
         */
        List<RequestType> getLastFulfilledRequestTypes();

        /**
         * Possible states of the last authorized requests
         * @return Possible states of the last authorized requests
         */
        List<CreditCardsStateMachine.State> getLastFulfilledRequestStates();

        void setLastFulfilledRequest(CreditCardRequestEntity lastRequest);

        CreditCardRequestEntity getLastFulfilledRequest();
        /**
         * List of states to use as filter to load all the requests that a customer has done in the current year.
         * @return List of states to use as filter for historical requests
         */
        List<CreditCardsStateMachine.State> getHistoricalFulfilledRequestStates();

        /**
         * List of types to use as filter to load all the requests that a customer has done in the current year.
         * @return List of types to use as filter for historical requests
         */
        List<RequestType> getHistoricalFulfilledRequestTypes();

        /**
         * List where the historical requests will be added.
         * Should be initialized as empty list before calling the suppliedMove.
         * @return List of historical requests
         */
        List<CreditCardRequestEntity> getHistoricalFulfilledRequests();
    }
}
