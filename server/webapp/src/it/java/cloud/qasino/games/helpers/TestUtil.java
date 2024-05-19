//package cloud.qasino.card.httpcalls;
//
//import com.ing.cards.credit.database.requests.action.AbstractPersistProcessSpecificDataAction;
//import com.ing.cards.credit.database.requests.entity.CreditCardRequestEntity;
//import com.ing.cards.credit.database.requests.entity.ProcessSpecificDataEntity;
//import com.ing.cards.credit.database.requests.model.CreditCardsRequestTypes;
//import com.ing.cards.credit.database.requests.model.CreditCardsStates;
//import com.ing.cards.credit.database.requests.service.CreditCardsRequestService;
//import lombok.extern.slf4j.Slf4j;
//import org.awaitility.Awaitility;
//import org.awaitility.Duration;
//import org.awaitility.core.ConditionTimeoutException;
//
//import jakarta.annotation.Resource;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static com.ing.cards.credit.database.requests.model.CreditCardsStates.*;
//
//@Slf4j
//public class TestUtil {
//
//    @Resource
//    protected CreditCardsRequestService requestService;
//
//    final List<CreditCardsStates> endStates = List.of(FULFILLED, ERROR, REJECTED);
//
//    public CreditCardRequestEntity waitTillState(String requestId, CreditCardsStates expectedState, String customerId) {
//        if (requestId == null || customerId == null || expectedState == null) {
//            throw new RuntimeException("RequestId, customerId and expected state are mandatory");
//        }
//        if (requestService.getCreditCardRequest(requestId, customerId) == null) {
//            throw new RuntimeException(String.format("Request in database not found, for id %s", requestId));
//        }
//        waitForExecutionsToComplete(requestId, expectedState, customerId, null);
//
//        final CreditCardRequestEntity request = requestService.getCreditCardRequest(requestId, customerId);
//        log.warn("Waiting finished, current state is {}", request.getCurrentState());
//        if (!expectedState.equals(request.getCurrentState())) {
//            throw new RuntimeException(String.format(
//                    "Wrong end state reached, state is %s instead of %s",
//                    request.getCurrentState(), expectedState));
//        }
//        return request;
//    }
//
//    public CreditCardRequestEntity waitTillState(String requestId, CreditCardsStates expectedState, String customerId, Duration waitTime) {
//        if (requestId == null || customerId == null || expectedState == null) {
//            throw new RuntimeException("RequestId, customerId and expected state are mandatory");
//        }
//        if (requestService.getCreditCardRequest(requestId, customerId) == null) {
//            throw new RuntimeException(String.format("Request in database not found, for id %s", requestId));
//        }
//        waitForExecutionsToComplete(requestId, expectedState, customerId, waitTime);
//
//        final CreditCardRequestEntity request = requestService.getCreditCardRequest(requestId, customerId);
//        log.warn("Waiting finished, current state is {}", request.getCurrentState());
//        if (!expectedState.equals(request.getCurrentState())) {
//            throw new RuntimeException(String.format(
//                    "Wrong end state reached, state is %s instead of %s",
//                    request.getCurrentState(), expectedState));
//        }
//        return request;
//    }
//
//    private void waitForExecutionsToComplete(String requestId, CreditCardsStates expectedState, String customerId, Duration time) {
//        var executionWaitTime = (time == null) ? Duration.ONE_MINUTE : time;
//        try {
//            Awaitility.await()
//                    .atMost(executionWaitTime)
//                    .pollInterval(new Duration(250, TimeUnit.MILLISECONDS))
//                    .until(() -> {
//                        final CreditCardsStates currentState = requestService.getCreditCardRequest(requestId, customerId).getCurrentState();
//                        log.warn("Current state of request is {}", currentState);
//                        return expectedState.equals(currentState) || endStates.contains(currentState);
//                    });
//        } catch (ConditionTimeoutException e) {
//            log.warn("No end state reached, current state: {}",
//                    requestService.getCreditCardRequest(requestId, customerId).getCurrentState());
//            throw e;
//        }
//    }
//
//
//    public CreditCardRequestEntity updateAccountInformationInDatabase(CreditCardRequestEntity entity,
//                                                                      String subArrangementNumber,
//                                                                      String arrangementNumber,
//                                                                      String iban) {
//        entity.getAccount().setAccountNumber(subArrangementNumber);
//        entity.getAccount().setArrangementNumber("P" + arrangementNumber);
//        entity.getAccount().setSubArrangmentNumber("M" + subArrangementNumber);
//        entity.getAccount().setIban(iban);
//        return requestService.updateRequest(entity);
//    }
//
//    public void addProcessSpecificDataToRequest(CreditCardRequestEntity request, AbstractPersistProcessSpecificDataAction.ProcessReferenceKey key, String value) {
//        ProcessSpecificDataEntity processSpecificDataEntity = new ProcessSpecificDataEntity();
//        processSpecificDataEntity.setRequest(request);
//        processSpecificDataEntity.setRequestType(CreditCardsRequestTypes.APPLY_CARD_SERVICE);
//        processSpecificDataEntity.setReferenceKey(key.name());
//        processSpecificDataEntity.setReferenceValue(value);
//        request.getProcessSpecificData().add(processSpecificDataEntity);
//    }
//
//    public void changeCreationTime(CreditCardRequestEntity requestInDatabase, LocalDateTime creationTime) {
//        requestInDatabase.setCreationTime(creationTime);
//        requestService.updateRequest(requestInDatabase);
//    }
//
//    public CreditCardRequestEntity fetchRequestFromDatabase(String requestId, String customerId) {
//        return requestService.getCreditCardRequest(requestId, customerId);
//    }
//
//    public void cancelAllRequestsForCustomer(String customerId) {
//        List<CreditCardRequestEntity> preAuthorizedRequests = requestService.getPreAuthorizedRequestsForCustomerForRequestType(customerId, CreditCardsRequestTypes.APPLY_CARD_SERVICE);
//        if (preAuthorizedRequests != null && !preAuthorizedRequests.isEmpty()) {
//            log.warn("pre authorized requests exist for this request id");
//            requestService.batchUpdateCreditCardRequestList(preAuthorizedRequests, CreditCardsStates.CANCELLED);
//        }
//        List<CreditCardRequestEntity> postAuthorizedRequests = requestService.getPostAuthorizedRequestsForCustomer(customerId);
//        if (postAuthorizedRequests != null && !postAuthorizedRequests.isEmpty()) {
//            log.warn("post authorized requests exist for this request id");
//            requestService.batchUpdateCreditCardRequestList(postAuthorizedRequests, CreditCardsStates.CANCELLED);
//        }
//    }
//}
//
//
//
//
//
