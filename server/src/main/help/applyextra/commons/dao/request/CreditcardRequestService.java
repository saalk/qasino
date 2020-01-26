package applyextra.commons.dao.request;

import com.ing.api.toolkit.trust.context.ChannelContext;
import com.ing.api.toolkit.trust.context.Customer;
import com.ing.api.toolkit.trust.context.Employee;
import lombok.extern.slf4j.Slf4j;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.event.AbstractCreditcardFlowDTO;
import applyextra.commons.model.database.entity.AccountStatusEntity;
import applyextra.commons.model.database.entity.CreditCardAccountEntity;
import applyextra.commons.model.database.entity.CreditCardEntity;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.state.CreditCardsStateMachine.State;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.OptimisticLockException;
import javax.validation.constraints.NotNull;
import java.util.*;

@Component
@Slf4j
public class CreditcardRequestService {
	@Resource
	private CreditCardRequestEntityRepository repository;

	//NO Transactional
	public CreditCardRequestEntity createCreditcardRequest(AbstractCreditcardFlowDTO dto) {
		CreditCardRequestEntity requestEntity = new CreditCardRequestEntity();
		requestEntity.setId(UUID.randomUUID().toString());
		requestEntity.setSequenceNumber(0);
		requestEntity.setCurrentState(State.INITIATED);
        if (dto.getCustomerId() != null) {
            requestEntity.setCustomerId(dto.getCustomerId());
        }
        if (dto.getContextType() != null) {
		    requestEntity.setRequestType(dto.getContextType());
        }

        CreditCardAccountEntity account = new CreditCardAccountEntity();
        account.setCreditCard(dto.getCreditcardRequest() == null ? new CreditCardEntity() : dto.getCreditcardRequest().getAccount().getCreditCard());
        account.setAccountStatus(dto.getCreditcardRequest() == null ? new AccountStatusEntity() : dto.getCreditcardRequest().getAccount().getAccountStatus());
        requestEntity.setAccount(account);

        // save/get is resolved in two different transactions to don't overwrite oracle triggers changes
        persistCreditcardRequest(requestEntity);
		requestEntity = getCreditcardRequest(requestEntity.getId());

		log.debug("Created RequestContext: id: " + requestEntity.getId() + " sequence: " + requestEntity.getSequenceNumber());
		return requestEntity;
	}

    @Transactional(readOnly = true)
	public CreditCardRequestEntity getCreditcardRequest(final String id) {
		CreditCardRequestEntity request = repository.findCreditCardRequestEntityById(id);
        if (request.getFinancialData() != null) {
            request.getFinancialData().getExtraLoanEntities().size();
        }
        return request;
	}

    @Transactional(readOnly = true)
    public CreditCardRequestEntity getLastAuthorizedRequestByIban(RequestType requestType, String iban) {
        CreditCardRequestEntity request = repository.findFirstByRequestTypeAndCurrentStateInAndAccount_IbanAndCreationTimeIsNotNullOrderByCreationTimeDesc(requestType, Arrays.asList(State.AUTHORIZED, State.RECHECKED, State.POST_RECHECKED, State.FULFILLED, State.PEGA_AWAITED), iban);
        return request;
    }

    @Transactional
	public void persistCreditcardRequest(CreditCardRequestEntity entity){
		repository.save(entity);
	}

	public Boolean verifyRequest(final String requestId, final ChannelContext channelContext) {
		final CreditCardRequestEntity request = repository.findCreditCardRequestEntityById(requestId);
		if (request != null) {
			final Customer customer = channelContext.getCustomers()
					.get()
					.getActiveCustomer();
			if (customer == null) {
				throw new IllegalStateException(
						"Active customer of channelcontext is empty, please check the headers of the request.");
			}
			if (request.getRequestorId() == null) {
				throw new IllegalStateException("RequestorId is null, please check the initialize state.");
			}
			if (channelContext.getEmployee().isPresent()){
				Employee employee = channelContext.getEmployee().get();
				return request.getRequestorId().equals(employee.getEmployeeId());
			}
			return request.getRequestorId()
					.equals(customer.getCustomerId());
		}
		return false;
	}

