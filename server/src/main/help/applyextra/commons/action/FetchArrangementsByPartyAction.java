package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import nl.ing.api.party.domain.Arrangement;
import applyextra.api.parties.arrangement.value.PartyArrangementBusinessRequest;
import applyextra.api.parties.arrangement.value.PartyArrangementBusinessResponse;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


@Component
@Lazy
@Slf4j
public class FetchArrangementsByPartyAction implements Action<FetchArrangementsByPartyAction.FetchArrangementsByPartyEventDTO, EventOutput> {
    private static String ACTIVE_STATUS_CODE = "1";
    private static String CREDITCARDS_CATEGORY_CODE = "14";
    private static String PAYMENTS_CATEGORY_CODE = "19";

    @Resource
    private GetArrangementsAction getArrangementsAction;

    public EventOutput perform(FetchArrangementsByPartyEventDTO flowDTO) {
        final PartyArrangementBusinessResponse businessResponse = getArrangementsAction.perform(flowDTO);

        if (businessResponse != null) {
            flowDTO.setCreditcardArrangements(businessResponse.getCreditCardArrangements());
            flowDTO.setInTransferArrangements(businessResponse.getInTransferArrangements());
            flowDTO.setStudentOverdraftArrangements(businessResponse.getStudentOverdraftArrangements());
        } else {
            throw new ActivityException("gArrangements", "No business response value from gArrangements has been set");
        }

        return EventOutput.success();
    }

    public interface FetchArrangementsByPartyEventDTO extends GetArrangementsAction.GetArrangementsActionDTO {
        void setCreditcardArrangements(List<Arrangement> arrangement);
        void setInTransferArrangements(List<Arrangement> inTransferArrangements);
        void setStudentOverdraftArrangements(List<Arrangement> studentOverdraftArrangements);

        @Override // Codes required for this action.
        default void addRequestCodesToRequest(PartyArrangementBusinessRequest request) {
            request.getCategoryCodes().add(CREDITCARDS_CATEGORY_CODE);
            request.getCategoryCodes().add(PAYMENTS_CATEGORY_CODE);
            request.getStatusCodes().add(ACTIVE_STATUS_CODE);
        }
    }

}