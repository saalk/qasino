package applyextra.actions;

import lombok.extern.slf4j.Slf4j;
import applyextra.api.listaccounts.creditcards.value.CreditCard;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccount;
import applyextra.businessrules.KandatRule;
import applyextra.businessrules.rules.AccountIsNotStudentRule;
import applyextra.businessrules.rules.MaxNumberOfExtraCardsRule;
import applyextra.commons.action.GetCreditCardListAction;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.RoleWithArrangement;
import org.joda.time.DateTimeUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Lazy
@Component
public class CheckMaxNumberOfCardsAction implements Action<CheckMaxNumberOfCardsAction.ApplyExtraCardBusinessRulesDTO,EventOutput> {

    @Resource
    private AccountIsNotStudentRule accountIsNotStudentRule;

    @Resource
    private MaxNumberOfExtraCardsRule maxNumberOfExtraCardsRule;

    @Resource
    private GetCreditCardListAction creditCardListAction;

    @Override
    public EventOutput perform(ApplyExtraCardBusinessRulesDTO dto) {
        log.info("########## Start of move Perform CheckMaxNumberOfCardsAction: " + LocalTime.now());
        dto.setCreditCardAccount(dto.getCreditCardAccount());
        EventOutput result = creditCardListAction.perform(dto);
        if(result.isFailure()) {
            throw new ActivityException("There are not credit cards", null);
        }
        final Map<KandatRule, Object> applicableBusinessRules = dto.getRulesMap();

        applicableBusinessRules.put(maxNumberOfExtraCardsRule, dto.getCreditCardAccount().getCreditCardMemberAccountList());
        applicableBusinessRules.put(accountIsNotStudentRule, dto.getRoleWithArrangements());

        log.info("########## End of move Perform CheckMaxNumberOfCardsAction: " + LocalTime.now());
        return EventOutput.success();
    }

    public interface ApplyExtraCardBusinessRulesDTO extends GetCreditCardListAction.GetCreditCardListActionDTO{
        Map<KandatRule, Object> getRulesMap();
        List<RoleWithArrangement> getRoleWithArrangements();

        CreditCardAccount getCreditCardAccount();

        void setCreditCardAccount(CreditCardAccount creditCardAccount);

        List<CreditCard> getCreditCardList();
    }

}
