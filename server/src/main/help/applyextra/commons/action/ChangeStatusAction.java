package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.audit.impl.WhichWay;
import applyextra.commons.event.EventOutput;
import applyextra.commons.helper.GraphiteHelper;
import applyextra.commons.orchestration.Action;
import applyextra.operations.dto.ChangeStatusDTO;
import nl.ing.riaf.core.event.ApplicationEvent;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.serviceclient.sia.changestatus2.dto.ChangeStatus2BusinessRequest;
import nl.ing.serviceclient.sia.changestatus2.dto.ChangeStatus2BusinessResponse;
import nl.ing.serviceclient.sia.changestatus2.dto.ChangeStatusReturnCode;
import nl.ing.serviceclient.sia.common.configuration.ServiceWrapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
@Lazy
public class ChangeStatusAction implements Action<ChangeStatusAction.ChangeStatusActionDTO, EventOutput> {
    private static final long DEFAULT_REQUEST_TIMEOUT = 5000;

    private static final String SERVICE_NAME = "ChangeStatus2";
    private static final String CHANGESTATUS_CATEGORY = "sia.changestatus2";

    private static final String SIA_ACTION = "SIA suppliedMove";
    private static final String REQUEST_ID = "RequestId";
    private static final String RETURN_CODE = "ReturnCode";
    private static final String RETURN_MESSAGE = "ReturnMessage";
    private static final String ACCOUNT_NUMBER = "AccountNumber";
    private static final String USER_ID = "UserId";


    private long serviceTimeout = DEFAULT_REQUEST_TIMEOUT;

    @Resource
    private GraphiteHelper graphiteHelper;
    @Resource(name = "ChangeStatus2ServiceClient")
    private ServiceWrapper<ChangeStatus2BusinessRequest, ChangeStatus2BusinessResponse> serviceClient;
    @Resource
    private JNDIUtil jndiUtil;
    @Resource
    private AuditDelegate auditDelegate;

    @PostConstruct
    public void init() {
        serviceTimeout = jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", DEFAULT_REQUEST_TIMEOUT);
    }

    @Override
    public EventOutput perform(ChangeStatusActionDTO flowDTO) {
        final ChangeStatus2BusinessRequest businessRequest = new ChangeStatus2BusinessRequest();
        businessRequest.setUserId("API" + flowDTO.getApplicationId());

        businessRequest.setCreditcardAccountNumber(flowDTO.getChangeStatusDTO().getCreditcardAccountNumber());
        businessRequest.setCardNumber(flowDTO.getChangeStatusDTO().getCreditcardNumber());
        businessRequest.setNewStatusCode(flowDTO.getChangeStatusDTO().getNewStatusCode());
        businessRequest.setNewStatusReason(flowDTO.getChangeStatusDTO().getNewStatusReason());
        businessRequest.setComment(flowDTO.getChangeStatusDTO().getComment());

        final ChangeStatus2BusinessResponse businessResponse;

        try {
            logMessage(flowDTO.getRequestId(), businessRequest, null, WhichWay.OUT);
            businessResponse = serviceClient.invoke(businessRequest, serviceTimeout);

            if (businessResponse == null) {
                throw new ActivityException(SERVICE_NAME, "Call to ChangeStatus2 Failed. Response isOk == false.");
            }

            logMessage(flowDTO.getRequestId(), null, businessResponse, WhichWay.IN);

            ChangeStatusReturnCode responseCode = ChangeStatusReturnCode.fromString(businessResponse.getReturnCode());
            switch (responseCode) {
                case STATUS_CHANGE_SUCCESSFUL:
                    break;
                case NEW_STATUS_EQUALS_CURRENT_STATUS:
                    graphiteHelper.customCounter(CHANGESTATUS_CATEGORY, "statusalreadychanged", 1);
                    break;
                case STATUS_CHANGE_ALREADY_BEING_PROCESSED:
                    graphiteHelper.customCounter(CHANGESTATUS_CATEGORY, "statusalreadybeingchanged", 1);
                    break;
                default:
                    log.error("Functional Error calling ChangeStatus2: " + businessRequest.getCreditcardAccountNumber() + ", Return Code: " +
                            businessResponse.getReturnCode());

                    throw new ActivityException(SERVICE_NAME, "Functional Error in ChangeStatus2");
            }
        } catch (final Exception e) {
            log.error("Technical Error calling ChangeStatus2: " + businessRequest.getCreditcardAccountNumber(),
                    e.getClass().getSimpleName());
            throw new ActivityException(SERVICE_NAME, "Call to change status2 failed", e);
        }

        return EventOutput.success();
    }


    public interface ChangeStatusActionDTO {

        String getRequestId();

        String getApplicationId();

        ChangeStatusDTO getChangeStatusDTO();
    }

    private void logMessage(final String requestId,
                            final ChangeStatus2BusinessRequest request,
                            final ChangeStatus2BusinessResponse response,
                            final WhichWay whichWay) {
        auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, whichWay) {

            @Override
            public void getSpecificFields(Map<String, Object> fields) {

                fields.put(REQUEST_ID, requestId);
                fields.put(SIA_ACTION, SERVICE_NAME);
                if (request != null) {
                    fields.put(USER_ID, request.getUserId());
                    fields.put(ACCOUNT_NUMBER, request.getCreditcardAccountNumber());
                }
                if (response != null) {
                    fields.put(RETURN_CODE, response.getReturnCode());
                    fields.put(RETURN_MESSAGE, response.getReturnMessage());
                }
            }
        });
    }

}