	@Transactional(readOnly = true)
    public List<CreditCardRequestEntity> getCurrentRequestsForRequestorByState(final String requestorId, final List<RequestType> requestTypesToCancel, final List<State> states) {
        return repository.findCreditCardRequestByRequestorIdAndRequestTypeInAndCurrentStateIn(requestorId,requestTypesToCancel, states);
    }

    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> getCurrentRequestsForCustomerByState(final String customerId, final List<RequestType> requestTypesToCancel, final List<State> states) {
        return repository.findCreditCardRequestByCustomerIdAndRequestTypeInAndCurrentStateIn(customerId,requestTypesToCancel, states);
    }
    
    /**
     * update all the request to the passed in preferred state in DB
     * @return list of all updated requests
     */
    public List<CreditCardRequestEntity> updateCreditCardRequestList(final List<CreditCardRequestEntity> requestList, final State newUpdatedState){
            final List<CreditCardRequestEntity> result = new ArrayList<>();
            for (int i = 0; i < requestList.size(); i++) {
                final CreditCardRequestEntity request = requestList.get(i);
                request.setPreviousState(request.getCurrentState());
                request.setCurrentState(newUpdatedState);
                try {
                    requestList.set(i, updateRequest(request));
                } catch (final ConcurrentModificationException | IllegalStateException | JpaSystemException e) {
                    log.warn("Failure to update status", e);
                    result.add(request);
                }
            }
            return result;      
    }
    
    
    @Transactional
    public CreditCardRequestEntity updateRequest(final CreditCardRequestEntity currentRequest) {
        try {
            validateExistingRequest(currentRequest);
            final CreditCardRequestEntity resultRequest = repository.save(currentRequest);
            log.debug("Updated RequestContext: id: " + resultRequest.getId() + " requstor.id: " + resultRequest.getRequestorId()
                    + " sequence: " + resultRequest.getSequenceNumber());
            return resultRequest;
        } catch (final OptimisticLockException exception) {
            final String exceptionMsg = "Request context cannot be updated: a concurrent process updated request "
                    + "context for requestId: " + currentRequest.getId();
            throw new ConcurrencyFailureException(exceptionMsg, exception);
        }
    }

    public void validateExistingRequest(final CreditCardRequestEntity currentRequest) {
    	CreditCardRequestEntity findCreditCardRequestById = repository.findCreditCardRequestById(currentRequest.getId());
        if (findCreditCardRequestById == null) {
            throw new IllegalStateException("request with id: " + currentRequest.getId() + " does not exist and cannot be updated");
        }
    }

    /**
     * Get a list of previous requests based on the given selected states before authorised state
     * 
     * @return list of previous requests
     */
    public List<CreditCardRequestEntity> getPreviousRequestsForCardHolder(@NotNull final CreditCardRequestEntity currentCreditCardRequest, @NotNull final State[] selectedStates, @NotNull RequestType requestType) {
        return repository.findPreviousAndPendingCreditCardRequestByArrangement(currentCreditCardRequest.getAccount().getArrangementNumber(), requestType, selectedStates);

    }
    
    /**
     * Get a list of requests based on the given selected states 
     * 
     * @return list of pending requests
     */
    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> getPendingRequestsForCardHolder(@NotNull final CreditCardRequestEntity currentCreditCardRequest, @NotNull final State[] selectedStates) {
        final List<CreditCardRequestEntity> resultList;
        resultList = getPendingRequests(currentCreditCardRequest, selectedStates);
        resultList.remove(findRequestInList(currentCreditCardRequest, resultList));
        return resultList;
    }

