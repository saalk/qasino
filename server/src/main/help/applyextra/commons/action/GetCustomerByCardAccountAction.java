package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.audit.impl.AuditDelegateHelper;
import applyextra.commons.helper.GraphiteHelper;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.ConstantsUtil;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.core.util.JNDIUtil;
import nl.ing.serviceclient.sia.common.configuration.ServiceWrapper;
import nl.ing.serviceclient.sia.siaGetCustomerByCardAccount1.dto.SIAGetCustomerByCardAccountBusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Praveena Biyyam on 6-4-2018
 */
@Lazy
@Slf4j
@Component
public class GetCustomerByCardAccountAction implements Action<GetCustomerByCardAccountAction.GetCustomerByCardAccountDTO, String>{

    private long serviceTimeout;
    @Resource(name = "GetCustomerByCardAccountServiceClient")
    private ServiceWrapper<String, SIAGetCustomerByCardAccountBusinessResponse> getCustomerByCardAccountServiceClient;
    @Resource
    private JNDIUtil jndiUtil;
    @Resource
    private AuditDelegateHelper auditDelegateHelper;
    @Resource
    private GraphiteHelper graphiteHelper;

    @PostConstruct
    public void init() {
        serviceTimeout = jndiUtil.getJndiValueWithDefault("param/services/sia/timeout", ConstantsUtil.DEFAULT_REQUEST_TIMEOUT);
    }

    public String perform(final GetCustomerByCardAccountDTO flowDto) {
        final String accountNumber = flowDto.getSiaAccountNumber();
        String customerNumber;
        try {
            customerNumber = getCustomerNumberFromSIA(accountNumber, flowDto.getMessageId());
            if(customerNumber != null){
                log.info("Customer number found from SIA: " + customerNumber.substring(customerNumber.length()-4) + " for the messageId=\""+flowDto.getMessageId()+"\"");
                return customerNumber;
            }else {
                log.warn("Customer number not found @SIA for the messageId=\""+flowDto.getMessageId()+"\""+", hence customer details will not be updated : ", accountNumber);
                return null;
            }
        } catch (final RIAFRuntimeException e) {
            throw e; // will be caught and dealt with further up the chain
        } catch (final RuntimeException e) {
            log.error("problem invoking "+ConstantsUtil.GET_CUSTOMER_BY_CARD_SERVICE+" execution for the messageId=\""+flowDto.getMessageId()+"\"", e);
            throw new ActivityException(getCustomerByCardAccountServiceClient.getClass().getSimpleName(), "getCustomerByCardAccountService call failed for the message "+flowDto.getMessageId(), e);
        }
    }

    private String getCustomerNumberFromSIA(String accountNumber, String messageId) {
        final SIAGetCustomerByCardAccountBusinessResponse getCustomerByCardAccountBusinessResponse;
        getCustomerByCardAccountBusinessResponse = getCustomerByCardAccountServiceClient.invoke(accountNumber, serviceTimeout);
        if(getCustomerByCardAccountBusinessResponse != null){
            if("00001".equalsIgnoreCase(getCustomerByCardAccountBusinessResponse.getReturnCode()) && getCustomerByCardAccountBusinessResponse.getCustomerId() != null){
                auditDelegateHelper.logMessage(ConstantsUtil.GET_CUSTOMER_BY_CARD_SERVICE, "Move successful for the messageId", messageId);
                graphiteHelper.customCounter(ConstantsUtil.GETCUSTOMER_BY_CARD_COUNTER, ConstantsUtil.SUCCESS_REQUESTS_COUNTER, 1);
                return getCustomerByCardAccountBusinessResponse.getCustomerId().toString();
            } else {
                log.warn("Error while fetching customer number from "+ConstantsUtil.GET_CUSTOMER_BY_CARD_SERVICE+" for the messageId=\""+messageId+"\" with the error "+getCustomerByCardAccountBusinessResponse.getReturnMessage());
                graphiteHelper.customCounter(ConstantsUtil.GETCUSTOMER_BY_CARD_COUNTER, ConstantsUtil.FAILURE_REQUESTS_COUNTER, 1);
                return null;
            }
        } else {
            log.warn("Error while fetching customer number from "+ConstantsUtil.GET_CUSTOMER_BY_CARD_SERVICE+" & business response is null for the messageId=\""+messageId+"\"");
            return null;
        }
    }

    public interface GetCustomerByCardAccountDTO{
        String getSiaAccountNumber();
        String getMessageId();
    }
}
