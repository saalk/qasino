package applyextra.commons.components.retry;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.state.CreditCardsStateMachine;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Lazy
@Slf4j
public class CreditCardRequestRetryTask implements RetryTask<CreditCardRequestEntity>{

    @Resource
    private GraphiteRetryHelper graphiteRetryHelper;

    @Resource
    private RetryController retryController;

    @Resource
    private CreditcardRequestService requestService;

    @Override
    public boolean retry(CreditCardRequestEntity input) {
        if (input==null) {
            log.error("Cannot retry null request");
            return false;
        }
        log.warn("Retrying requestId={}",input.getId());
        boolean retried = false;
        try {
            retried = retryController.handleRetry(input);
        } catch (Exception e) {
            final CreditCardRequestEntity request = requestService.getCreditcardRequest(input.getId());
            if (request.getCurrentState()!=CreditCardsStateMachine.State.ERROR) {
                log.warn("Setting requestId={} to ERROR state after retry",input.getId());
                request.setPreviousState(request.getCurrentState());
                request.setCurrentState(CreditCardsStateMachine.State.ERROR);
                requestService.persistCreditcardRequest(request);
            }
            throw e;
        }
        // update graphite with retry count
        graphiteRetryHelper.tickRetriedRequestsCounter();
        return retried;
    }

    @Override
    public void giveUp(CreditCardRequestEntity request) {

        graphiteRetryHelper.tickTimedOutRequestsCounter();
    }

}
