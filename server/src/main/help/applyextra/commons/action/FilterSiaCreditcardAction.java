package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.dto.SIAAccountInformation;
import applyextra.operations.event.InquireAccountEvent;
import applyextra.operations.model.SIACreditCard;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


/**
 * This suppliedMove finds a creditcard in the @{@link applyextra.operations.dto.SiaOrchestrationDTO}
 * based on the playingcard number.
 * It is necessary to query SIA with the InquireAccountEvent because we need to get the activation flag from the CC.
 */
@Lazy
@Slf4j
@Component
public class FilterSiaCreditcardAction implements Action<FilterSiaCreditcardAction.FilterSiaCreditcardsDTO,EventOutput.Result> {

    private static final String EMPTY_STRING = "";

    @Override
    public EventOutput.Result perform(FilterSiaCreditcardsDTO flowDTO) {
        // sia dto is populated by inquire account move
        final List<SIAAccountInformation> accountList = flowDTO.getSiaOrchestrationDTO().getAccounts();
        final String cardId = flowDTO.getCreditCardId();

        if (accountList == null || cardId == null || cardId.isEmpty()) { return EventOutput.Result.FAILURE; }

        Optional<SIACreditCard> creditCard = accountList.stream()
                .flatMap(siaAccountInformation -> siaAccountInformation.getSiaCards().stream())
                .filter(siaCreditCard -> cardId.equals(getCardIdFromCreditCard(siaCreditCard)))
                .findFirst();

        if (!creditCard.isPresent()) {
            log.error("No creditcard found in list={} with cardNumber={}", accountList, cardId);
            return EventOutput.Result.FAILURE;
        }
        flowDTO.setSiaCreditCard(creditCard.get());
        return EventOutput.Result.SUCCESS;
    }

    private String getCardIdFromCreditCard (SIACreditCard card) {
        return card.getCardNumber() == null ? EMPTY_STRING : card.getCardNumber().substring(card.getCardNumber().length() - 4);
    }

    public interface FilterSiaCreditcardsDTO extends InquireAccountEvent.InquireAccountEventDTO{
        void setSiaCreditCard(final SIACreditCard card);
        String getCreditCardId();
    }
}
