package applyextra.commons.util;


import lombok.extern.slf4j.Slf4j;
import applyextra.api.listaccounts.creditcards.value.*;
import applyextra.commons.activity.ActivityException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@Lazy
@Slf4j
public class FilterListAccountsResponseUtil {
    private static final String LIST_ACCOUNT_FILTER_NAME = "ListAccountFilter";


    public static final Predicate<CreditCard> isPrimary = c -> CreditCardType.PRIMARY.equals(c.getCreditCardType());

    public List<CreditCard> getActiveCreditCardsFromAccount(CreditCardAccount account) {
        List<CreditCard> creditCards = new ArrayList<>();

        if (null != account.getCreditCardList()) {
            account.getCreditCardList().stream() // Regular account
                    .filter(this::isCardNotBlockedOrStolen)
                    .filter(this::isCardNotCloseClosed )
                    .forEach(creditCards::add);
        }

        if (null != account.getCreditCardMemberAccountList()) {
            account.getCreditCardMemberAccountList().stream()  // Family account
                    .filter(ma -> CreditCardAccountStatus.ACTIVE.equals(ma.getCreditCardMemberStatus()))
                    .flatMap(ma -> ma.getCreditCardList().stream())
                    .filter(this::isCardNotBlockedOrStolen)
                    .filter(this::isCardNotCloseClosed )
                    .forEach(creditCards::add);
        }
        return creditCards;
    }

    public boolean isFamilyMainCardClosed(CreditCardAccount account, String creditCardNumber) {
        return null != account.getCreditCardMemberAccountList()
                && account.getCreditCardMemberAccountList().stream()
                .flatMap(member -> member.getCreditCardList().stream())
                .anyMatch(isPrimary.and(c -> getCardId(c.getCreditCardNumber()).equals(getCardId(creditCardNumber))));
    }

    public Optional<CreditCard> findPrimaryCard(List<CreditCard> creditCards) {
        return creditCards == null
                ? Optional.empty()
                : creditCards.stream().filter(isPrimary) .findFirst();
    }


    public String getCardId (String creditCardNumber) {
        return StringUtils.right(creditCardNumber,4);
    }

    public boolean isCardNotBlockedOrStolen(CreditCard creditcard) {
        return !CreditCardStatus.STOLEN.equals(creditcard.getCreditCardStatus())
                && !CreditCardStatus.LOST.equals(creditcard.getCreditCardStatus());
    }

    private boolean isCardNotCloseClosed(CreditCard creditcard) {
        return !CreditCardStatus.CLOSED.equals(creditcard.getCreditCardStatus());
    }

    public Optional<CreditCard> getSelectedCreditCard(CreditCardAccount creditCardAccount, String selectedCardId) {
        if (creditCardAccount == null ) { return Optional.empty(); }
        List<CreditCard> cardList = this.getActiveCreditCardsFromAccount(creditCardAccount);
        return cardList.stream()
                .filter(c -> this.getCardId(c.getCreditCardNumber()).equals(selectedCardId)).findAny();
    }


    public Boolean isSelectedCardPrimaryCard(CreditCardAccount creditCardAccount, String selectedCardId){
        Optional<CreditCard> response = this.getSelectedCreditCard(creditCardAccount, selectedCardId);

        return response.isPresent() && isPrimary.test(response.get());
    }

    public String[] getRolesFromAccount(CreditCardAccount creditCardAccount, String selectedCardId) {
        if (creditCardAccount!= null
                && creditCardAccount.getCreditCardList() != null
                && !creditCardAccount.getCreditCardList().isEmpty()
                && creditCardAccount.getRoles() != null) {
            return creditCardAccount.getRoles().stream()
                    .map(Enum::toString).toArray(String[]::new);
        }

        if (creditCardAccount!= null
                && creditCardAccount.getCreditCardMemberAccountList() != null
                && !creditCardAccount.getCreditCardMemberAccountList().isEmpty()) {
            return creditCardAccount.getCreditCardMemberAccountList().stream()
                    .filter(mb -> mb.getCreditCardList().stream()
                            .anyMatch(c -> getCardId(c.getCreditCardNumber()).equals(selectedCardId)))
                    .map(CreditCardMemberAccount::getRoles)
                    .filter(Objects::nonNull)
                    .flatMap(role -> role.stream().map(Enum::toString))
                    .toArray(String[]::new);
        }
        throw new ActivityException(LIST_ACCOUNT_FILTER_NAME, "Could not determine the Roles for the account with cardId: " + selectedCardId);
    }

    /**
     * Returns the AccountStatus from either the memberAccount in a family Structure, or the Posistion Account in a regular Structure.
     * @param account           Should be the selected account from which the status is desired. Throws exception when empty account is provided.
     * @param selectedCardId    Should be the cardId of the memberAccount which is filtered on. Only used in case of family structure.
     * @return CreditCardAccountStatus
     */
    public CreditCardAccountStatus getStatusFromAccount (CreditCardAccount account, String selectedCardId) {
        if (account == null ) {  throw new IllegalArgumentException("Input Account cannot be null. Unable to determine account status."); }
        if (account.getCreditCardMemberAccountList() != null ) {
            return account.getCreditCardMemberAccountList().stream()
                    .filter(mb -> mb.getCreditCardList().stream()
                            .anyMatch(c -> getCardId(selectedCardId).equals(getCardId(c.getCreditCardNumber()))))
                    .findFirst()
                    .orElseThrow(() -> new ActivityException(LIST_ACCOUNT_FILTER_NAME,
                            "Did not find a matching playingcard to get account Status for cardID: " + getCardId(selectedCardId)))
                    .getCreditCardMemberStatus();
        } else {
            return account.getCreditCardAccountStatus();
        }

    }
}
