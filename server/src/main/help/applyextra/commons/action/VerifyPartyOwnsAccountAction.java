package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import applyextra.commons.orchestration.Action;
import applyextra.operations.model.SimplePaymentAccount;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import java.util.List;

@Lazy
@Component
public class VerifyPartyOwnsAccountAction implements Action<VerifyPartyOwnsAccountAction.VerifyPartyOwnsAccountActionDTO,
        Boolean> {
    @Override
    public Boolean perform(final VerifyPartyOwnsAccountActionDTO accountDTO) {
        if (!accountDTO.getListAccountsDTO().isListAccountResultOk()) {
            String partyId = accountDTO.getPartyId();
            throw new WebApplicationException("Could not fetch accounts for " + partyId);
        }

        List<SimplePaymentAccount> ownedAccounts = accountDTO.getListAccountsDTO().getAccountList();
        boolean isOwnedByParty = false;
        for (SimplePaymentAccount simplePaymentAccount : ownedAccounts) {
            if (simplePaymentAccount.getAccountNumber().equals(accountDTO.getArrangementId())){
                isOwnedByParty = true;
                accountDTO.setAccountOwnedByParty(true);
                break;
            }
        }
        return isOwnedByParty;
    }

    public interface VerifyPartyOwnsAccountActionDTO extends ListAccountsAction.ListAccountsActionDTO {
        String getArrangementId();

        String getPartyId();

        void setAccountOwnedByParty(boolean b);

        ChannelContext getChannelContext();
    }
}
