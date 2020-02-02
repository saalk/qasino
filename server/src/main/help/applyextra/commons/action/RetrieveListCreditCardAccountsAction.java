package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.exception.ResourceException;
import applyextra.api.listaccounts.creditcards.CreditCardsListAccountsAPIPeerToPeerResourceClient;
import applyextra.api.listaccounts.creditcards.CreditCardsListAccountsAPIResourceClient;
import applyextra.api.listaccounts.creditcards.configuration.CreditCardsListAccountsAPIPeerToPeerResourceClientConfiguration;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccount;
import applyextra.api.listaccounts.creditcards.value.CreditCardMemberAccount;
import applyextra.api.listaccounts.creditcards.value.ListAccountsBusinessRequest;
import applyextra.api.listaccounts.creditcards.value.ListAccountsBusinessResponse;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.SecurityEncryptionUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static applyextra.api.listaccounts.creditcards.configuration.CreditCardsListAccountsAPIResourceClientConfiguration.SERVICE_NAME;

/**
 * Move to get the credit cards from list account api.
 */
@Slf4j
@Lazy
@Component
public class RetrieveListCreditCardAccountsAction implements Action<RetrieveListCreditCardAccountsAction.RetrieveListOfAccountsDTO, EventOutput.Result>{

    @Resource
    private CreditCardsListAccountsAPIResourceClient client;
    @Resource
    private CreditCardsListAccountsAPIPeerToPeerResourceClient p2pClient;
    @Resource // To get the p2p Token for decrypting.
    private CreditCardsListAccountsAPIPeerToPeerResourceClientConfiguration apiConfig;
    @Resource
    private SecurityEncryptionUtil securityEncryptionUtil;

    @Override
    public EventOutput.Result perform (RetrieveListOfAccountsDTO dto) {

        final ListAccountsBusinessResponse response;

        try {
            //Business request populated with channel context
            ListAccountsBusinessRequest listAccountsBusinessRequest = new ListAccountsBusinessRequest();
            if (dto.getChannelContext() == null) {
                listAccountsBusinessRequest.setPartyId(dto.getCustomerId()); // PartyId
                response = p2pClient.execute(listAccountsBusinessRequest);
            } else {
                listAccountsBusinessRequest.setChannelContext(dto.getChannelContext());
                response = client.execute(listAccountsBusinessRequest);
            }
        } catch (ResourceException e) {
            throw new ActivityException(SERVICE_NAME, 500, "CreditCardsListAccountsAPIResourceClient Exception", e);
        }

        if (response.getCreditCardAccountList().isEmpty()) {
            throw new ActivityException(SERVICE_NAME, 500, "CreditCardsListAccountsAPIResourceClient Exception", null);
        }

        if (dto.getSiaAccountNumber() != null) {
            final CreditCardAccount account = selectCreditcardAccount(
                    getSharedSecret(dto.getChannelContext()),
                    dto.getSiaAccountNumber(),
                    response.getCreditCardAccountList());

            if (account != null) {
                dto.setCreditCardAccount(account);
                dto.setIban(account.getIban());
                return EventOutput.Result.SUCCESS;
            }
            log.warn("Unable to Select the Correct creditcard account from listAccounts Response");
            return EventOutput.Result.FAILURE;
        } else {
            dto.getListCreditCardAccount().addAll(response.getCreditCardAccountList());
            return EventOutput.Result.SUCCESS;
        }
    }

    private String getSharedSecret(ChannelContext channelContext) {
        return channelContext != null
                ? channelContext.getContextSession().getSessionSharedSecret()
                : apiConfig.getPeerToPeerTrustToken().getClaimsSet().getSharedKey();
    }

    /*
        If channel context is null, the security service will take the config for peer 2 peer to try to decrypt the accountnumber.
     */
    private CreditCardAccount selectCreditcardAccount (final String sharedSecret,
                                                       final String accountNumber,
                                                       final List<CreditCardAccount> accountList) {

        for (CreditCardAccount account : accountList) {
            String decryptAccount = securityEncryptionUtil.decrypt( account.getEncryptedAccountNumber(), sharedSecret);
            if (accountNumber.equals(decryptAccount)) {
                return account;
            }

            final List<CreditCardMemberAccount> memberList = account.getCreditCardMemberAccountList();
            if (memberList != null && !memberList.isEmpty()){
                for (CreditCardMemberAccount member : memberList) {
                    String decryptMember =  securityEncryptionUtil.decrypt( member.getEncryptedAccountNumber(), sharedSecret);
                    if (accountNumber.equals(decryptMember)) { return account; }
               }
            }
        }
        return null;
    }



    public interface RetrieveListOfAccountsDTO {
        ChannelContext getChannelContext();
        String getSiaAccountNumber();
        List<CreditCardAccount> getListCreditCardAccount();
        void setIban(String iban);
        void setCreditCardAccount(CreditCardAccount creditCardAccount);
        String getCustomerId(); // PartyId used from CCrequest if no channel context present.
    }
}
