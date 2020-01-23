package applyextra.commons.action;

import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import applyextra.operations.dto.SIAAccountInformation;
import applyextra.operations.dto.SiaOrchestrationDTO;
import applyextra.operations.model.SIACreditCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class MapInquireAccountCardToRequestAction implements Action<MapInquireAccountCardToRequestAction.MapInquireAccountCardToRequestActionDTO, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(MapInquireAccountCardToRequestActionDTO dto) {
        log.info("########## Start of event Perform MapInquireAccountCardToRequestAction: " + LocalTime.now());

        final List<SIAAccountInformation> accounts = dto.getSiaOrchestrationDTO().getAccounts();
        final String selectedCardId = dto.getSelectedCardId();

        final List<SIACreditCard> listCard = accounts.stream()
                .flatMap(s -> s.getSiaCards().stream()
                        .filter(c -> selectedCardId.equals(
                                c.getCardNumber().substring(c.getCardNumber().length() - 4))))
                .collect(toList());

        if (listCard.isEmpty() || listCard.size() > 1) {
            throw new ActivityException(dto.getApplicationName(), "Could not determine the Correct Creditcard for card number: "
                    + selectedCardId);
        }

        final SIACreditCard creditCard = listCard.get(0);
        dto.getCreditcardRequest().getAccount().getCreditCard().setCreditCardNumber(creditCard.getCardNumber());
        log.info("########## End of event Perform MapInquireAccountCardToRequestAction: " + LocalTime.now());
        return EventOutput.Result.SUCCESS;
    }

    public interface MapInquireAccountCardToRequestActionDTO {
        SiaOrchestrationDTO getSiaOrchestrationDTO();

        String getSelectedCardId();

        CreditCardRequestEntity getCreditcardRequest();

        String getApplicationName();
    }
}
