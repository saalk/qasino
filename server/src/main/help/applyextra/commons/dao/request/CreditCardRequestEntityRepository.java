package applyextra.commons.dao.request;

import applyextra.commons.configuration.RequestType;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.state.CreditCardsStateMachine;
import applyextra.commons.state.CreditCardsStateMachine.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CreditCardRequestEntityRepository extends JpaRepository<CreditCardRequestEntity, String> {

    CreditCardRequestEntity findCreditCardRequestEntityById(String id);

    //"select request from CreditCardRequestEntity as request left join request.account as acc where (((request.requestType = :requestType) and request.currentState = :currentState)) and (request.creationTime is not null) order by request.creationTime desc limit 1"
    public CreditCardRequestEntity findFirstByRequestTypeAndCurrentStateInAndAccount_IbanAndCreationTimeIsNotNullOrderByCreationTimeDesc(RequestType requestType, List<CreditCardsStateMachine.State> currentState, String Iban);
    
    public CreditCardRequestEntity findFirstByRequestTypeInAndCurrentStateInAndCustomerIdIsAndCreationTimeIsNotNullOrderByCreationTimeDesc(List<RequestType> requestType, List<CreditCardsStateMachine.State> currentState, String customerId);

    public List<CreditCardRequestEntity> findRequestsByRequestTypeInAndCurrentStateInAndCustomerIdIsAndCreationTimeIsNotNullAndCreationTimeAfter(List<RequestType> requestType, List<CreditCardsStateMachine.State> currentState, String customerId, Date startDate);
    
    /**
     * gets all the requests on the card in the cc_request table of a particular request type and states
     * @return list of requests in selected states
     */
    List<CreditCardRequestEntity> findCreditCardRequestByRequestorIdAndRequestTypeInAndCurrentStateIn(
            final String requestorId, final List<RequestType> requestTypesToCancel, final List<State> states);

    /**
     * gets all the requests on the card in the cc_request table of a particular request type and states
     * @return list of requests in selected states
     */
    List<CreditCardRequestEntity> findCreditCardRequestByCustomerIdAndRequestTypeInAndCurrentStateIn(
            final String customerId, final List<RequestType> requestTypesToCancel, final List<State> states);

    CreditCardRequestEntity findCreditCardRequestById(final String id);
    
    @Query("select request from CreditCardRequestEntity request, CreditCardAccountEntity account where request.requestType = :requestType and request.currentState IN (:states) and account.arrangementNumber = :arrangementNumber and request.account=account.id")
    List<CreditCardRequestEntity> findPreviousAndPendingCreditCardRequestByArrangement(@Param("arrangementNumber") String arrangementNumber, @Param("requestType") RequestType requestType, @Param("states") State[] states);

    List<CreditCardRequestEntity> findCreditCardRequestByRequestTypeInAndCurrentState(final RequestType[] requestTypes, final
    State states);

    List<CreditCardRequestEntity> findCreditCardRequestByRequestTypeAndCurrentStateIn(final RequestType requestType, final Set<State> states);

    List<CreditCardRequestEntity> findCreditCardRequestByRequestTypeIsAndCurrentStateIsAndPreviousStateIn(final RequestType requestType, final State error, final Set<State> previousStates);

    @Query("select request from CreditCardRequestEntity request, CreditCardAccountEntity account where request.requestType IN (:requestTypes) and request.currentState IN (:states) and account.accountNumber = :accountNumber and request.account=account.id")
    List<CreditCardRequestEntity> findPreviousAndPendingCreditCardRequestByAccountNumber(@Param("accountNumber") String arrangementNumber, @Param("requestTypes") RequestType[] requestTypes, @Param("states") State[] states);

    List<CreditCardRequestEntity> findRequestsByRequestTypeInAndCurrentStateInAndAccount_AccountNumberAndUpdateTimeIsNotNullAndUpdateTimeBeforeOrderByUpdateTimeDesc(@Param("requestTypes") List<RequestType> requestTypes, @Param("states") List<State> states, @Param("accountNumber") String accountNumber, @Param("date") Date date);

    List<CreditCardRequestEntity> findRequestsByRequestTypeInAndCurrentStateInAndAccount_IbanAndUpdateTimeIsNotNullAndUpdateTimeBeforeOrderByUpdateTimeDesc(@Param("requestTypes") List<RequestType> requestTypes, @Param("states") List<State> states, @Param("iban") String iban, @Param("date") Date date);

}
