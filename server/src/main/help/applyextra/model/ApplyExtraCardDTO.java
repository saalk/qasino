package applyextra.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;
import applyextra.actions.*;
import applyextra.configuration.Constants;
import applyextra.event.ApplyExtraCardRRBEvent;
import applyextra.api.listaccounts.creditcards.value.CreditCard;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccount;
import applyextra.api.parties.individuals.value.IndividualsAllBusinessResponse;
import applyextra.businessrules.KandatRule;
import applyextra.commons.action.*;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.event.AbstractCreditcardFlowDTO;
import applyextra.commons.model.database.entity.ChangeProcessEntity;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.model.financialdata.CardType;
import applyextra.commons.orchestration.EventEnum;
import applyextra.commons.state.CreditCardsStateMachine;
import applyextra.operations.dto.CramDTO;
import applyextra.operations.dto.CramStatus;
import applyextra.operations.dto.RejectReasonDTO;
import applyextra.operations.dto.SiaOrchestrationDTO;
import applyextra.operations.event.CheckEvent;
import applyextra.operations.event.InquireAccountEvent;
import applyextra.operations.event.ListPartiesByIbanAndDobEvent;
import applyextra.operations.event.SetCramStatusEvent;
import applyextra.operations.model.PackageType;
import applyextra.operations.model.SimplePaymentAccount;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.RoleWithArrangement;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigInteger;
import java.util.*;

import static com.ing.api.toolkit.trust.context.ContextSession.ChannelOfOrigin.ASSISTED_CALL;
import static applyextra.configuration.Constants.NAME_APPLY_EXTRA_CARD;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


