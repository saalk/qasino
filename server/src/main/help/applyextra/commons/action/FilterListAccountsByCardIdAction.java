package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.listaccounts.creditcards.value.*;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.SecurityEncryptionUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Lazy
@Slf4j
@Component
public class FilterListAccountsByCardIdAction implements Action<FilterListAccountsByCardIdAction.FilterListAccountsByCardIdActionDTO, EventOutput.Result> {

    @Resource
    private SecurityEncryptionUtil securityEncryptionUtil;

    @Override
    public EventOutput.Result perform(FilterListAccountsByCardIdActionDTO dto) {
        final List<CreditCardAccount> listAccounts = dto.getListCreditCardAccount();
        final String cardId = dto.getCreditCardId();
        if (listAccounts == null || listAccounts.isEmpty() || cardId == null || cardId.isEmpty()) { return EventOutput.Result.FAILURE; }

        for (CreditCardAccount account : listAccounts) {
            if (account.getCreditCardList() != null && checkForSelectedCard(account.getCreditCardList(), cardId)) {
                log.debug("found Regular account for cardID: {}", cardId);
                return collectValuesForRequest(dto, account, null);

            } else if (account.getCreditCardMemberAccountList() != null) {
                Optional<CreditCardMemberAccount> memberAccount = account.getCreditCardMemberAccountList().stream()
                        .filter(member -> CreditCardAccountStatus.ACTIVE.equals(member.getCreditCardMemberStatus()))
                        .filter(member -> checkForSelectedCard(member.getCreditCardList(), cardId))
                        .findFirst();

                if (memberAccount.isPresent()) {
                    log.debug("found Family account for cardID: {}", cardId);
                    return collectValuesForRequest(dto, account, memberAccount.get());
                }
            }
        }
        log.warn("Could not select a creditcard from the list of accounts for cardId: {}", cardId);
        return EventOutput.Result.FAILURE;
    }


    private EventOutput.Result collectValuesForRequest(FilterListAccountsByCardIdActionDTO dto,
                                                       CreditCardAccount creditCardAccount,
                                                       CreditCardMemberAccount selectedMemberAccount) {

        dto.setCreditCardAccount(creditCardAccount);
        dto.setSelectedMemberAccount(selectedMemberAccount);


        String encryptedAccountNumber = selectedMemberAccount != null
                ? selectedMemberAccount.getEncryptedAccountNumber()
                : creditCardAccount.getEncryptedAccountNumber();

        dto.getCreditcardRequest().getAccount().setAccountNumber(
                securityEncryptionUtil.decrypt(encryptedAccountNumber, dto.getChannelContext()));


        return EventOutput.Result.SUCCESS;
    }



    private boolean checkForSelectedCard(List<CreditCard> cardList, String cardId) {
        return cardList.stream()
                .filter(this::canBeActivated)
                .anyMatch(creditCard -> cardId.equals(getCardIdFromCard(creditCard)));
    }

    private boolean canBeActivated (CreditCard creditCard) {
        return creditCard.getCreditCardStatus() != CreditCardStatus.BLOCKED &&
                creditCard.getCreditCardStatus() != CreditCardStatus.LOST &&
                creditCard.getCreditCardStatus() != CreditCardStatus.CANCELLED;
    }

    private String getCardIdFromCard (CreditCard creditCard) {
        return creditCard.getCreditCardNumber().substring(creditCard.getCreditCardNumber().length() - 4);
    }


    public interface FilterListAccountsByCardIdActionDTO  {
        List<CreditCardAccount> getListCreditCardAccount();
        String getCreditCardId();
        CreditCardRequestEntity getCreditcardRequest();
        ChannelContext getChannelContext();

        void setCreditCardAccount(CreditCardAccount creditCardAccount);
        void setSelectedMemberAccount(CreditCardMemberAccount creditCardMemberAccount);
    }
}
