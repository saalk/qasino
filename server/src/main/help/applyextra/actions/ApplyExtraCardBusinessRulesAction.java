package applyextra.actions;


import applyextra.commons.action.GatherChangeProcessDataAction;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.ChangeProcessEntity;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.AccountUtils;
import applyextra.configuration.Constants;
import applyextra.operations.event.PersistProcessSpecificValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is a class which holds the business rules that need to be checked.
 */
@Lazy
@Component
@Slf4j
public class ApplyExtraCardBusinessRulesAction implements Action<ApplyExtraCardBusinessRulesAction.ApplyExtraCardBusinessRulesActionDTO, EventOutput.Result> {


    @Resource
    private JNDIUtil jndiUtil;

    @Resource
    private AccountIsNotStudentRule accountIsNotStudentRule;
    @Resource
    private MaxNumberOfExtraCardsRule maxNumberOfExtraCardsRule;
    @Resource
    private RequestorIsNotBeneficiaryRule requestorIsNotBeneficiaryRule;
    @Resource
    private MinimumAgeExtraCardHolderRule minimumAgeExtraCardHolderRule;
    @Resource
    private PendingRequestRule pendingRequestRule;
    @Resource
    private GetPartiesAction getPartiesAction;
    @Resource
    private RetrieveSecondaryCardAction retrieveSecondaryCardAction;
    @Resource
    private ExtraCardNotAllowedTwiceRule extraCardNotAllowedTwiceRule;
    @Resource
    private GatherChangeProcessDataAction gatherChangeProcessDataAction;
    @Resource
    private NoChangeProcessCheckRule noChangeProcessCheckRule;

    @Override
    public EventOutput.Result perform(ApplyExtraCardBusinessRulesActionDTO flowDto) {

        final Map<KandatRule, Object> rules = flowDto.getRulesMap();

        if (flowDto.getIban() == null) {
            throw new ActivityException("ApplyExtraCard", "Iban cannot be null");
        }

        final List<RoleWithArrangement> arrangements = flowDto.getRoleWithArrangements().stream()
                .filter(roleWithArrangement -> findValidArrangement(flowDto.getIban(), roleWithArrangement))
                .collect(Collectors.toList());

        if (arrangements.isEmpty()) {
            throw new ActivityException("ListArrangements", "no arrangement found for iban:" + flowDto.getIban());
        }

        rules.put(accountIsNotStudentRule, arrangements);

        rules.put(requestorIsNotBeneficiaryRule, Arrays.asList(flowDto.getCustomerId(), flowDto.getBeneficiaryId()));
        rules.put(pendingRequestRule, flowDto.getLastRequests());

        final Years years = Years.yearsBetween(flowDto.getBeneficiaryDateOfBirth().toDateTimeAtStartOfDay(), DateTime.now());
        rules.put(minimumAgeExtraCardHolderRule, years.getYears());

        final List<CreditCardMemberAccount> creditCardMemberAccountList = new ArrayList<>();
        if (flowDto.getCreditCardAccount() != null && flowDto.getCreditCardAccount().getCreditCardMemberAccountList() != null) {
            creditCardMemberAccountList.addAll(flowDto.getCreditCardAccount().getCreditCardMemberAccountList());
        }
        rules.put(maxNumberOfExtraCardsRule, creditCardMemberAccountList); // This Check is also in initialize.

        // TODO: remove isBusinessRuleActive() if it is working.
        //Checks to see if secondary card is present and if so, the rule checked that the extracard is not allowed twice
        if (isBusinessRuleActive() && retrieveSecondaryCardAction.perform(flowDto)) {
            String secondaryAccountNumber = flowDto.getProcessSpecificValue(Constants.SECONDARY_ACCOUNT_NUMBER);
            if (StringUtils.isNotEmpty(secondaryAccountNumber)) {
                EventOutput getPartiesResult = getPartiesAction.perform(flowDto);
                if (getPartiesResult.isFailure()) {
                    throw new ActivityException("GetParties", "No secondary party found" + secondaryAccountNumber);
                }
                rules.put(extraCardNotAllowedTwiceRule, Arrays.asList(flowDto.getPartyId(), flowDto.getBeneficiaryId()));
                log.debug("ExtraCardNotAllowedTwiceRule executed successfully ");
            }
        }

        //TODO: remove business rule once RRB dependency is removed
        gatherChangeProcessDataAction.perform(flowDto);
        rules.put(noChangeProcessCheckRule, flowDto.getChangeProcessHistoricalRequests());

        return SUCCESS;
    }

    private boolean findValidArrangement(String iban, RoleWithArrangement roleWithArrangement) {
        return AccountUtils.ibanToBbanAsString(iban).equals(
                roleWithArrangement.getArrangement().getPackageBbanNumber()) ||
                iban.equals(roleWithArrangement.getArrangement().getIbanNumber());
    }


    private boolean isBusinessRuleActive() {
        return jndiUtil.getJndiValueWithDefault("businessrule/extracardnotallowedtwicerule", true);
    }

    public interface ApplyExtraCardBusinessRulesActionDTO extends GetPartiesAction.GetPartiesDTO, PersistProcessSpecificValue
            .ProcessSpecificDataActionDTO, GatherChangeProcessDataAction.GatherChangeProcessDataActionDTO {
        Map<KandatRule, Object> getRulesMap();

        String getCustomerId();

        String getBeneficiaryId();

        LocalDate getBeneficiaryDateOfBirth();

        List<RoleWithArrangement> getRoleWithArrangements();

        String getIban();

        CreditCardAccount getCreditCardAccount();

        List<CreditCardRequestEntity> getLastRequests();

        String getProcessSpecificValue(String key);

        String getPartyId();

        List<ChangeProcessEntity> getChangeProcessHistoricalRequests();

    }
}
