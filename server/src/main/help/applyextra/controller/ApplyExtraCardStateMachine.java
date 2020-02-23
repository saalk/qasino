package applyextra.controller;

import applyextra.actions.*;
import applyextra.commons.action.*;
import applyextra.commons.configuration.CreditCardAsyncConfiguration;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.event.AbstractFlowDTO;
import applyextra.commons.orchestration.CreditCardsEventHandler;
import applyextra.commons.orchestration.Event;
import applyextra.commons.orchestration.OrchestrationConfig;
import applyextra.operations.event.CheckEvent;
import applyextra.operations.event.InquireAccountEvent;
import applyextra.operations.event.SetCramStatusEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static applyextra.commons.event.EventOutput.Result.FAILURE;
import static applyextra.commons.event.EventOutput.Result.SUCCESS;
import static applyextra.commons.orchestration.EventEnum.*;
import static applyextra.commons.state.CreditCardsStateMachine.State.*;


public class ApplyExtraCardStateMachine implements CreditCardAsyncConfiguration.ASyncEventHandler {

    @Resource
    private ApplicationContext applicationContext;+

    public static final OrchestrationConfig applyExtraCardConfiguration = new OrchestrationConfig();

    static {
        applyExtraCardConfiguration
                .beforeEventPerform(LoadCreditcardRequestContextAction.class)
                .afterEventPerform(PersistCreditcardRequestAction.class)
                .retryRequest(RequestType.APPLY_EXTRA_CARD)
                .onResult(Exception.class, ERROR)
                .rethrowExceptions()
                .retryRequest(RequestType.APPLY_EXTRA_CARD);

        applyExtraCardConfiguration
                .onState(INITIATED)
                .onEvent(LIST)
                .perform(RetrieveListCreditCardAccountsAction.class)
                .onResult(FAILURE, ERROR)   //Move catches RunTime Exceptions. So we need this.
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(CheckMaxNumberOfCardsAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .perform(InquireAccountEvent.class)
                .perform(MapInquireAccountCardToRequestAction.class)
                .perform(RetrieveSecondaryAccountNumberAction.class)        //RetrieveSecondayAccountNumber if its not Regular Account
                .perform(StoreApplyExtraCardRequestContextAction.class)
                .perform(CancelOpenRequestsAction.class)
                .onResult(SUCCESS, LISTED);


        applyExtraCardConfiguration // Actions need to be the same as for state Checked and Declined so retry is possible.
                .onState(LISTED)
                .onEvent(CHECK)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(ApplyExtraCardBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, CHECKED);

        applyExtraCardConfiguration // Allows for rechecking CHECKED with different beneficiary
                .onState(CHECKED)
                .onEvent(CHECK)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(ApplyExtraCardBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, CHECKED);

        applyExtraCardConfiguration // Allows for rechecking DECLINED with different beneficiary
                .onState(DECLINED)
                .onEvent(CHECK)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(ApplyExtraCardBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, CHECKED);


        applyExtraCardConfiguration // Moving passed checked.
                .onState(CHECKED)
                .onEvent(VERIFY)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(ApplyExtraCardBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, VERIFIED);


        applyExtraCardConfiguration
                .onState(VERIFIED)
                .onEvent(SUBMIT)
                .perform(PopulateCRAMDataAction.class) //Commons class to populate CramDTO with DRAFT status
                .perform(CreateOrSetCramStatusAction.class) //Creates a new cram request
                .perform(StoreCramCorrelationAction.class) //Store the created cream correlation id
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, SUBMITTED);

        // FULFILLMENT
        applyExtraCardConfiguration
                .onState(SUBMITTED)
                .onEvent(AUTHORIZE)
                .perform(AuthorizeApplyExtraCardFulfillmentAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(PopulateCRAMDataAction.class)
                .perform(SetCramStatusEvent.class) //nosetcramstatusexecutiongaction?
                .onResult(FAILURE, ERROR)
                .onResult(SUCCESS, AUTHORIZED);

        applyExtraCardConfiguration
                .onState(AUTHORIZED)
                .retryIfError()
                .onEvent(VERIFY)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, REVERIFIED, CHECK);


        applyExtraCardConfiguration
                .onState(REVERIFIED)
                .retryIfError()
                .onEvent(CHECK)
                .perform(RetrieveListCreditCardAccountsAction.class)
                .onResult(FAILURE, ERROR)   //Move catches RunTime Exceptions. So we need this.
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(ApplyExtraCardBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, RECHECKED, EXECUTE);

        applyExtraCardConfiguration
                .onState(RECHECKED)
                .retryIfError()
                .onEvent(EXECUTE)
                .perform(InformRRBWrapperAction.class)
                .onResult(FAILURE, ERROR)
                .onResult(SUCCESS, EXECUTED, SEND_MESSAGE);

        applyExtraCardConfiguration.
                onState(FULFILLED_CUSTOMER)
                .retryIfError()
                .onEvent(EXECUTE)
                .perform(PopulateCRAMDataAction.class)
                .perform(SetCramStatusEvent.class) //nosetcramstatusfulfilledaction?
                .onResult(FAILURE, ERROR)
                .onResult(SUCCESS, FULFILLED);

        // RESET
        applyExtraCardConfiguration
                .onState(LISTED)
                .onEvent(RESET)
                .perform(ApplyExtraCardResetAction.class)
                .onResult(FAILURE, FAILED)
                .onResult(SUCCESS, LISTED);
        applyExtraCardConfiguration
                .onState(CHECKED)
                .onEvent(RESET)
                .perform(ApplyExtraCardResetAction.class)
                .onResult(FAILURE, FAILED)
                .onResult(SUCCESS, LISTED);
        applyExtraCardConfiguration
                .onState(VERIFIED)
                .onEvent(RESET)
                .perform(ApplyExtraCardResetAction.class)
                .onResult(FAILURE, FAILED)
                .onResult(SUCCESS, LISTED);
        applyExtraCardConfiguration
                .onState(SUBMITTED)
                .onEvent(RESET)
                .perform(PopulateCRAMDataAction.class) //Commons class to populate CramDTO with CANCELLED status
                .perform(SetCramStatusEvent.class) // Update the cram status/cancel the pending request
                .perform(ApplyExtraCardResetAction.class)
                .onResult(FAILURE, FAILED)
                .onResult(SUCCESS, LISTED);


        // Retry
        applyExtraCardConfiguration.onState(AUTHORIZED)
                .onEvent(RETRY)
                .transition(AUTHORIZED, VERIFY);
        applyExtraCardConfiguration.onState(REVERIFIED)
                .onEvent(RETRY)
                .transition(AUTHORIZED, VERIFY);
        applyExtraCardConfiguration.onState(RECHECKED)
                .onEvent(RETRY)
                .transition(RECHECKED, EXECUTE);
        applyExtraCardConfiguration.onState(EXECUTED)
                .onEvent(RETRY)
                .transition(EXECUTED, SEND_MESSAGE);
        applyExtraCardConfiguration.onState(FULFILLED_CUSTOMER)
                .onEvent(RETRY)
                .transition(FULFILLED_CUSTOMER,EXECUTE);


        // Reject / expired / cancelled
        applyExtraCardConfiguration.onState(SUBMITTED)
                .onEvent(REJECT)
                .transition(EXPIRED);
        applyExtraCardConfiguration.onState(SUBMITTED)
                .onEvent(CANCEL)
                .transition(CANCELLED);

    }

    private CreditCardsEventHandler eventHandler;


    @PostConstruct
    public void init() {
        eventHandler = new CreditCardsEventHandler(applyExtraCardConfiguration, applicationContext);
    }

    public <T extends AbstractFlowDTO> T handleEvent(Event event, T dto) {
        eventHandler.handleEvent(event, dto);
        return dto;
    }

}
