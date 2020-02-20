package applyextra.commons.util;

import applyextra.api.listaccounts.creditcards.value.CreditCard;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccount;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccountType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Util class for CredCardMemberAccounts.
 */
public class CreditCardMemberListUtil {

    //Method to check if credit playingcard account contains member acount list
    public static boolean isMemberListPresent(CreditCardAccount creditCardAccount) {
        return CreditCardAccountType.POSITION.equals(creditCardAccount.getCreditCardAccountType());
    }

    //Method to get the CreditCard list from the creditCardAccount
    public static List<CreditCard> getCreditCardList(CreditCardAccount creditCardAccount) {
        return creditCardAccount.getCreditCardMemberAccountList()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(c -> c.getCreditCardList().stream())
                .collect(Collectors.toList());
    }
}