    private CreditCardRequestEntity findRequestInList( final CreditCardRequestEntity currentCreditCardRequest, final List<CreditCardRequestEntity> resultList) {
        for (CreditCardRequestEntity pendingRequest : resultList) {
            if (pendingRequest.getId().equals(currentCreditCardRequest.getId())) {
                return pendingRequest;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> getPendingRequests(final CreditCardRequestEntity currentCreditCardRequest,final State[] endStates) {
        return repository.findPreviousAndPendingCreditCardRequestByArrangement(currentCreditCardRequest.getAccount().getArrangementNumber(), currentCreditCardRequest.getRequestType(), endStates);
    }

    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> findRequestsPendingByRequestTypesAndCurrentState(final RequestType[] requestTypes,
                                                                                          final State state) {
        return repository.findCreditCardRequestByRequestTypeInAndCurrentState(requestTypes, state);
    }

    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> findRequestsPendingByRequestTypeAndCurrentStates(
            final RequestType requestType,
            final Set<State> states) {
        return repository.findCreditCardRequestByRequestTypeAndCurrentStateIn(requestType, states);
    }

    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> findRequestsInErrorStateByRequestTypeAndPreviousState(
            final RequestType requestType,
            final Set<State> previousStates) {
        return repository.findCreditCardRequestByRequestTypeIsAndCurrentStateIsAndPreviousStateIn(requestType, State.ERROR, previousStates);
    }
    
	/**
	 * Returns the last requests belonging to the customer where: the status is present in the array, the request type is
	 * present in the array and the creation time is greater than start date.
	 *
	 * @param requestTypes List of request types to filter
	 * @param states List of states to filter
	 * @param customerId id of the customer
	 * @return null if no request is found
	 */
	@Transactional(readOnly = true)
	public CreditCardRequestEntity getLastRequestByCustomerId(final String customerId, final List<RequestType> requestTypes, final List<State> states) {
		CreditCardRequestEntity request = repository.findFirstByRequestTypeInAndCurrentStateInAndCustomerIdIsAndCreationTimeIsNotNullOrderByCreationTimeDesc(requestTypes, states, customerId);
		return request;
	}

    /**
     * Returns all the requests belonging to the customer where: the status is present in the array, the request type is
     * present in the array and the creation time is greater than start date.
     *
     * @param customerId id of the customer
     * @param requestTypes List of request types to filter
     * @param states List of states to filter
     * @param startDate date to use as a lower bound
     * @return empty list if no request is found
     */
	@Transactional(readOnly = true)
    public List<CreditCardRequestEntity> getHistoricalFulfilledRequests(final String customerId, final List<RequestType> requestTypes, final List<State> states, final Date startDate) {
        return repository.findRequestsByRequestTypeInAndCurrentStateInAndCustomerIdIsAndCreationTimeIsNotNullAndCreationTimeAfter(requestTypes,states,customerId,startDate);
    }
	
    /**
     * Get a list of requests based on the given selected states, request types and account number
     * 
     * @return list of pending requests
     */
    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> getPendingRequestsForAccountNumber(
            @NotNull final CreditCardRequestEntity currentCreditCardRequest, final RequestType[] requestTypes,
            @NotNull final State[] selectedStates) {
        final List<CreditCardRequestEntity> resultList;
        resultList = getPendingRequestsForThisAccountNumber(currentCreditCardRequest, requestTypes, selectedStates);
        resultList.remove(findRequestInList(currentCreditCardRequest, resultList));
        return resultList;
    }

    private List<CreditCardRequestEntity> getPendingRequestsForThisAccountNumber(CreditCardRequestEntity currentCreditCardRequest,
            final RequestType[] requestTypes, State[] selectedStates) {
        return repository.findPreviousAndPendingCreditCardRequestByAccountNumber(currentCreditCardRequest.getAccount()
                .getAccountNumber(), requestTypes, selectedStates);
    }

    /**
     * Returns previous requests belonging to the customer by account number where: the status is present in the array, the request type is
     * present in the array and the update time is before given date.
     * @param requestTypes
     * @param states
     * @param arrangementNumber
     * @param date
     * @return
     */

    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> getHistoricalRequestsByAccountNumber(List<RequestType> requestTypes, List<State> states, String arrangementNumber, Date date) {
        return repository.findRequestsByRequestTypeInAndCurrentStateInAndAccount_AccountNumberAndUpdateTimeIsNotNullAndUpdateTimeBeforeOrderByUpdateTimeDesc(requestTypes, states, arrangementNumber, date);
    }

    /**
     * Returns previous requests belonging to the customer by iban where: the status is present in the array, the request type is
     * present in the array and the update time is before given date.
     * @param requestTypes
     * @param states
     * @param iban
     * @param date
     * @return
     */
    @Transactional(readOnly = true)
    public List<CreditCardRequestEntity> getHistoricalRequestsByIban(List<RequestType> requestTypes, List<State> states, String iban, Date date) {
        return repository.findRequestsByRequestTypeInAndCurrentStateInAndAccount_IbanAndUpdateTimeIsNotNullAndUpdateTimeBeforeOrderByUpdateTimeDesc( requestTypes, states, iban, date);
    }

}
