package applyextra.operations.service;


import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.operations.dto.InquireAccount2DTO;
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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;

@Component
@Slf4j
@Deprecated
@Lazy
/**
 * Deprecated!
 * Use {@link applyextra.operations.event.InquireAccountEvent} instead.
 */
public class InquireAccount2Service {
    public static final long DEFAULT_REQUEST_TIMEOUT = 5000;

    private static final String SERVICE_NAME_INQUIRE_ACCOUNT = "inquireaccount";
    private static final String INQUIRE_ACCOUNT2_SUCCESS = "01001";

    private long serviceTimeout = DEFAULT_REQUEST_TIMEOUT;

    @Resource(name = "InquireAccount2ServiceClient")
    private ServiceWrapper<InquireAccount2BusinessRequest, InquireAccount2BusinessResponse> inquireAccount2ServiceClient;

    @Resource
    private JNDIUtil jndiUtil;

    @PostConstruct
    public void init() {
        serviceTimeout = jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", DEFAULT_REQUEST_TIMEOUT);
    }

    public InquireAccount2DTO callInquireAccount2(final InquireAccount2DTO dto, final String requestId) {
        final String accountNumber = dto.getAccountNumber();

        final InquireAccount2BusinessRequest businessRequest = new InquireAccount2BusinessRequest();
        businessRequest.setAccountNumber(accountNumber);
        final InquireAccount2BusinessResponse businessResponse;
        try {
            businessResponse = inquireAccount2ServiceClient.invoke(businessRequest, serviceTimeout);
        } catch (RIAFRuntimeException e) {
            throw e;    // will be caught and dealt with further up the chain
        } catch (Exception e) {
            log.error("problem invoking execution", e);
            throw new ActivityException(SERVICE_NAME_INQUIRE_ACCOUNT, "call to InquireAccount2ServiceClient failed", e);
        }
        processResponse(businessResponse, dto, requestId);

        return dto;
    }

    private void processResponse(final InquireAccount2BusinessResponse response, final InquireAccount2DTO dto,
                                 final String requestId) {
		if (response.getAccountInfoResponseItem() != null && !response.getAccountInfoResponseItem().isEmpty()) {
            AccountInfoResponseItem accountInfo = response.getAccountInfoResponseItem().get(0);
            // regular account
            if ("".equals(accountInfo.getAccountCategory())) {
                processRegularAccount(response, dto);
            }
            // position/member account
            else if ("P".equals(accountInfo.getAccountCategory())) {
                processMemberAccount(response, dto);
            }
		} else {

			log.error(String.format("RequestId: %s", requestId));
			throw new ActivityException(SERVICE_NAME_INQUIRE_ACCOUNT, "Error getting account information");

		}

    }

    private void addCard(final CardInfoResponseItem cardInfo, final InquireAccount2DTO dto) {
        SIACreditCard card = new SIACreditCard();
        card.setActivationDate(cardInfo.getActivationDate());
        card.setActivationFlag(cardInfo.getActivationFlag());
        card.setCardNumber(cardInfo.getCardNumber());
        card.setCardStatus(cardInfo.getCardStatus());
        card.setEmbossingDate(cardInfo.getEmbossingDate());
        card.setEmbossingLine1(cardInfo.getEmbossingLine1());
        card.setEmbossingLine2(cardInfo.getEmbossingLine2());
        card.setExpiryDate(cardInfo.getExpiryDate());
        card.setPlasticType(cardInfo.getPlasticType());
        dto.getCards().add(card);
    }

    private void processRegularAccount(final InquireAccount2BusinessResponse businessResponse, final InquireAccount2DTO dto) {
        AccountInfoResponseItem accountInfo = businessResponse.getAccountInfoResponseItem().get(0);
        dto.setCards(new ArrayList<SIACreditCard>());
        for (CardInfoResponseItem cardInfo : accountInfo.getCardInfoResponseItem()) {
            addCard(cardInfo, dto);
        }
    }

    private void processMemberAccount(final InquireAccount2BusinessResponse businessResponse, final InquireAccount2DTO dto) {
        AccountInfoResponseItem accountInfoPosition = businessResponse.getAccountInfoResponseItem().get(0);
        if (accountInfoPosition.getAccountInfoResponseItem() != null && !accountInfoPosition.getAccountInfoResponseItem().isEmpty()) {
            AccountInfoResponseItem2 accountInfoMember = accountInfoPosition.getAccountInfoResponseItem().get(0);
            dto.setCards(new ArrayList<SIACreditCard>());
            for (CardInfoResponseItem cardInfo : accountInfoMember.getCardInfoResponseItem()) {
                addCard(cardInfo, dto);
            }
        }
    }
}
