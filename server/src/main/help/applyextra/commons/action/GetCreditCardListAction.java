package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.listaccounts.creditcards.value.CreditCard;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccount;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.CreditCardMemberListUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This Move class is a prototype that needs refinement.
 * The main purpose is to obtain the credit playingcard account, credit playingcard and the credit playingcard member account.
 * In each case there are 2 steps
 * 1. Check if the member list is present or not
 * 2. IF member list is present then get the playingcard list from the member list utils. Else get it from the CreditCardListUtil
 */
@Component
@Slf4j
public class GetCreditCardListAction implements Action<GetCreditCardListAction.GetCreditCardListActionDTO,EventOutput> {

    @Override
    public EventOutput perform(GetCreditCardListActionDTO dto) {
            CreditCardAccount creditCardAccount = dto.getCreditCardAccount();
            //is member
            if(creditCardAccount == null) {
                log.error("CreditCard Account is null");
                return EventOutput.failure();
            }
            if(CreditCardMemberListUtil.isMemberListPresent(creditCardAccount)) {
                dto.setCreditCardList(CreditCardMemberListUtil.getCreditCardList(creditCardAccount));
            } else {
                dto.setCreditCardList(creditCardAccount.getCreditCardList());
            }

        return EventOutput.success();
    }

    public interface GetCreditCardListActionDTO {
        void setCreditCardList(List<CreditCard> creditCardList);
        ChannelContext getChannelContext();
        CreditCardAccount getCreditCardAccount();
    }
}
