package applyextra.actions;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccount;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccountStatus;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccountType;
import applyextra.api.listaccounts.creditcards.value.CreditCardMemberAccount;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.SecurityEncryptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * Class to check and retrieve the decrypted account account number of the member account if its present.
 */
@Component
@Lazy
@Slf4j
public class RetrieveSecondaryAccountNumberAction implements Action<RetrieveSecondaryAccountNumberAction.RetrieveSecondaryAccountNumberActionDTO, EventOutput> {

    @Resource
    private SecurityEncryptionUtil securityEncryptionUtil;

    @Override
    public EventOutput perform(RetrieveSecondaryAccountNumberActionDTO flowDTO) {
        log.debug("Getting the Secondary Account Number");
        if(!StringUtils.equals(CreditCardAccountType.REGULAR.toString(), flowDTO.getCreditCardAccount().getCreditCardAccountType().toString())) {
            if (flowDTO.getCreditCardAccount().getCreditCardMemberAccountList().size() >= 2) {
                String encryptedSecondaryAccountNumber = flowDTO.getCreditCardAccount().getCreditCardMemberAccountList().stream()
                        .filter(ma -> CreditCardAccountStatus.ACTIVE.equals(ma.getCreditCardMemberStatus()))
                        .filter(ma -> isNotSelectedAccount(flowDTO.getSiaAccountNumber(),
                                                            ma.getEncryptedAccountNumber(),
                                                            flowDTO.getChannelContext()))
                        .findFirst()
                        .orElse(new CreditCardMemberAccount()) // To prevent optional and null pointer
                        .getEncryptedAccountNumber();

                flowDTO.setSecondaryAccountNumber(
                        StringUtils.isNotEmpty(encryptedSecondaryAccountNumber)
                                ? securityEncryptionUtil.decrypt(encryptedSecondaryAccountNumber, flowDTO.getChannelContext())
                                : null
                );
            }
        }

        return EventOutput.success();
    }

    private boolean isNotSelectedAccount(String siaAccountNumber, String encryptedAccountNumber, ChannelContext channelContext) {
        final String decrypt = securityEncryptionUtil.decrypt(encryptedAccountNumber, channelContext);
        return !siaAccountNumber.equals(decrypt);
    }

    public interface RetrieveSecondaryAccountNumberActionDTO {
        String getSiaAccountNumber();
        CreditCardAccount getCreditCardAccount();
        ChannelContext getChannelContext();
        void setSecondaryAccountNumber(String secondaryAccountNumber);
    }
}
