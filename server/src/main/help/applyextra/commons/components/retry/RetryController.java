package applyextra.commons.components.retry;

import applyextra.commons.model.database.entity.CreditCardRequestEntity;

import java.util.List;

/**
 * Implement this interface in a <i>Component</i> in order to use the retry feature in your project.
 *
 */
public interface RetryController {

    /**
     * This method will be called by the scheduler at every retry.
     *
     * The implementing class should trigger the retry move from this method. It should look like this:
     *
     * public void handleRetry(CreditCardRequestEntity request){
     *     ChangeRepaymentOnDTO input = new ChangeRepaymentOnDTO();
     *     input.setRequestId(request.getId());
     *     changeRepaymentController.handleEvent(RETRY, input);
     * }
     *
     * @param input flow dto required to handle the retry
     */
    boolean handleRetry(final CreditCardRequestEntity input);


    List<RetryCriteria> getRetryCriteria();
}
