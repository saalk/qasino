package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.api.commission.CommissionAPIPeerToPeerResourceClient;
import applyextra.api.commission.value.CommissionAPIBusinessRequest;
import applyextra.api.exception.ResourceException;
import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.audit.impl.WhichWay;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import nl.ing.riaf.core.event.ApplicationEvent;
import nl.ing.riaf.core.util.JNDIUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 *  This is a class that invokes the gCommissioinAPI resource client to send an email to the back office.
 *  At the time of designing this class, the assumption is that the gCommissionAPI team only connects via peer-to-peer.
 * Hence, no logic for channel context has been included here.
 */
@Slf4j
@Lazy
@Component
public class SendCommissionAction implements Action<SendCommissionAction.SendCommissionActionDTO, EventOutput.Result> {

    @Resource private CommissionAPIPeerToPeerResourceClient commissionAPIPeerToPeerResourceClient;
    @Resource private JNDIUtil jndiUtil;
    @Resource private AuditDelegate auditDelegate;

    private static final String CONFIRMATION_EMAIL_PARAM = "param/confirmationEmail";

    @Override
    public EventOutput.Result perform(SendCommissionActionDTO dto) {
        final CommissionAPIBusinessRequest commissionAPIBusinessRequest = new CommissionAPIBusinessRequest();

        if(dto.getDataToSend() != null && !dto.getDataToSend().isEmpty()) {
            commissionAPIBusinessRequest.setBodyParams(dto.getDataToSend());
            //Make sure to set a jndi value for this if you want a confirmation email.
            commissionAPIBusinessRequest.setConfirmationEmail(jndiUtil.getJndiValue(CONFIRMATION_EMAIL_PARAM));

            logMessage(WhichWay.OUT, dto, null);
            try {
                commissionAPIPeerToPeerResourceClient.execute(commissionAPIBusinessRequest);
            } catch (ResourceException exception) {
                log.error("Failed to send an email"+ exception);
                return EventOutput.Result.FAILURE;
            }
            logMessage(WhichWay.IN, dto, EventOutput.Result.SUCCESS.name());
        } else {
            log.warn("No data to send from getDataToSend received in SendCommissionAction, cannot send email.");
        }
        return EventOutput.Result.SUCCESS;
    }

    private void logMessage (WhichWay way, SendCommissionActionDTO dto, String result) {
        auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, way) {
            @Override public void getSpecificFields(Map<String, Object> fields) {
                if ( (dto.getDataToSend() != null || !dto.getDataToSend().isEmpty())
                        && StringUtils.isBlank(result)) {
                    fields.putAll(dto.getDataToSend());
                }
                if (StringUtils.isNotBlank(result)) {
                    fields.put("Result", result);
                }
            }
        });
    }

    public interface SendCommissionActionDTO {

        Map<String, Object> getDataToSend();

    }
}
