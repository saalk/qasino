package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.audit.impl.WhichWay;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.RepricingProgram;
import applyextra.operations.dto.SIAAccountInformation;
import applyextra.operations.dto.SiaOrchestrationDTO;
import nl.ing.riaf.core.event.ApplicationEvent;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.serviceclient.sia.applyrepricing.dto.ApplyRepricingBusinessRequest;
import nl.ing.serviceclient.sia.applyrepricing.dto.ApplyRepricingBusinessResponse;
import nl.ing.serviceclient.sia.applyrepricing.dto.RepricingRequestItem;
import nl.ing.serviceclient.sia.common.configuration.ServiceWrapper;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@Lazy
@Component
@Slf4j
public class ApplyRepricingEvent extends AbstractEvent {
    public static final long DEFAULT_REQUEST_TIMEOUT = 5000;
    private static final String SERVICE_NAME = "Apply Repricing";
    private static final String RETURN_SUCCESS = "00000";
    private static final String SIA_ACTION = "SIA action";
    private static final String REQUEST_ID = "RequestId";
    private static final String RETURN_CODE = "ReturnCode";
    private static final String RETURN_MESSAGE = "ReturnMessage";
    private static final String ACCOUNT_NUMBER = "AccountNumber";
    private static final String USER_ID = "UserId";

    private long serviceTimeout = DEFAULT_REQUEST_TIMEOUT;

    @Resource (name = "ApplyRepricingServiceClient")
    private ServiceWrapper<ApplyRepricingBusinessRequest, ApplyRepricingBusinessResponse> applyRepricingServiceClient;
    @Resource
    private JNDIUtil jndiUtil;
    @Resource
    private AuditDelegate auditDelegate;

    @PostConstruct
    public void init() {
        serviceTimeout = jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", DEFAULT_REQUEST_TIMEOUT);
    }

    @Override
    protected EventOutput execution(final Object... eventInput) {
        final ApplyRepricingEventDTO flowDTO = (ApplyRepricingEventDTO) eventInput[0];

        final ApplyRepricingProcessor processor = new ApplyRepricingProcessor("API" + flowDTO.getApplicationId());
        processor.applyRepricing(flowDTO.getSiaOrchestrationDTO(), flowDTO.getRequestId());

        if (processor.exception != null) {
            throw processor.exception;
        }

        return EventOutput.success();
    }

    public interface ApplyRepricingEventDTO {
        SiaOrchestrationDTO getSiaOrchestrationDTO();

        String getApplicationId();

        String getRequestId();
    }


    private final class ApplyRepricingProcessor {

        private RuntimeException exception = null;
        private final String userId;

        private ApplyRepricingProcessor(final String userId) {
            this.userId = userId;
        }

        private void applyRepricing(final SiaOrchestrationDTO siaOrchestrationDTO, String requestId) {
            for (SIAAccountInformation accountInfo : siaOrchestrationDTO.getAccounts()) {
                applyRepricingToAccount(accountInfo, siaOrchestrationDTO.getRepricingStartDate(), requestId);
            }
        }

        private void applyRepricingToAccount(final SIAAccountInformation accountInfo,
                                             final DateTime repricingStartDate,
                                             final String requestId) {
            final ApplyRepricingBusinessRequest businessRequest = new ApplyRepricingBusinessRequest();
            businessRequest.setAccountNumber(accountInfo.getSiaAccountNumber());
            businessRequest.setRepricingRequestItems(new ArrayList<RepricingRequestItem>());
            businessRequest.setUserId(userId);
            for (RepricingProgram program : accountInfo.getRepricingPrograms()) {
                businessRequest.getRepricingRequestItems().add(
                        // Do not set repricingStartDate. Will cause issue @Sia when something goes wrong and reprocessing.
                        new RepricingRequestItem(program.getRepricingProgram(), repricingStartDate));
            }

            final ApplyRepricingBusinessResponse businessResponse;
            try {
                logMessage(requestId, businessRequest, null, WhichWay.OUT);

                businessResponse =
                        applyRepricingServiceClient.invoke(businessRequest, serviceTimeout);
                if (businessResponse == null) {
                    throw new Exception("No response received from ApplyRepricing");
                }
                logMessage(requestId, null, businessResponse, WhichWay.IN);
                if (!RETURN_SUCCESS.equals(businessResponse.getReturnCode())) {
                    log.error("Error calling apply repricing: " + businessRequest.getAccountNumber() + ", Return Code: " +
                            businessResponse.getReturnCode());
                    addException(new ActivityException(SERVICE_NAME, "Error in apply repricing"));
                }
            } catch (final Exception e) {
                log.error("Error calling apply repricing: " + businessRequest.getAccountNumber(), e);
                addException(e);
            }
        }

        private void addException(final Exception e) {
            if (!(exception instanceof RIAFRuntimeException)) {
                if (e instanceof RIAFRuntimeException) {
                    exception = (RIAFRuntimeException) e;
                } else {
                    exception = new ActivityException(SERVICE_NAME, "call to apply repricing failed", e);
                }
            }
        }


        private void logMessage(final String requestId,
                                final ApplyRepricingBusinessRequest request,
                                final ApplyRepricingBusinessResponse response,
                                final WhichWay whichWay) {
            auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, whichWay) {
                @Override
                public void getSpecificFields(Map<String, Object> fields) {
                    fields.put(REQUEST_ID, requestId);
                    fields.put(SIA_ACTION, SERVICE_NAME);
                    if (request != null) {
                        fields.put(ACCOUNT_NUMBER, request.getAccountNumber());
                        fields.put(USER_ID, request.getUserId());
                    }
                    if (response != null) {
                        fields.put(RETURN_CODE, response.getReturnCode());
                        fields.put(RETURN_MESSAGE, response.getReturnMessage());
                    }
                }
            });
        }
    }
}
