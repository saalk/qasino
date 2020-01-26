package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.audit.impl.AuditDelegateHelper;
import applyextra.commons.helper.GraphiteHelper;
import applyextra.commons.orchestration.Action;
import applyextra.commons.resource.ResourceConstants;
import applyextra.commons.util.ConstantsUtil;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.serviceclient.sia.common.configuration.ServiceWrapper;
import nl.ing.serviceclient.sia.inquireaccount2.dto.InquireAccount2BusinessRequest;
import nl.ing.serviceclient.sia.inquireaccount2.dto.InquireAccount2BusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Nimal
 */
@Lazy
@Slf4j
@Component
public class InquireAccountAction implements Action<InquireAccountAction.InquireAccountDTO, InquireAccount2BusinessResponse> {

    private long serviceTimeout;
    @Resource(name = "InquireAccount2ServiceClient")
    private ServiceWrapper<InquireAccount2BusinessRequest, InquireAccount2BusinessResponse> inquireAccount2ServiceClient;
    @Resource
    private AuditDelegateHelper auditDelegateHelper;
    @Resource
    private GraphiteHelper graphiteHelper;
    @Resource
    private JNDIUtil jndiUtil;

    @PostConstruct
    public void init() {
        serviceTimeout = jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", ConstantsUtil.DEFAULT_REQUEST_TIMEOUT);
    }

    @Override
    public InquireAccount2BusinessResponse perform(final InquireAccountAction.InquireAccountDTO flowDto) {
        InquireAccount2BusinessRequest businessRequest = new InquireAccount2BusinessRequest();
        InquireAccount2BusinessResponse tmpResponse;
        businessRequest.setAccountNumber(flowDto.getSiaAccountNumber());
        try {
             tmpResponse = inquireAccount2ServiceClient.invoke(businessRequest, serviceTimeout);
            if(tmpResponse == null || !tmpResponse.getResponseMessage().getReturnCode().equals(ResourceConstants.SUCCESS_CODE_INQUIRE_ACCOUNT)){
                graphiteHelper.customCounter(ConstantsUtil.INQUIREACCOUNT_COUNTER, ConstantsUtil.FAILURE_REQUESTS_COUNTER, 1);
                throw new Exception("No known response  or error response received from InquireAccount for messageId "+flowDto.getMessageId());
            } else {
                auditDelegateHelper.logMessage(ConstantsUtil.SERVICE_NAME_INQUIRE_ACCOUNT, "Action successful for the messageId",flowDto.getMessageId());
                graphiteHelper.customCounter(ConstantsUtil.INQUIREACCOUNT_COUNTER, ConstantsUtil.SUCCESS_REQUESTS_COUNTER, 1);
                return tmpResponse;
            }
        } catch (final RIAFRuntimeException e) {
            throw e; // will be caught and dealt with further up the chain
        } catch (final Exception e) {
            log.error("problem invoking execution of "+ConstantsUtil.SERVICE_NAME_INQUIRE_ACCOUNT+" for the messageId=\""+flowDto.getMessageId()+"\"", e);
            throw new ActivityException(ConstantsUtil.SERVICE_NAME_INQUIRE_ACCOUNT, " call to InquireAccount2ServiceClient failed for messageId "+flowDto.getMessageId(), e);
        }
    }

    public interface InquireAccountDTO {
        String getSiaAccountNumber();
        String getMessageId();
    }
}
