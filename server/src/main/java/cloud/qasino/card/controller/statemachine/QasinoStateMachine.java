package cloud.qasino.card.controller.statemachine;

import cloud.qasino.card.controller.statemachine.configuration.QasinoAsyncConfiguration;
import cloud.qasino.card.dto.event.AbstractFlowDTO;
import cloud.qasino.card.orchestration.OrchestrationConfig;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static cloud.qasino.card.controller.statemachine.EventState.ERROR;

public class QasinoStateMachine implements QasinoAsyncConfiguration.ASyncEventHandler {

    @Resource
    private ApplicationContext applicationContext;

    public static final OrchestrationConfig config = new OrchestrationConfig();

static {
        // @formatter:off
        // start on player page
        config.configure(EventState.INIT) // no events yes
        .permit(EventTrigger.START, EventState.NEW)
        ;


        config.configure(EventState.NEW) // new/first turn started
        .permit(EventTrigger.START, EventState.NEW)

        .permit(EventTrigger.STOP, EventState.HIGHER)
        .permit(EventTrigger.STOP, EventState.LOWER);

        config.configure(EventState.DEALT)
        .permit(EventTrigger.HIGHER, EventState.HIGHER)
        .permit(EventTrigger.HIGHER, EventState.LOWER)
        .permit(EventTrigger.LOWER, EventState.HIGHER)
        .permit(EventTrigger.LOWER, EventState.LOWER);

        config.configure(EventState.HIGHER)
        .permitReentry(EventTrigger.HIGHER)
        .permitReentry(EventTrigger.LOWER)
        .permitReentry(EventTrigger.STOP)
        .permit(EventTrigger.CRASH, EventState.ERROR);

        config.configure(EventState.HIGHER)
        .permitReentry(EventTrigger.DEAL)
        .permit(EventTrigger.WINNER, EventState.FINISHED)
        .permit(EventTrigger.CRASH, EventState.ERROR);

        config.configure(EventState.LOWER)
        .permit(EventTrigger.DEAL, EventState.PLAYING); // allows continue other players!

        // @formatter:on
        }
        /*
        qasinoConfiguration
                .beforeEventPerform(LoadQasinoRequestContextAction.class)
                .afterEventPerform(PersistQasinoRequestAction.class)
                .retryRequest(RequestType.APPLY_EXTRA_CARD)
                .onResult(Exception.class, ERROR)
                .rethrowExceptions();
                .retryRequest(RequestType.APPLY_EXTRA_CARD);

        qasinoConfiguration
                .onState(INITIATED)
                .onEvent(LIST)
                .perform(RetrieveListQasinoAccountsAction.class)
                .onResult(FAILURE, ERROR)   //Action catches RunTime Exceptions. So we need this.
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(CheckMaxNumberOfCardsAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .perform(InquireAccountEvent.class)
                .perform(MapInquireAccountCardToRequestAction.class)
                .perform(RetrieveSecondaryAccountNumberAction.class)        //RetrieveSecondayAccountNumber if its not Regular Account
                .perform(StoreQasinoRequestContextAction.class)
                .perform(CancelOpenRequestsAction.class)
                .onResult(SUCCESS, LISTED);


        qasinoConfiguration // Actions need to be the same as for state Checked and Declined so retry is possible.
                .onState(LISTED)
                .onEvent(CHECK)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(QasinoBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, CHECKED);

        qasinoConfiguration // Allows for rechecking CHECKED with different beneficiary
                .onState(CHECKED)
                .onEvent(CHECK)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(QasinoBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, CHECKED);

        qasinoConfiguration // Allows for rechecking DECLINED with different beneficiary
                .onState(DECLINED)
                .onEvent(CHECK)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(QasinoBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, CHECKED);


        qasinoConfiguration // Moving passed checked.
                .onState(CHECKED)
                .onEvent(VERIFY)
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(IndividualsAllForBeneficiaryAction.class)                            // to get date of birth of beneficiary
                .perform(LoadCustomersHistoricalRequestsAction.class)           // to get previous requests for customer
                .perform(VerifyEventWrapperForCustomerAndBeneficiaryAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(QasinoBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, VERIFIED);


        qasinoConfiguration
                .onState(VERIFIED)
                .onEvent(SUBMIT)
                .perform(PopulateCRAMDataAction.class) //Commons class to populate CramDTO with DRAFT status
                .perform(CreateOrSetCramStatusAction.class) //Creates a new cram request
                .perform(StoreCramCorrelationAction.class) //Store the created cream correlation id
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, SUBMITTED);

        // FULFILLMENT
        qasinoConfiguration
                .onState(SUBMITTED)
                .onEvent(AUTHORIZE)
                .perform(AuthorizeQasinoFulfillmentAction.class)
                .onResult(FAILURE, DECLINED)
                .perform(PopulateCRAMDataAction.class)
                .perform(SetCramStatusEvent.class) //nosetcramstatusexecutiongaction?
                .onResult(FAILURE, ERROR)
                .onResult(SUCCESS, AUTHORIZED);

        qasinoConfiguration
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


        qasinoConfiguration
                .onState(REVERIFIED)
                .retryIfError()
                .onEvent(CHECK)
                .perform(RetrieveListQasinoAccountsAction.class)
                .onResult(FAILURE, ERROR)   //Action catches RunTime Exceptions. So we need this.
                .perform(ListArrangementsAction.class)   // TODO: replace with gArrangementsAPI once packagetype is in MDM
                .perform(DeterminePackageFromListArrangementsResponse.class)
                .perform(QasinoBusinessRulesAction.class)
                .perform(CheckEvent.class)
                .onResult(FAILURE, DECLINED)
                .onResult(SUCCESS, RECHECKED, EXECUTE);

        qasinoConfiguration
                .onState(RECHECKED)
                .retryIfError()
                .onEvent(EXECUTE)
                .perform(InformRRBWrapperAction.class)
                .onResult(FAILURE, ERROR)
                .onResult(SUCCESS, EXECUTED, SEND_MESSAGE);

        qasinoConfiguration.
                onState(FULFILLED_CUSTOMER)
                .retryIfError()
                .onEvent(EXECUTE)
                .perform(PopulateCRAMDataAction.class)
                .perform(SetCramStatusEvent.class) //nosetcramstatusfulfilledaction?
                .onResult(FAILURE, ERROR)
                .onResult(SUCCESS, FULFILLED);

        // RESET
        qasinoConfiguration
                .onState(LISTED)
                .onEvent(RESET)
                .perform(QasinoResetAction.class)
                .onResult(FAILURE, FAILED)
                .onResult(SUCCESS, LISTED);
        qasinoConfiguration
                .onState(CHECKED)
                .onEvent(RESET)
                .perform(QasinoResetAction.class)
                .onResult(FAILURE, FAILED)
                .onResult(SUCCESS, LISTED);
        qasinoConfiguration
                .onState(VERIFIED)
                .onEvent(RESET)
                .perform(QasinoResetAction.class)
                .onResult(FAILURE, FAILED)
                .onResult(SUCCESS, LISTED);
        qasinoConfiguration
                .onState(SUBMITTED)
                .onEvent(RESET)
                .perform(PopulateCRAMDataAction.class) //Commons class to populate CramDTO with CANCELLED status
                .perform(SetCramStatusEvent.class) // Update the cram status/cancel the pending request
                .perform(QasinoResetAction.class)
                .onResult(FAILURE, FAILED)
                .onResult(SUCCESS, LISTED);


        // Retry
        qasinoConfiguration.onState(AUTHORIZED)
                .onEvent(RETRY)
                .transition(AUTHORIZED, VERIFY);
        qasinoConfiguration.onState(REVERIFIED)
                .onEvent(RETRY)
                .transition(AUTHORIZED, VERIFY);
        qasinoConfiguration.onState(RECHECKED)
                .onEvent(RETRY)
                .transition(RECHECKED, EXECUTE);
        qasinoConfiguration.onState(EXECUTED)
                .onEvent(RETRY)
                .transition(EXECUTED, SEND_MESSAGE);
        qasinoConfiguration.onState(FULFILLED_CUSTOMER)
                .onEvent(RETRY)
                .transition(FULFILLED_CUSTOMER,EXECUTE);


        // Reject / expired / cancelled
        qasinoConfiguration.onState(SUBMITTED)
                .onEvent(REJECT)
                .transition(EXPIRED);
        qasinoConfiguration.onState(SUBMITTED)
                .onEvent(CANCEL)
                .transition(CANCELLED);

    }
*/


    private QasinoEventHandler eventHandler;


    @PostConstruct
    public void init() {
        eventHandler = new QasinoEventHandler(qasinoConfiguration, applicationContext);
    }

    public <T extends AbstractFlowDTO> T handleEvent(Event event, T dto) {
        eventHandler.handleEvent(event, dto);
        return dto;
    }

}
