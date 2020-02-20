package applyextra.commons.components.retry;

import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static applyextra.commons.state.CreditCardsStateMachine.State.TIMEOUT;

@Component
@Lazy
public class CreditCardRequestRetryRecordProvider implements RetryRecordProvider<CreditCardRequestEntity> {

    @Lazy
    @Resource
    private CreditcardRequestService creditcardRequestService;

    @Resource
    private GraphiteRetryHelper graphiteRetryHelper;

    @Resource
    @Lazy
    private RetryController retryController;

    @Resource
    private CreditCardRequestRetryConfig retryConfig;

    private final Map<String, RetryRecord<CreditCardRequestEntity>> retryRecordsByRequestId = new HashMap();

    @Override
    public List<RetryRecord<CreditCardRequestEntity>> getRetryRecords() {
        final List<RetryRecord<CreditCardRequestEntity>> result = new ArrayList<>();
        for (RetryCriteria criteria: retryController.getRetryCriteria()) {
            if (criteria.getRequestTypeCriteria() == null) { continue; }
            result.addAll(toRetryRecords(loadPendingRequests(criteria)));
            result.addAll(toRetryRecords(loadRequestsInErrorState(criteria)));
        }
        return result;
    }

    private List<CreditCardRequestEntity> loadPendingRequests(final RetryCriteria criteria){
        if (criteria.getPendingRequestStateCriteria().isEmpty()){
            return Collections.EMPTY_LIST;
        }
        return creditcardRequestService
                .findRequestsPendingByRequestTypeAndCurrentStates( // load from db all requests
                        criteria.getRequestTypeCriteria(),          // with current state in
                        criteria.getPendingRequestStateCriteria()   // <- this list
                );
    }

    private List<CreditCardRequestEntity> loadRequestsInErrorState(final RetryCriteria criteria){
        if (criteria.getErrorAndPreviousStateCriteria().isEmpty()){
            return Collections.EMPTY_LIST;
        }
        return creditcardRequestService
                .findRequestsInErrorStateByRequestTypeAndPreviousState( // load from db all requests with current
                        criteria.getRequestTypeCriteria(),          // state error and previous state in
                        criteria.getErrorAndPreviousStateCriteria() // <- this list
                );
    }

    @Override
    public void removeRetryRecord(CreditCardRequestEntity input) {
        RetryRecord<CreditCardRequestEntity> retryRecord = retryRecordsByRequestId.get(input.getId());
        if (retryRecord.getNrOfRetriesAttempted() < retryConfig.getMaxRetrials()) {
            return;
        }

        input.setCurrentState(TIMEOUT);
        creditcardRequestService.persistCreditcardRequest(input);
        retryRecordsByRequestId.remove(input);
    }

    @Override
    public void updateRetryRecord(CreditCardRequestEntity entity) {
        if (retryConfig.isResetToPreviousState()) {
            entity.setCurrentState(entity.getPreviousState());
            creditcardRequestService.persistCreditcardRequest(entity);
        }
    }

    private List<RetryRecord<CreditCardRequestEntity>> toRetryRecords(
            final List<CreditCardRequestEntity> pendingRequests
    ) {
        final List<RetryRecord<CreditCardRequestEntity>> result = new ArrayList<>();
        final DateTime twoDaysAgo = DateTime.now().minusDays(2)
                    .withTime(0,0,0,0);
        for (final CreditCardRequestEntity requestEntity : pendingRequests) {
            final DateTime updatedTime = new DateTime(requestEntity.getUpdateTime());
            if (updatedTime.isBefore(twoDaysAgo)){
                continue;
            }
            if (retryRecordsByRequestId.containsKey(requestEntity.getId())) {
                final RetryRecord<CreditCardRequestEntity> retryRecord = retryRecordsByRequestId.get(requestEntity.getId());
                retryRecord.setRetryInput(requestEntity); // update input
                result.add(retryRecord);
            } else {
                // first retry, create new retry record
                final RetryRecord<CreditCardRequestEntity> newRetryRecord = new RetryRecord<>
                        (requestEntity, requestEntity.getUpdateTime(), 0);
                retryRecordsByRequestId.put(requestEntity.getId(), newRetryRecord);
                graphiteRetryHelper.tickPendingRequestsCounter();
                newRetryRecord.setRetryInput(requestEntity);
                result.add(newRetryRecord);
            }
        }
        return result;
    }

}
