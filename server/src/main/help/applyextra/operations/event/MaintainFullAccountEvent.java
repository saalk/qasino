package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.audit.impl.WhichWay;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.operations.dto.SIAAccountInformation;
import applyextra.operations.dto.SiaOrchestrationDTO;
import nl.ing.riaf.core.event.ApplicationEvent;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.serviceclient.sia.common.configuration.ServiceWrapper;
import nl.ing.serviceclient.sia.maintainfullaccount.dto.AccountDataBusinessRequestItem;
import nl.ing.serviceclient.sia.maintainfullaccount.dto.MaintainFullAccountBusinessRequest;
import nl.ing.serviceclient.sia.maintainfullaccount.dto.MaintainFullAccountBusinessResponse;
import nl.ing.serviceclient.sia.maintainfullaccount.dto.ResponseBusinessMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Lazy
@Component
@Slf4j
public class MaintainFullAccountEvent extends AbstractEvent {

    public static final long DEFAULT_REQUEST_TIMEOUT = 5000;
    private static final String SERVICE_NAME = "Maintain Full Account";
    private static final String RETURN_SUCCESS = "00000";
    private static final String REQUEST_ID = "RequestId";
    private static final String SIA_ACTION = "SIA action";
    private static final String ACCOUNT_NUMBER = "AccountNumber";
    private static final String PORTFOLIO_CODE = "PortfolioCode";

    private long serviceTimeout = DEFAULT_REQUEST_TIMEOUT;
    @Resource(name = "MaintainFullAccountServiceClient")
    private ServiceWrapper<MaintainFullAccountBusinessRequest, MaintainFullAccountBusinessResponse> serviceClient;
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

        final MaintainFullAccountEventDTO flowDTO = (MaintainFullAccountEventDTO) eventInput[0];

        final MaintainFullAccountProcessor processor = new MaintainFullAccountProcessor("API" + flowDTO.getApplicationId());
        processor.applyRepricing(flowDTO.getSiaOrchestrationDTO(), flowDTO.getRequestId());

        if (processor.exception != null) {
            throw processor.exception;
        }

        return new EventOutput(EventOutput.Result.SUCCESS);
    }

    public interface MaintainFullAccountEventDTO {
        SiaOrchestrationDTO getSiaOrchestrationDTO();
        String getApplicationId();
        String getRequestId();
    }

    private final class MaintainFullAccountProcessor {

        private RuntimeException exception;
        private final String userId;

        private MaintainFullAccountProcessor(final String userId) {
            this.userId = userId;
        }

        private void applyRepricing(final SiaOrchestrationDTO siaOrchestrationDTO, String requestId) {
            for (SIAAccountInformation accountInfo : siaOrchestrationDTO.getAccounts()) {
                maintainSingleFullAccount(accountInfo, requestId);
            }
        }

        private void maintainSingleFullAccount(final SIAAccountInformation accountInfo, final String requestId) {
            final MaintainFullAccountBusinessRequest businessRequest = new MaintainFullAccountBusinessRequest();
            businessRequest.setAccountNumber(accountInfo.getSiaAccountNumber());
            businessRequest.setUserId(userId);
            businessRequest.setAccountDataBusinessRequestItem(new AccountDataBusinessRequestItem(accountInfo.getPortfolioCode()
                    .getPortfolioCode()));

            final MaintainFullAccountBusinessResponse businessResponse;

            try {
                logMessage(requestId, businessRequest, null, WhichWay.OUT);
                businessResponse = serviceClient.invoke(businessRequest, serviceTimeout);
                logMessage(requestId, null, businessResponse, WhichWay.IN);
                if (!businessResponse.isOkResponseMessage()) {
                    for (ResponseBusinessMessage message : businessResponse.getMessageCodes()) {
                        log.error("Error in response from application " +
                                message.getApplicationCode() + "." +
                                businessRequest.getAccountNumber() + ": " +
                                message.getReturnCode() + " (" +
                                message.getReturnMessage() + ")");
                        addException(null); // send null because the method addException would double wrap a ActivityException
                    }
                }
            } catch (final Exception e) {
                log.error("Error calling maintain full account: " + businessRequest.getAccountNumber(), e);
                addException(e);
            }

        }

        private void addException(final Exception inputException) {
            if (!(exception instanceof RIAFRuntimeException)) {
                if (inputException instanceof RIAFRuntimeException) {
                    exception = (RIAFRuntimeException) inputException;
                } else {
                    exception = new ActivityException(SERVICE_NAME, "call to apply repricing failed", inputException);
                }
            }
        }

        private void logMessage(final String requestId,
                                final MaintainFullAccountBusinessRequest request,
                                final MaintainFullAccountBusinessResponse response,
                                final WhichWay whichWay) {
            auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, whichWay) {
                @Override
                public void getSpecificFields(Map<String, Object> fields) {
                    fields.put(SIA_ACTION, SERVICE_NAME);
                    fields.put(REQUEST_ID, requestId);
                    if (request != null) {
                        fields.put(ACCOUNT_NUMBER, request.getAccountNumber());
                        fields.put(PORTFOLIO_CODE, request.getAccountDataBusinessRequestItem().getPortfolioCode());
                    }
                    if (response != null && response.getMessageCodes() != null) {
                        int i = 1;
                        for (ResponseBusinessMessage message : response.getMessageCodes()) {
                            fields.put("Code/Message" + i, message.getReturnCode() + "/" + message.getReturnMessage());
                            i++;
                        }
                    }
                }
            });
        }

    }

}
