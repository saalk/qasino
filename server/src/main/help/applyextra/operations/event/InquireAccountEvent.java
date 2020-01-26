package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.AccountCategory;
import applyextra.commons.model.PortfolioCode;
import applyextra.operations.dto.SIAAccountInformation;
import applyextra.operations.dto.SiaOrchestrationDTO;
import applyextra.operations.model.SIACreditCard;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.serviceclient.sia.common.configuration.ServiceWrapper;
import nl.ing.serviceclient.sia.inquireaccount2.dto.InquireAccount2BusinessRequest;
import nl.ing.serviceclient.sia.inquireaccount2.dto.InquireAccount2BusinessResponse;
import nl.ing.serviceclient.sia.inquireaccount2.jaxb.generated.AccountInfoResponseItem;
import nl.ing.serviceclient.sia.inquireaccount2.jaxb.generated.AccountInfoResponseItem2;
import nl.ing.serviceclient.sia.inquireaccount2.jaxb.generated.CardInfoResponseItem;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
@Lazy
public class InquireAccountEvent extends AbstractEvent {
    public static final long DEFAULT_REQUEST_TIMEOUT = 5000;

    private static final String SERVICE_NAME_INQUIRE_ACCOUNT = "inquireaccount";
    @Resource(name = "InquireAccount2ServiceClient")
    private ServiceWrapper<InquireAccount2BusinessRequest, InquireAccount2BusinessResponse> inquireAccount2ServiceClient;
    @Resource
    private JNDIUtil jndiUtil;

    @Override
    protected EventOutput execution(final Object... eventInput) {
        final InquireAccountEventDTO flowDTO = (InquireAccountEventDTO) eventInput[0];
        final String accountNumber = flowDTO.getAccountNumber();

        final InquireAccount2BusinessRequest businessRequest = new InquireAccount2BusinessRequest();
        businessRequest.setAccountNumber(accountNumber);
        final InquireAccount2BusinessResponse businessResponse;
        try {
            businessResponse = inquireAccount2ServiceClient.invoke(businessRequest, jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", DEFAULT_REQUEST_TIMEOUT));
            if (businessResponse == null) {
                throw new Exception("No response received from InquireAccount2");
            }
        } catch (final RIAFRuntimeException e) {
            throw e; // will be caught and dealt with further up the chain
        } catch (final Exception e) {
            log.error("problem invoking execution", e);
            throw new ActivityException(SERVICE_NAME_INQUIRE_ACCOUNT, "call to InquireAccount2ServiceClient failed", e);
        }
        processResponse(businessResponse, flowDTO);

        return EventOutput.success();
    }

    private void processResponse(final InquireAccount2BusinessResponse response, final InquireAccountEventDTO dto) {

        final SiaOrchestrationDTO siaOrchestrationDTO = dto.getSiaOrchestrationDTO();

        if (response.getAccountInfoResponseItem() != null && !response.getAccountInfoResponseItem()
                .isEmpty()) {

            siaOrchestrationDTO.getAccounts()
                    .clear();
            for (final AccountInfoResponseItem accountInfo : response.getAccountInfoResponseItem()) {
                processMemberInformation(accountInfo, siaOrchestrationDTO.getAccounts());
            }
        } else {

            log.error(String.format("RequestId: %s", dto.getRequestId()));
            throw new ActivityException(SERVICE_NAME_INQUIRE_ACCOUNT, "Error getting account information ");
        }

    }

    private void processMemberInformation(final AccountInfoResponseItem accountInfo,
                                          final List<SIAAccountInformation> accounts) {
        final SIAAccountInformation result = new SIAAccountInformation();
        result.setSiaAccountNumber(accountInfo.getAccountNumber());
        result.setSiaAccountStatus(accountInfo.getAccountStatus());
        result.setPortfolioCode(PortfolioCode.string2Code(accountInfo.getPortfolioCode()));
        result.setCreditLineAmount(accountInfo.getCreditLineAmount());
        result.setDailyFinancialsResponseItem(accountInfo.getDailyFinancialsResponseItem());

        for (final CardInfoResponseItem cardInfo : accountInfo.getCardInfoResponseItem()) {
            addCard(cardInfo, result.getSiaCards());
        }

        result.setAccountCategory(transformAccountCategory(accountInfo.getAccountCategory()));

        for (final AccountInfoResponseItem2 memberAccount : accountInfo.getAccountInfoResponseItem()) {
            processMemberInformation(memberAccount, accounts);
        }

        accounts.add(result);
    }

    private AccountCategory transformAccountCategory(final String accountCategory) {
        if ("".equals(accountCategory) || "R".equals(accountCategory)) {
            // regular account
            return AccountCategory.REGULAR;
        } else if ("P".equals(accountCategory)) {
            // position account
            return AccountCategory.POSITION;
        } else if ("M".equals(accountCategory)) {
            return AccountCategory.MEMBER;
        }
        throw new ActivityException(SERVICE_NAME_INQUIRE_ACCOUNT,
                "Unknown category received from SIA: " + accountCategory);
    }

    private void processMemberInformation(final AccountInfoResponseItem2 memberAccountInfo,
                                          final List<SIAAccountInformation> accounts) {
        final SIAAccountInformation result = new SIAAccountInformation();

        result.setSiaAccountNumber(memberAccountInfo.getAccountNumber());
        result.setSiaAccountStatus(memberAccountInfo.getAccountStatus());
        result.setPortfolioCode(PortfolioCode.string2Code(memberAccountInfo.getPortfolioCode()));

        for (final CardInfoResponseItem cardInfo : memberAccountInfo.getCardInfoResponseItem()) {
            addCard(cardInfo, result.getSiaCards());
        }

        result.setAccountCategory(transformAccountCategory(memberAccountInfo.getAccountCategory()));

        accounts.add(result);
    }

    private void addCard(final CardInfoResponseItem cardInfo, final List<SIACreditCard> cards) {
        final SIACreditCard card = new SIACreditCard();
        card.setActivationDate(cardInfo.getActivationDate());
        card.setActivationFlag(cardInfo.getActivationFlag());
        card.setCardNumber(cardInfo.getCardNumber());
        card.setCardStatus(cardInfo.getCardStatus());
        card.setEmbossingDate(cardInfo.getEmbossingDate());
        card.setEmbossingLine1(cardInfo.getEmbossingLine1());
        card.setEmbossingLine2(cardInfo.getEmbossingLine2());
        card.setExpiryDate(cardInfo.getExpiryDate());
        card.setPlasticType(cardInfo.getPlasticType());
        cards.add(card);
    }

    public interface InquireAccountEventDTO {
        String getRequestId();

        String getAccountNumber();

        SiaOrchestrationDTO getSiaOrchestrationDTO();
    }

}
