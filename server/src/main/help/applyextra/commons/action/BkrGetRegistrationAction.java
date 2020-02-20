package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import applyextra.api.creditbureau.get.CreditBureauAPIGetPeerToPeerResourceClient;
import applyextra.api.creditbureau.get.CreditBureauAPIGetResourceClient;
import applyextra.api.creditbureau.get.value.BkrArrangement;
import applyextra.api.creditbureau.get.value.CreditBureauGetBusinessRequest;
import applyextra.api.creditbureau.get.value.CreditBureauGetBusinessResponse;
import applyextra.api.exception.ResourceException;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.audit.impl.WhichWay;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import nl.ing.riaf.core.event.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Lazy
@Component
public class BkrGetRegistrationAction implements Action<BkrGetRegistrationAction.BkrGetRegistrationActionDTO, EventOutput.Result> {

    @Resource private CreditBureauAPIGetResourceClient client;
    @Resource private CreditBureauAPIGetPeerToPeerResourceClient p2pClient;
    @Resource private AuditDelegate auditDelegate;

    private static final String REQUEST_ID = "requestId";
    private static final String CUSTOMER_ID = "customerId";
    private static final String MESSAGE = "message";

    @Override
    public EventOutput.Result perform(BkrGetRegistrationActionDTO dto) {

        final CreditBureauGetBusinessRequest request = CreditBureauGetBusinessRequest.builder()
                .channelContext(dto.getChannelContext())
                .partyId(dto.getCustomerId())
                .build();

        validateRequest(dto, request);

        logMessageToAuditLog(WhichWay.OUT, dto);
        final CreditBureauGetBusinessResponse response;
        try {
            response = dto.getChannelContext() != null
                    ? client.execute(request)
                    : p2pClient.execute(request);
        } catch (ResourceException e) {
            throw new ActivityException(dto.getApplicationName(), 500,
                    "Could not not retrieve the BKR Registrations for customerId " + dto.getCustomerId()
                            + " with requestId: " + dto.getRequestId(), e);
        }
        logMessageToAuditLog(WhichWay.IN, dto);

        dto.setBkrArrangements(response.getBkrArrangements());
        return EventOutput.Result.SUCCESS;
    }

    private void validateRequest(BkrGetRegistrationActionDTO dto, CreditBureauGetBusinessRequest request) {
        if (request.getPartyId() == null) {
            throw new ActivityException(dto.getApplicationName(), 500,
                    "Could not create request for getting BKR Registration due to missing customerId for requestId: "
                            + dto.getRequestId(), null);
        }
    }


    private void logMessageToAuditLog (WhichWay direction,BkrGetRegistrationActionDTO dto) {
        auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, direction) {
            @Override public void getSpecificFields(Map<String, Object> fields) {
                fields.put(REQUEST_ID, dto.getRequestId());
                fields.put(CUSTOMER_ID, dto.getCustomerId());
                fields.put(MESSAGE, WhichWay.OUT.equals( direction)
                        ? "Retrieving BKR registrations"
                        : "Successfully retrieved BKR Registration");
            }
        });
    }

    public interface BkrGetRegistrationActionDTO {
        ChannelContext getChannelContext();
        String getCustomerId();
        String getApplicationName();
        String getRequestId();

        void setBkrArrangements(List<BkrArrangement> bkrArrangements);
    }
}