/**
 * DTO class that is passed to all the moves.
 */

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyExtraCardDTO extends AbstractCreditcardFlowDTO implements
        StoreApplyExtraCardRequestContextAction.StoreRequestContextDTO,
        RetrieveListCreditCardAccountsAction.RetrieveListOfAccountsDTO,
        ListArrangementsAction.ListArrangementsActionDTO,
        DeterminePackageFromListArrangementsResponse.DeterminePackageFromListArrangementsResponseDTO,
        IndividualsAllAction.IndividualsAllActionDTO,
        IndividualsAllForBeneficiaryAction.IndividualsAllForBeneficiaryActionDto,
        CheckMaxNumberOfCardsAction.ApplyExtraCardBusinessRulesDTO,
        CheckEvent.CheckEventDTO,
        VerifyEventWrapperForCustomerAndBeneficiaryAction.VerifyEventWrapperForBeneficiaryActionDTO,
        ApplyExtraCardBusinessRulesAction.ApplyExtraCardBusinessRulesActionDTO, GetCreditCardListAction.GetCreditCardListActionDTO,
        ApplyExtraCardRRBEvent.ApplyExtraCardRRBEventDTO,
        LoadCustomersHistoricalRequestsAction.LoadCustomersHistoricalRequestsActionDTO,
        CancelOpenRequestsAction.CancelOpenRequestsActionDTO,
        PopulateCRAMDataAction.PopulateCRAMDataActionDTO,
        CreateOrSetCramStatusAction.CreateOrSetCramStatusActionDTO,
        SetCramStatusEvent.SetCramStatusEventDTO,

        RetrieveSecondaryAccountNumberAction.RetrieveSecondaryAccountNumberActionDTO,
        StoreCramCorrelationAction.StoreCramCorrelationDTO,
        MapInquireAccountCardToRequestAction.MapInquireAccountCardToRequestActionDTO,

        // TODO: Remove when no longer needed
        InquireAccountEvent.InquireAccountEventDTO {

    private List<CreditCardAccount> listCreditCardAccount = new ArrayList<>();
    private String siaAccountNumber;
    private Integer rulesCode;
    private String selectedCardId;
    private List<ChangeProcessEntity> changeProcessHistoricalRequests = new ArrayList<>();


    public String getSiaAccountNumber () {
        if (siaAccountNumber == null) {
            siaAccountNumber = getCreditcardRequest().getAccount().getAccountNumber();
            if (siaAccountNumber == null) {
                throw new ActivityException(NAME_APPLY_EXTRA_CARD, "Couldn't find the Creditcard account");
            }
        }
        return siaAccountNumber;
    }

    //GetPartiesDTO
    private List<ListPartiesByIbanAndDobEvent.SimpleParty> parties;
    private Integer type;
    private String value;
    private String partyId;
    private String secondaryAccountNumber;

    //PackageArrangementDTO
    private PackageType packageRefType;
    private SimplePaymentAccount simplePaymentAccount;

    private List<RoleWithArrangement> roleWithArrangements = new ArrayList<>(); // TODO: replace with gArrangementsAPI once packagetype is in MDM

    private Map<KandatRule, Object> rulesMap = new LinkedHashMap<>();
    private List<CreditCard> creditCardList;
    private CreditCardAccount creditCardAccount;

    // Individuals All
    private IndividualsAllBusinessResponse individualsAll;
    private IndividualsAllBusinessResponse beneficiaryIndividualsAll;

    // VerifyEventDTO
    private BigInteger arrangementType = Constants.VERIFYEVENT_ARRANGEMENTTYPE_FOR_IBAN;
    private BigInteger verificationCode; // Required by interface, but not used. Wrapper classes return the correct Code from the constants
    private String verificationStatus;
    private List<RejectReasonDTO> rejectReason = new ArrayList<>();

    // Load Context
    private List<CreditCardRequestEntity> lastRequests = new ArrayList<>();

    //RRB
    private String beneficiaryId;
    private String creditCardNumber;

    //Cram
    private CramDTO cramDTO = new CramDTO();
    private CramStatus cramStatus;

    @Override
    public CramDTO.RiskLevel getRiskLevel () {
        ChannelContext channelContext = getChannelContext();
        return channelContext != null && channelContext.getContextSession().getChannelOfOrigin().equals(ASSISTED_CALL)
                ? CramDTO.RiskLevel.MILD
                : CramDTO.RiskLevel.MEDIUM;
    }


    @Override
    public RequestType getContextType() {
        return RequestType.APPLY_EXTRA_CARD;
    }

    @Override
    public String getApplicationName() {
        return NAME_APPLY_EXTRA_CARD;
    }


    private Map<String, String> persistedValues = new HashMap<>();


    @Override
    public String getArrangementId() {
        return this.getIban();
    }

    @Override public String getSelectedCreditCardId() { return getSelectedCardId(); }
    public String getSelectedCardId () {
        if (this.selectedCardId == null)  {
            final String card = getCreditcardRequest().getAccount().getCreditCard().getCreditCardNumber();
            if (card ==  null) { throw new ActivityException(NAME_APPLY_EXTRA_CARD, "Empty Creditcard in Request"); }
            this.selectedCardId = card.substring(card.length() -4);
        }
        return this.selectedCardId;
    }


    @Override
    public LocalDate getBeneficiaryDateOfBirth() {
        if (getBeneficiaryIndividualsAll() != null && isNotBlank(getBeneficiaryIndividualsAll().getBirthDate())) {
            final DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DATE_TIME_FORMAT_DD_MM_YYYY);
            return formatter.parseLocalDate(getBeneficiaryIndividualsAll().getBirthDate());
        }
        return null;
    }

    @Override
    public String getBeneficiaryId() {
        return getCreditcardRequest().getBeneficiaryId();
    }

    @Override
    public CardType getCardType() {
        return this.getCreditcardRequest().getAccount().getAccountStatus().getCardType();
    }

    @Override
    public String getIban() {
        return getCreditcardRequest().getAccount().getIban();
    }

    @Override
    public void setIban(String iban) {
        getCreditcardRequest().getAccount().setIban(iban);
    }

    @Override
    public String getCreditCardNumber() {
        return getCreditcardRequest().getAccount().getCreditCard().getCreditCardNumber();
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.getCreditcardRequest().setBeneficiaryId(beneficiaryId);
    }

    @Override
    public List<CreditCardsStateMachine.State> getPendingStates() {
        return Constants.CANCEL_REQUEST_STATE;
    }

    @Override
    public List<RequestType> requestTypesToCancel() {
        return Arrays.asList(getContextType());
    }

    // LoadCustomersHistoricalRequestsAction
    @Override public List<RequestType> getRequestTypes() { return Collections.singletonList(getContextType()); }
    public List<CreditCardsStateMachine.State> getLastFulfilledRequestStates() {
        return Constants.PENDING_STATES;
    }

    @Override    //Set the CramStatus by logic
    public CramStatus getCramStatus() {
        //If the user goes back, then Cram request should be cancelled
        if(EventEnum.RESET.equals(this.getCurrentEvent())) {
            return CramStatus.DRAFT;
        }
        return determineStatus(getCurrentState());
    }

    private CramStatus determineStatus(final CreditCardsStateMachine.State currentState){
        switch (currentState){
            case SUBMITTED:
                return CramStatus.EXECUTION;
            case FULFILLED_CUSTOMER:
                return CramStatus.FULFILLED;
            default:
                return CramStatus.SUBMITTED;
        }
    }

    // TODO: remove when deleting InquireAccount
    private SiaOrchestrationDTO siaOrchestrationDTO = new SiaOrchestrationDTO();
    @Override
    public String getAccountNumber() {
        return getSiaAccountNumber();
    }

    @Override
    public String getProcessSpecificValue(String key) {
        return persistedValues.get(key);
    }

    @Override
    public void setProcessSpecificValue(String key, String value) {
        persistedValues.put(key, value);
    }

    @Override
    public java.time.LocalDate getDateOfBirth() {
        return null;
    }
    // TODO: end of remove when deleting InquireAccount


    @Override
    public List<Integer> getCategoryCodes() {
        final List<Integer> categoryCodes = new ArrayList<>();
        categoryCodes.add(15);
        return categoryCodes;
    }

}
