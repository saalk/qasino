package applyextra.commons.action;

import applyextra.api.creditbureau.get.value.BkrArrangement;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.converters.CreditcardServiceClientToCreditcardModelConverter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


@Lazy
@Component
public class BkrFilterRegistrationAction implements Action<BkrFilterRegistrationAction.BkrFilterRegistrationActionDTO, EventOutput.Result> {

    @Resource private CreditcardServiceClientToCreditcardModelConverter converter;

    @Override
    public EventOutput.Result perform(BkrFilterRegistrationActionDTO dto) {
        List<BkrArrangement> bkrArrangements = dto.getBkrArrangements();


        if (bkrArrangements == null || bkrArrangements.isEmpty()) {
            throw new ActivityException(dto.getApplicationName(), 500, "", null);
        }

        BkrArrangement arrangement = bkrArrangements.stream()
                // BkrArrangementId should be the CreditCard number.
                .filter(a -> a.getArrangementNumber().equalsIgnoreCase(dto.getBkrArrangementId()))
                .findAny()
                .orElseThrow(() -> new ActivityException(dto.getApplicationName(), 500,
                        "Failed to determine the correct BkrArrangement for requestId: " + dto.getRequestId() +
                                " with bkrArrangementId: " + maskArrangementId(dto.getBkrArrangementId()) , null));

        dto.setBkrArrangement(arrangement);
        return EventOutput.Result.SUCCESS;
    }

    private String maskArrangementId(String arrangement){
        return (arrangement == null || arrangement.isEmpty())
                ? null
                : converter.maskCreditCardNumber(arrangement);
    }

    public interface BkrFilterRegistrationActionDTO {
        List<BkrArrangement> getBkrArrangements();
        String getBkrArrangementId();      // BkrArrangementId should be the CreditCard number.
        void setBkrArrangement(BkrArrangement arrangement);

        String getApplicationName();
        String getRequestId();
    }
}
