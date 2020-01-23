package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.serviceclient.sia.common.configuration.ServiceWrapper;
import nl.ing.serviceclient.sia.maintainfullaccount.dto.CreditLineBusinessRequestItem;
import nl.ing.serviceclient.sia.maintainfullaccount.dto.MaintainFullAccountBusinessRequest;
import nl.ing.serviceclient.sia.maintainfullaccount.dto.MaintainFullAccountBusinessResponse;
import nl.ing.serviceclient.sia.maintainfullaccount.dto.ResponseBusinessMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Lazy
@Component
@Slf4j
public class MaintainFullAccountChangeLimitAction extends AbstractEvent {

    public static final long DEFAULT_REQUEST_TIMEOUT = 5000;
    private static final String SERVICE_NAME = "Maintain Full Account";
    private long serviceTimeout = DEFAULT_REQUEST_TIMEOUT;
    @Resource
    private ServiceWrapper<MaintainFullAccountBusinessRequest, MaintainFullAccountBusinessResponse> serviceClient;
    @Resource
    private JNDIUtil jndiUtil;

    @PostConstruct
    public void init() {
        serviceTimeout = jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", DEFAULT_REQUEST_TIMEOUT);
    }

    @Override
    protected EventOutput execution(final Object... eventInput) {

        final MaintainFullAccountActionDTO flowDTO = (MaintainFullAccountActionDTO) eventInput[0];

        final MaintainFullAccountProcessor processor = new MaintainFullAccountProcessor("API" + flowDTO.getApplicationId());
        processor.maintainSingleFullAccount(flowDTO.getAccountNumber(), flowDTO.getNewCreditLimitAmount());

        if (processor.exception != null) {
            throw processor.exception;
        }

        return new EventOutput(EventOutput.Result.SUCCESS);
    }

    private final class MaintainFullAccountProcessor {

        private RuntimeException exception;
        private final String userId;

        private MaintainFullAccountProcessor(final String userId) {
            this.userId = userId;
        }

        private void maintainSingleFullAccount(final String accountNumber, final Long newLimit) {

            final MaintainFullAccountBusinessRequest businessRequest = createBusinessRequest(accountNumber, newLimit);

            MaintainFullAccountBusinessResponse businessResponse = null;

            try {
                businessResponse = serviceClient.invoke(businessRequest, serviceTimeout);
                if (!businessResponse.isOkResponseMessage()) {
                    for (final ResponseBusinessMessage message : businessResponse.getMessageCodes()) {
                        log.error("Error in response from application " + message.getApplicationCode() + "."
                                + businessRequest.getAccountNumber() + ": " + message.getReturnCode() + " ("
                                + message.getReturnMessage() + ")");
                        addException(new ActivityException(SERVICE_NAME, "Error in maintain full account"));
                    }
                }
            } catch (final Exception e) {
                log.error("Error calling maintain full account: " + businessRequest.getAccountNumber(), e);
                addException(e);
            }

        }

        private MaintainFullAccountBusinessRequest createBusinessRequest(final String accountNumber, final Long newLimit) {

            final MaintainFullAccountBusinessRequest businessRequest = new MaintainFullAccountBusinessRequest();
            final CreditLineBusinessRequestItem newCreditLimit = new CreditLineBusinessRequestItem();

            businessRequest.setAccountNumber(accountNumber);
            businessRequest.setUserId(userId);
            newCreditLimit.setCreditLineAmount(newLimit);
            businessRequest.setCreditLineBusinessRequestItem(newCreditLimit);

            return businessRequest;

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
    }

    public interface MaintainFullAccountActionDTO {

        String getAccountNumber();

        String getApplicationId();

        Long getNewCreditLimitAmount();
    }

}
